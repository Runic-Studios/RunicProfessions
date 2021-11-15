package com.runicrealms.plugin.professions.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * This listener disables the vanilla functionality of: anvils, brewing stands, cauldrons,
 * crafting tables,  furnaces.
 */
public class VanillaStationListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL) // after checking for workstation
    public void onVanillaStationUse(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (!e.hasBlock()) return;
        if (e.getClickedBlock() == null) return;
        Block block = e.getClickedBlock();
        if (blockedStations().contains(block.getType()))
            e.setCancelled(true);
    }

    private Set<Material> blockedStations() {
        Set<Material> blockedStations = new HashSet<>();
        blockedStations.add(Material.ANVIL);
        blockedStations.add(Material.CHIPPED_ANVIL);
        blockedStations.add(Material.DAMAGED_ANVIL);
        blockedStations.add(Material.BREWING_STAND);
        blockedStations.add(Material.CAULDRON);
        blockedStations.add(Material.CRAFTING_TABLE);
        blockedStations.add(Material.ENCHANTING_TABLE);
        blockedStations.add(Material.FURNACE);
        blockedStations.add(Material.LECTERN);
        blockedStations.add(Material.HOPPER);
        blockedStations.add(Material.LOOM);
        blockedStations.add(Material.BARREL);
        blockedStations.add(Material.SMOKER);
        blockedStations.add(Material.BLAST_FURNACE);
        blockedStations.add(Material.CARTOGRAPHY_TABLE);
        blockedStations.add(Material.FLETCHING_TABLE);
        blockedStations.add(Material.GRINDSTONE);
        blockedStations.add(Material.SMITHING_TABLE);
        blockedStations.add(Material.STONECUTTER);
        return blockedStations;

    }
}
