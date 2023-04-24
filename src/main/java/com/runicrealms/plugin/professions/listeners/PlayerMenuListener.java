package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicProfessions;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Listens for when player clicks their crafting slots inventory menu to open gathering GUI
 */
public class PlayerMenuListener implements Listener {

    private static final Set<Integer> PLAYER_CRAFTING_SLOTS = new HashSet<>(Arrays.asList(1, 2, 3, 4));

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.CRAFTING) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (event.getClickedInventory().equals(event.getView().getBottomInventory())) return;
        if (!PLAYER_CRAFTING_SLOTS.contains(event.getSlot())) return;
        event.setCancelled(true);
        player.updateInventory();
        if (event.getCursor() == null) return;
        if (event.getCursor().getType() != Material.AIR) return; // prevents clicking with items on cursor
        if (event.getSlot() == 3) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            RunicProfessions.getAPI().openGatheringGUI(player);
        }
    }
}
