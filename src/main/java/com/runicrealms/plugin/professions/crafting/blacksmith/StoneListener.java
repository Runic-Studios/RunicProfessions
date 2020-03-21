package com.runicrealms.plugin.professions.crafting.blacksmith;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.professions.utilities.itemutil.BlacksmithItems;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class StoneListener implements Listener {

    HashMap<UUID, Double> boostedPlayers;

    public StoneListener() {
        boostedPlayers = new HashMap<>();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (e.getItem() == null) return;
        if (e.getItem().getType() != Material.FLINT) return;
        if (!e.getItem().isSimilar(BlacksmithItems.whetStone())
                && !e.getItem().isSimilar(BlacksmithItems.sharpStone())) return;

        Player pl = e.getPlayer();
        ItemStack stone = e.getItem();

        double bonus = AttributeUtil.getCustomDouble(stone, "custom.weaponDamageBonus");

        ItemRemover.takeItem(pl, stone, 1);
        boostedPlayers.put(pl.getUniqueId(), bonus);
        pl.playSound(pl.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 2.0f);
        pl.sendMessage(ChatColor.GREEN + "Your weapon is sharpened!");

        new BukkitRunnable() {
            @Override
            public void run() {
                boostedPlayers.remove(pl.getUniqueId());
                e.getPlayer().sendMessage(ChatColor.GRAY + "Your weapon is no longer sharpened.");
            }
        }.runTaskLater(RunicProfessions.getInstance(), 3*60*20);
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        if (!boostedPlayers.containsKey(e.getPlayer().getUniqueId())) return;
        double bonus = boostedPlayers.get(e.getPlayer().getUniqueId());
        e.setAmount((int) (e.getAmount()+bonus));
        e.getEntity().getWorld().spawnParticle(Particle.CRIT, e.getEntity().getLocation(), 10, 0, 0, 0, 0);
    }
}
