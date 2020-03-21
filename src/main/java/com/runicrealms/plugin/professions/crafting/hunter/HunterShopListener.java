package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.GUIMenu.OptionClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@SuppressWarnings("deprecation")
public class HunterShopListener implements Listener {

    /**
     * Handles logic for the shop menus
     */
    @EventHandler
    public void onShopClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player pl = (Player) e.getWhoClicked();
        if (RunicCore.getShopManager().getPlayerShop(pl) == null) return;
        if (!(RunicCore.getShopManager().getPlayerShop(pl) instanceof HunterShop)) return;
        HunterShop shop = (HunterShop) RunicCore.getShopManager().getPlayerShop(pl);
        String title = ChatColor.translateAlternateColorCodes('&', shop.getTitle());

        // verify custom GUI
        if (title.equals(e.getInventory().getTitle())) {

            int slot = e.getRawSlot();

            // shop gui
            if (e.getClickedInventory() != null && e.getClickedInventory().getTitle().equals(title)) {
                e.setCancelled(true);
                e.setResult(Event.Result.DENY);
            }

            /*
            Call custom ItemGUI logic
             */
            ItemGUI itemGUI = shop.getItemGUI();
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
