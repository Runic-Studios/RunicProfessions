package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.event.CustomFishEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class CustomFishListener implements Listener {

    /*
    Lets do this so players cannot game the system
     */
    private final HashSet<Player> currentFishers;

    public CustomFishListener() {
        currentFishers = new HashSet<>();
    }

    @EventHandler
    public void onCustomFish(CustomFishEvent e) {

        if (currentFishers.contains(e.getPlayer())) {
            e.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    currentFishers.remove(e.getPlayer());
                }
            }.runTaskLaterAsynchronously(RunicProfessions.getInstance(), 5L);
            return;
        }

        // pull back fishing rod
        Player pl = e.getPlayer();
        currentFishers.add(pl);
        int currentSlot = pl.getInventory().getHeldItemSlot();
        pl.getInventory().setHeldItemSlot(0);
        new BukkitRunnable() {
            @Override
            public void run() {
                pl.getInventory().setHeldItemSlot(currentSlot);
                currentFishers.remove(pl);
            }
        }.runTaskLaterAsynchronously(RunicProfessions.getInstance(), 5L);
    }
}
