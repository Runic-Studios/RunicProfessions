package com.runicrealms.plugin.professions.shop;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.api.NpcClickEvent;
import com.runicrealms.plugin.common.util.GUIUtil;
import com.runicrealms.plugin.item.shops.RunicShop;
import com.runicrealms.runicitems.RunicItemsAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class JewelRemoverListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST) // first
    public void onNpcClick(NpcClickEvent event) {
        if (!JewelRemover.SCRAPPER_NPC_IDS.contains(event.getNpc().getId())) return;
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
        JewelRemover jewelRemover = new JewelRemover(event.getPlayer());
        RunicCore.getRunicShopManager().getPlayersInShops().put(event.getPlayer().getUniqueId(), jewelRemover);
        event.getPlayer().openInventory(jewelRemover.getInventoryHolder().getInventory());
    }

    /**
     * Handles logic for the shop menus
     */
    @EventHandler
    public void onShopClick(InventoryClickEvent event) {

                /*
        Preliminary checks
         */
        if (event.getClickedInventory() == null) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof JewelRemover.JewelRemoverHolder))
            return;
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;
        JewelRemover.JewelRemoverHolder jewelRemoverHolder =
                (JewelRemover.JewelRemoverHolder) event.getClickedInventory().getHolder();
        if (!event.getWhoClicked().equals(jewelRemoverHolder.getPlayer())) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) return;
        if (jewelRemoverHolder.getInventory().getItem(event.getRawSlot()) == null) return;
        JewelRemover jewelRemover =
                (JewelRemover) RunicCore.getRunicShopManager().getPlayersInShops().get(player.getUniqueId());

        ItemStack item = event.getCurrentItem();
        Material material = item.getType();

        if (!JewelRemover.USABLE_SLOTS.contains(event.getRawSlot())) { // event.getClickedInventory()
            // .equals(event.getView().getTopInventory()) &&
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            event.setCancelled(true);
        }

        if (material == GUIUtil.CLOSE_BUTTON.getType())
            player.closeInventory();
        else if (material == JewelRemover.checkMark().getType())
            jewelRemover.removeGemsFromItems(player);
    }

    /**
     * Add items back to player inventory to prevent item loss from shops
     */
    @EventHandler(priority = EventPriority.HIGHEST) // last
    public void onShopClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (RunicCore.getRunicShopManager().getPlayersInShops().get(player.getUniqueId()) == null)
            return;
        RunicShop shop = RunicCore.getRunicShopManager().getPlayersInShops().get(player.getUniqueId());
        String title = ChatColor.translateAlternateColorCodes('&', shop.getName());
        if (shop instanceof JewelRemover && title.equals(event.getView().getTitle())) {
            for (Integer slot : JewelRemover.USABLE_SLOTS) {
                ItemStack itemStack = event.getInventory().getItem(slot);
                if (itemStack == null) continue;
                if (((JewelRemover) shop).getStoredItems().get(player.getUniqueId()).contains(itemStack))
                    continue;
                RunicItemsAPI.addItem(player.getInventory(), itemStack, player.getLocation());
            }
            RunicCore.getRunicShopManager().getPlayersInShops().remove(player.getUniqueId());
        }
    }
}
