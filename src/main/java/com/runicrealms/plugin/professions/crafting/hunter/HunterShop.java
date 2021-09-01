package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.item.shops.RunicItemRunnable;
import com.runicrealms.plugin.item.shops.RunicShopGeneric;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import com.runicrealms.plugin.utilities.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class HunterShop {

    private static final int ORB_PRICE = 5;
    private static final int POTION_PRICE = 15;
    private static final int TELEPORT_SCROLL_PRICE = 25;
    private static final int SCROLL_PRICE = 50;
    private static final int COMPASS_PRICE = 1000;
    private static final int ARMOR_PRICE = 150;
    private static final String HUNTER_POINTS = " Hunter Points";

    public HunterShop() {
        LinkedHashMap<ItemStack, RunicShopItem> shopItems = new LinkedHashMap<>();
        shopItems.put
                (
                        HunterItems.SCRYING_ORB_ITEMSTACK,
                        new RunicShopItem(ORB_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.SCRYING_ORB_ITEMSTACK, ORB_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.SCRYING_ORB_ITEMSTACK, ORB_PRICE))
                );
        shopItems.put
                (
                        HunterItems.SHADOWMELD_POTION_ITEMSTACK,
                        new RunicShopItem(POTION_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.SHADOWMELD_POTION_ITEMSTACK, POTION_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.SHADOWMELD_POTION_ITEMSTACK, POTION_PRICE))
                );
        shopItems.put
                (
                        HunterItems.TELEPORT_OUTLAW_GUILD_ITEMSTACK,
                        new RunicShopItem(TELEPORT_SCROLL_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.TELEPORT_OUTLAW_GUILD_ITEMSTACK, TELEPORT_SCROLL_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.TELEPORT_OUTLAW_GUILD_ITEMSTACK, TELEPORT_SCROLL_PRICE))
                );
        shopItems.put
                (
                        HunterItems.TRACKING_SCROLL_ITEMSTACK,
                        new RunicShopItem(SCROLL_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.TRACKING_SCROLL_ITEMSTACK, SCROLL_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.TRACKING_SCROLL_ITEMSTACK, SCROLL_PRICE))
                );
        shopItems.put
                (
                        HunterItems.TRACKING_COMPASS_ITEMSTACK,
                        new RunicShopItem(COMPASS_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.TRACKING_COMPASS_ITEMSTACK, COMPASS_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.TRACKING_COMPASS_ITEMSTACK, COMPASS_PRICE))
                );
        shopItems.put
                (
                        HunterItems.HUNTER_CHEST_ARCHER_ITEMSTACK,
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.HUNTER_CHEST_ARCHER_ITEMSTACK, ARMOR_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.HUNTER_CHEST_ARCHER_ITEMSTACK, ARMOR_PRICE))
                );
        shopItems.put
                (
                        HunterItems.HUNTER_CHEST_CLERIC_ITEMSTACK,
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.HUNTER_CHEST_CLERIC_ITEMSTACK, ARMOR_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.HUNTER_CHEST_CLERIC_ITEMSTACK, ARMOR_PRICE))
                );
        shopItems.put
                (
                        HunterItems.HUNTER_CHEST_MAGE_ITEMSTACK,
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.HUNTER_CHEST_MAGE_ITEMSTACK, ARMOR_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.HUNTER_CHEST_MAGE_ITEMSTACK, ARMOR_PRICE))
                );
        shopItems.put
                (
                        HunterItems.HUNTER_CHEST_ROGUE_ITEMSTACK,
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.HUNTER_CHEST_ROGUE_ITEMSTACK, ARMOR_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.HUNTER_CHEST_ROGUE_ITEMSTACK, ARMOR_PRICE))
                );
        shopItems.put
                (
                        HunterItems.HUNTER_CHEST_WARRIOR_ITEMSTACK,
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                RunicShopGeneric.iconWithLore(HunterItems.HUNTER_CHEST_WARRIOR_ITEMSTACK, ARMOR_PRICE + HUNTER_POINTS),
                                runShopBuy(HunterItems.HUNTER_CHEST_WARRIOR_ITEMSTACK, ARMOR_PRICE))
                );
        // we're using hunter points, so we disable default currency functionality
        for (ItemStack itemStack : shopItems.keySet()) {
            shopItems.get(itemStack).setRemovePayment(false);
        }
        new RunicShopGeneric(45, ChatColor.YELLOW + "Hunter Shop", Arrays.asList(354, 234, 248, 254, 258, 263, 276, 278, 282, 323), shopItems,
                new int[]{0, 1, 2, 3, 4, 9, 10, 11, 12, 13});
    }

    private RunicItemRunnable runShopBuy(ItemStack itemStack, int price) {
        return player -> attemptToBuy(player, itemStack, price);
    }

    /**
     * Custom runnable that implements hunter point logic
     *
     * @param player    attempting to buy item
     * @param itemStack to be purchased
     * @param price     of the item in hunter points
     */
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
}
