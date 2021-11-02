package com.runicrealms.plugin.professions.crafting.blacksmith;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.runicitems.item.RunicItem;
import com.runicrealms.runicitems.item.event.RunicItemGenericTriggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class StoneListener implements Listener {

    private static final String DATA_DAMAGE_STRING = "damage-buff"; // used in the runic item config
    private static final String DATA_DURATION_STRING = "duration";
    private final HashMap<UUID, Integer> boostedPlayers;

    public StoneListener() {
        boostedPlayers = new HashMap<>();
    }

    @EventHandler
    public void onRunicClickTrigger(RunicItemGenericTriggerEvent e) {
        if (!(e.getItem().getTemplateId().equals(CraftedResource.SHARPSTONE_10.getTemplateId())
                || e.getItem().getTemplateId().equals(CraftedResource.SHARPSTONE_20.getTemplateId())
                || e.getItem().getTemplateId().equals(CraftedResource.SHARPSTONE_30.getTemplateId())))
            return;

        RunicItem runicItem = e.getItem();
        String bonus = runicItem.getData().get(DATA_DAMAGE_STRING);
        String duration = runicItem.getData().get(DATA_DURATION_STRING);
        int durationInt = Integer.parseInt(duration);
        Player pl = e.getPlayer();

        ItemRemover.takeItem(pl, e.getItemStack(), 1);
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(RunicProfessions.getInstance(), () -> {
            boostedPlayers.remove(pl.getUniqueId());
            e.getPlayer().sendMessage(ChatColor.GRAY + "Your attacks no longer deal additional damage.");
        }, durationInt * 20L);
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        if (!e.isAutoAttack()) return;
        if (!boostedPlayers.containsKey(e.getPlayer().getUniqueId())) return;
        double bonus = boostedPlayers.get(e.getPlayer().getUniqueId());
        e.setAmount((int) (e.getAmount() + bonus));
        e.getVictim().getWorld().spawnParticle(Particle.CRIT, e.getVictim().getLocation(), 10, 0, 0, 0, 0);
    }
}
