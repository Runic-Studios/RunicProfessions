package com.runicrealms.plugin.professions.crafting.jeweler;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.GUIMenu.OptionClickEvent;
import com.runicrealms.plugin.item.shops.Shop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class JewelShopListener implements Listener {

    /**
     * Handles logic for the shop menus
     */
    @EventHandler
    public void onShopClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player pl = (Player) e.getWhoClicked();
        if (RunicCore.getShopManager().getPlayerShop(pl) == null) return;
        if (!(RunicCore.getShopManager().getPlayerShop(pl) instanceof JewelMaster)) return;
        JewelMaster shop = (JewelMaster) RunicCore.getShopManager().getPlayerShop(pl);
        String title = ChatColor.translateAlternateColorCodes('&', shop.getTitle());

        // verify custom GUI
        if (title.equals(e.getView().getTitle())) {

            int slot = e.getRawSlot();

            // shop gui
            if (e.getView().getTitle().equals(title)) {
                if (e.getClickedInventory() != null && e.getClickedInventory() instanceof PlayerInventory) {
                    e.setCancelled(false);
                    e.setResult(Event.Result.ALLOW);
                } else {
                    if (slot > 0) {
                        e.setCancelled(true);
                        e.setResult(Event.Result.DENY);
                    }
                }
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

    /**
     * Drop items to prevent item loss from shops
     */
    @EventHandler
    public void onShopClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;
        Player pl = (Player) e.getPlayer();
        if (RunicCore.getShopManager().getPlayerShop(pl) == null) return;
        Shop shop = RunicCore.getShopManager().getPlayerShop(pl);
        String title = ChatColor.translateAlternateColorCodes('&', shop.getTitle());
        if (shop instanceof JewelMaster && title.equals(e.getView().getTitle())) {
            for (int i = 0; i < 1; i++) {
                if (e.getInventory().getItem(i) == null) continue;
                ItemStack itemStack = e.getInventory().getItem(i);
                if (((JewelMaster) shop).getStoredItems().get(pl.getUniqueId()).contains(itemStack)) continue;
                if (e.getPlayer().getInventory().firstEmpty() != -1) {
                    e.getPlayer().getInventory().addItem(itemStack);
                } else {
                    e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), itemStack);
                }
            }
        }
    }
}
