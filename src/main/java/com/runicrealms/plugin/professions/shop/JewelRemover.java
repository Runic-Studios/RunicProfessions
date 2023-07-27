package com.runicrealms.plugin.professions.shop;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.common.util.ColorUtil;
import com.runicrealms.plugin.common.util.GUIUtil;
import com.runicrealms.plugin.common.util.Pair;
import com.runicrealms.plugin.item.shops.RunicShop;
import com.runicrealms.plugin.runicitems.RunicItemsAPI;
import com.runicrealms.plugin.runicitems.item.RunicItem;
import com.runicrealms.plugin.runicitems.item.RunicItemArmor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Item Menu to scrap items
 */
public class JewelRemover implements RunicShop {
    public static final Collection<Integer> SCRAPPER_NPC_IDS =
            Arrays.asList(157, 158, 159, 160, 162, 163, 165, 166, 167, 169);
    // of them
    public static final Collection<Integer> USABLE_SLOTS = Arrays.asList(10, 11, 12, 13, 14);
    private static final int PRICE = 32;
    private static final int SHOP_SIZE = 27;
    private static final String SHOP_NAME = ChatColor.YELLOW + "Jewel Remover";
    private final InventoryHolder inventoryHolder;
    private final HashMap<UUID, List<ItemStack>> storedItems; // list of items NOT to return

    public JewelRemover(Player player) {
        this.inventoryHolder = new JewelRemoverHolder(player, SHOP_SIZE, SHOP_NAME);
        storedItems = new HashMap<>();
        List<ItemStack> items = new ArrayList<>();
        storedItems.put(player.getUniqueId(), items);
    }

    public static ItemStack checkMark() {
        ItemStack item = new ItemStack(Material.SLIME_BALL, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ColorUtil.format("&aRemove Gems"));
        meta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Remove all gems in this item!",
                "",
                ChatColor.GOLD + "Price:",
                ChatColor.GOLD + "- " + ChatColor.GREEN + ChatColor.BOLD + PRICE + ChatColor.GOLD +
                        " Gold Coin"));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public int getShopSize() {
        return SHOP_SIZE;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.STONE);
    }

    @Override
    public String getName() {
        return SHOP_NAME;
    }

    @Override
    public Collection<Integer> getRunicNpcIds() {
        return SCRAPPER_NPC_IDS;
    }

    @Override
    public InventoryHolder getInventoryHolder() {
        return inventoryHolder;
    }

    public HashMap<UUID, List<ItemStack>> getStoredItems() {
        return storedItems;
    }

    /**
     * This method reads the items in the first seven slots of the menu,
     * removes them, and then decides how much gold to dish out.
     *
     * @param player to give gold to
     */
    public void removeGemsFromItems(Player player) {
        Inventory inventory = this.getInventoryHolder().getInventory();
        boolean placedValidItem = false; // if ANY of the items are valid

        // loop through items
        for (Integer slot : USABLE_SLOTS) {
            if (inventory.getItem(slot) == null) continue;
            ItemStack itemStack = inventory.getItem(slot);
            RunicItem runicItem = RunicItemsAPI.getRunicItemFromItemStack(itemStack);
            if (!(runicItem instanceof RunicItemArmor runicItemArmor)) continue;
            storedItems.get(player.getUniqueId()).add(itemStack);
            runicItemArmor.getGems().clear(); // Remove all gems
            ItemStack newItem = runicItemArmor.generateItem();
            RunicItemsAPI.addItem(player.getInventory(), newItem);
            placedValidItem = true;
        }

        if (placedValidItem) {
            List<Pair<String, Integer>> requiredItems = new ArrayList<>();
            requiredItems.add(Pair.pair("coin", PRICE));
            boolean result = RunicCore.getShopAPI().checkItemRequirement(player, requiredItems, "",
                    true);
            if (result) {
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5f, 1.0f);
                player.sendMessage(ChatColor.GREEN + "You removed all gems from your items!");
            }
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
            player.sendMessage(ChatColor.GRAY + "Place an armor piece inside the menu to remove " +
                    "all gems!");
        }

        player.closeInventory();
    }

    /**
     * Creates an inventory holder which makes the listener easy to set up
     */
    static class JewelRemoverHolder implements InventoryHolder {

        private final Inventory inventory;
        private final Player player;

        public JewelRemoverHolder(Player player, int size, String title) {
            this.inventory = Bukkit.createInventory(this, size, title);
            this.player = player;
            setupInventory();
        }

        @NotNull
        @Override
        public Inventory getInventory() {
            return this.inventory;
        }

        public Player getPlayer() {
            return this.player;
        }

        /**
         * Opens the inventory associated w/ this GUI, ordering perks
         */
        private void setupInventory() {
            this.inventory.clear();
            this.inventory.setItem(0, GUIUtil.BACK_BUTTON);
            for (int i = 0; i < SHOP_SIZE; i++) {
                if (USABLE_SLOTS.contains(i)) continue; // skip scrapper slots
                this.inventory.setItem(i, GUIUtil.BORDER_ITEM);
            }
            this.inventory.setItem(16, checkMark());
            this.inventory.setItem(17, GUIUtil.CLOSE_BUTTON);
        }
    }
}
