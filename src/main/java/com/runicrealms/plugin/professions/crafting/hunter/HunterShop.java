package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.item.shops.RunicItemRunnable;
import com.runicrealms.plugin.item.shops.RunicItemShop;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import com.runicrealms.plugin.utilities.ColorUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class HunterShop implements RunicItemShop {

    private static final int ORB_PRICE = 5;
    private static final int POTION_PRICE = 15;
    private static final int TELEPORT_SCROLL_PRICE = 25;
    private static final int SCROLL_PRICE = 50;
    private static final int COMPASS_PRICE = 1000;
    private static final int ARMOR_PRICE = 150;
    private static final int LOAD_DELAY = 10;
    private Map<Integer, RunicShopItem> availableItems;

    public HunterShop() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RunicCore.getInstance(), () -> {
            availableItems = new HashMap<>();
            int nextItemIndex = 0;
            try {

                RunicShopItem scryingOrb = new RunicShopItem(0, "Coin",
                        iconWithLore(HunterItems.SCRYING_ORB_ITEMSTACK, ORB_PRICE), runShopBuy(HunterItems.SCRYING_ORB_ITEMSTACK, ORB_PRICE));
                scryingOrb.setRemovePayment(false);
                availableItems.put(nextItemIndex++, scryingOrb);

                RunicShopItem shadowmeldPotion = new RunicShopItem(0, "Coin",
                        iconWithLore(HunterItems.SHADOWMELD_POTION_ITEMSTACK, POTION_PRICE), runShopBuy(HunterItems.SHADOWMELD_POTION_ITEMSTACK, POTION_PRICE));
                shadowmeldPotion.setRemovePayment(false);
                availableItems.put(nextItemIndex++, shadowmeldPotion);

                RunicShopItem teleportScroll = new RunicShopItem(0, "Coin",
                        iconWithLore(HunterItems.TELEPORT_OUTLAW_GUILD_ITEMSTACK, TELEPORT_SCROLL_PRICE), runShopBuy(HunterItems.TELEPORT_OUTLAW_GUILD_ITEMSTACK, TELEPORT_SCROLL_PRICE));
                teleportScroll.setRemovePayment(false);
                availableItems.put(nextItemIndex++, teleportScroll);

                RunicShopItem trackingScroll = new RunicShopItem(0, "Coin",
                        iconWithLore(HunterItems.TRACKING_SCROLL_ITEMSTACK, SCROLL_PRICE), runShopBuy(HunterItems.TRACKING_SCROLL_ITEMSTACK, SCROLL_PRICE));
                trackingScroll.setRemovePayment(false);
                availableItems.put(nextItemIndex++, trackingScroll);

                RunicShopItem trackingCompass = new RunicShopItem(0, "Coin",
                        iconWithLore(HunterItems.TRACKING_COMPASS_ITEMSTACK, COMPASS_PRICE), runShopBuy(HunterItems.TRACKING_COMPASS_ITEMSTACK, COMPASS_PRICE));
                trackingCompass.setRemovePayment(false);
                availableItems.put(nextItemIndex, trackingCompass);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().info(ChatColor.DARK_RED + "Error: runic item template id not found!");
            }
            nextItemIndex = 9;
            nextItemIndex = loadRunicArmor(nextItemIndex, "hunter-shop-archer-chest");
            nextItemIndex = loadRunicArmor(nextItemIndex, "hunter-shop-cleric-chest");
            nextItemIndex = loadRunicArmor(nextItemIndex, "hunter-shop-mage-chest");
            nextItemIndex = loadRunicArmor(nextItemIndex, "hunter-shop-rogue-chest");
            loadRunicArmor(nextItemIndex, "hunter-shop-warrior-chest");
            RunicCoreAPI.registerRunicItemShop(this);
        }, LOAD_DELAY * 20L);
    }

    /**
     * Handy method for loading runic items from their template id.
     * @param nextItemIndex the index to start adding armor vertically
     * @param templateIds the string id of the armor piece
     * @return the next index to start adding items
     */
    private int loadRunicArmor(int nextItemIndex, String... templateIds) {
        int temp = nextItemIndex;
        for (String s : templateIds) {
            try {
                ItemStack itemStack = RunicItemsAPI.generateItemFromTemplate(s).generateItem();
                availableItems.put(nextItemIndex, new RunicShopItem(0, "Coin",
                        iconWithLore(itemStack, ARMOR_PRICE), runShopBuy(itemStack, ARMOR_PRICE)));
                nextItemIndex += 9;
            } catch (Exception e) {
                Bukkit.getLogger().info(ChatColor.DARK_RED + "Error: runic item template id not found!");
                e.printStackTrace();
            }
        }
        return temp + 1;
    }

    @Override
    public Map<Integer, RunicShopItem> getContents() {
        return availableItems;
    }

    /**
     * Size of the shop minus the title row
     * @return size of shop minus title row (smallest size 9)
     */
    @Override
    public int getShopSize() {
        return 18;
    }

    @Override
    public ItemStack getIcon() {
        ItemStack vendorItem = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = vendorItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Hunter Shop");
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Purchase items with hunter points!"));
            vendorItem.setItemMeta(meta);
        }
        return vendorItem;
    }

    /**
     * From RunicNPCS
     * @return ID of NPC in config
     */
    @Override
    public Collection<Integer> getNpcIds() {
        return Collections.singletonList(60); // todo
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Hunter Shop";
    }

    private RunicItemRunnable runShopBuy(ItemStack itemStack, int price) {
        return player -> attemptToBuy(player, itemStack, price);
    }

    private void attemptToBuy(Player player, ItemStack itemStack, int price) {
        HunterPlayer hunterPlayer = RunicProfessions.getHunterCache().getPlayers().get(player.getUniqueId());
        int points = hunterPlayer.getHunterPoints();
        if (points < price) {
            player.sendMessage(ChatColor.RED + "You don't have enough hunter points for this purchase.");
            player.sendMessage(ColorUtil.format("&7You currently have &6&l" + points + " &7hunter points."));
        } else {
            hunterPlayer.setHunterPoints(hunterPlayer.getHunterPoints() - price);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "You've purchased an item with hunter points!");
            // attempt to give player item (does not drop on floor)
            player.getInventory().addItem(itemStack);
        }

    }

    private ItemStack iconWithLore(ItemStack is, int price) {
        ItemStack iconWithLore = is.clone();
        ItemMeta meta = iconWithLore.getItemMeta();
        if (meta != null && meta.getLore() != null) {
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add(
                    ChatColor.GOLD + "Price: " +
                            ChatColor.GREEN + ChatColor.BOLD +
                            price + " Hunter Points"
            );
            meta.setLore(lore);
            iconWithLore.setItemMeta(meta);
        }
        return iconWithLore;
    }
}
