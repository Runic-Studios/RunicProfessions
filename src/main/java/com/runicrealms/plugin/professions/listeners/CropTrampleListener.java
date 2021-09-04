package com.runicrealms.plugin.professions.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CropTrampleListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST) // run last
    public void onCropTrample(PlayerInteractEvent e) {
        if (!e.hasBlock() || e.getClickedBlock() == null) return;
        if (e.getAction() != Action.PHYSICAL) return;
        if (e.getClickedBlock().getType() != Material.FARMLAND) return;
        e.setCancelled(true);
    }
}
