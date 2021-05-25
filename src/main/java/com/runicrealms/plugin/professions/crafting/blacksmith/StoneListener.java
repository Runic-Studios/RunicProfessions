package com.runicrealms.plugin.professions.crafting.blacksmith;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.professions.utilities.itemutil.BlacksmithItems;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItem;
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

    private static final String DATA_DAMAGE_STRING = "damage-buff"; // used in the runic item config
    private static final String DATA_DURATION_STRING = "duration";
    HashMap<UUID, Integer> boostedPlayers;

    public StoneListener() {
        boostedPlayers = new HashMap<>();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if (e.getItem() == null) return;
        if (e.getItem().getType() != Material.FLINT) return;
        if (e.getItem().getItemMeta() == null) return;
        if (!e.getItem().getItemMeta().getDisplayName().equals(BlacksmithItems.WHETSTONE.getDisplayableItem().getDisplayName())
                && !e.getItem().getItemMeta().getDisplayName().equals(BlacksmithItems.SHARPENING_STONE.getDisplayableItem().getDisplayName()))
            return;

        Player pl = e.getPlayer();
        ItemStack stone = e.getItem();

        RunicItem runicItem = RunicItemsAPI.getRunicItemFromItemStack(e.getItem());
        String bonus = runicItem.getData().get(DATA_DAMAGE_STRING);
        String duration = runicItem.getData().get(DATA_DURATION_STRING);
        int durationInt = Integer.parseInt(duration);
        Bukkit.broadcastMessage(bonus);
        Bukkit.broadcastMessage(duration);

        ItemRemover.takeItem(pl, stone, 1);
        boostedPlayers.put(pl.getUniqueId(), Integer.valueOf(bonus));
        pl.playSound(pl.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 2.0f);
        pl.sendMessage
                (
                        ChatColor.GREEN + "You consumed a " +
                        runicItem.getDisplayableItem().getDisplayName() +
                        ChatColor.GREEN + "! Your attacks now deal " +
                        bonus + " additional damage for "
                        + (durationInt / 60) + " minutes."
                );

        new BukkitRunnable() {
            @Override
            public void run() {
                boostedPlayers.remove(pl.getUniqueId());
                e.getPlayer().sendMessage(ChatColor.GRAY + "Your attacks no longer deal additional damage.");
            }
        }.runTaskLater(RunicProfessions.getInstance(), durationInt * 20L);
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        if (!boostedPlayers.containsKey(e.getPlayer().getUniqueId())) return;
        double bonus = boostedPlayers.get(e.getPlayer().getUniqueId());
        e.setAmount((int) (e.getAmount() + bonus));
        e.getEntity().getWorld().spawnParticle(Particle.CRIT, e.getEntity().getLocation(), 10, 0, 0, 0, 0);
    }
}
