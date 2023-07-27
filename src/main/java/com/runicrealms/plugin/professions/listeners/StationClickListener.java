package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.GUIMenu.OptionClickEvent;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StationClickListener implements Listener {

    /**
     * Handles logic for the workstation menus
     */
    @EventHandler
    public void onWorkstationClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (RunicProfessions.getAPI().getPlayerWorkstation(player) == null) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;

        Workstation station = RunicProfessions.getAPI().getPlayerWorkstation(player);
        String stationTitle = ChatColor.translateAlternateColorCodes('&', station.getTitle());
        if (event.getView().getTitle().equals(stationTitle)) {

            event.setCancelled(true);
            event.setResult(Event.Result.DENY);

            ItemGUI itemGUI = station.getItemGUI();
            int slot = event.getSlot();
            if (slot > -1 && slot < itemGUI.getSize()) { //  && itemGUI.getOptionNames()[slot] != null && slot >= 0 &&

                OptionClickEvent ope = new OptionClickEvent(event, (Player) event.getWhoClicked(), slot, itemGUI.getOptionNames()[slot]);
                itemGUI.getHandler().onOptionClick(ope);

                if (ope.willClose()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RunicProfessions.getInstance(), player::closeInventory, 1);
                }
                if (ope.willDestroy()) {
                    itemGUI.destroy();
                }
            }
        }
    }
}
