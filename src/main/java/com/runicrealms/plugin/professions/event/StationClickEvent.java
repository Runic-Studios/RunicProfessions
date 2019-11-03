package com.runicrealms.plugin.professions.event;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.GUIMenu.OptionClickEvent;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StationClickEvent implements Listener {

    /**
     * Handles logic for the workstation menus
     */
    @EventHandler
    public void onWorkstationClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player pl = (Player) e.getWhoClicked();
        if (RunicProfessions.getProfManager() == null) return;
        if (RunicProfessions.getProfManager().getPlayerWorkstation(pl) == null) return;
        Workstation station = RunicProfessions.getProfManager().getPlayerWorkstation(pl);

        String stationTitle = ChatColor.translateAlternateColorCodes('&', station.getTitle());
        if (e.getInventory().getTitle().equals(stationTitle)) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            ItemGUI itemGUI = station.getItemGUI();
            int slot = e.getRawSlot();
            if (slot >= 0 && slot < itemGUI.getSize() && itemGUI.getOptionNames()[slot] != null) {

                OptionClickEvent ope = new OptionClickEvent(e, (Player) e.getWhoClicked(), slot, itemGUI.getOptionNames()[slot]);
                itemGUI.getHandler().onOptionClick(ope);

                if (ope.willClose()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(RunicProfessions.getInstance(), pl::closeInventory, 1);
                }
                if (ope.willDestroy()) {
                    itemGUI.destroy();
                }
            }
        }
    }
}
