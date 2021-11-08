package com.runicrealms.plugin.professions.shop;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.item.shops.RunicItemRunnable;
import com.runicrealms.plugin.item.shops.RunicShopGeneric;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import com.runicrealms.plugin.professions.crafting.hunter.HunterItems;
import com.runicrealms.plugin.professions.crafting.hunter.HunterPlayer;
import com.runicrealms.plugin.utilities.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class HunterShop {

    private static final int ORB_PRICE = 5;
    private static final int POTION_PRICE = 15;
    private static final int TELEPORT_SCROLL_PRICE = 25;
    private static final int SCROLL_PRICE = 50;
    private static final int COMPASS_PRICE = 1000;
    private static final int ARMOR_PRICE = 150;
    private static final String HUNTER_POINTS = "Hunter Points";

    public HunterShop() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<>();
        shopItems.add
                (
                        new RunicShopItem(ORB_PRICE, "Coin",
                                HunterItems.SCRYING_ORB_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.SCRYING_ORB_ITEMSTACK, ORB_PRICE))
                );
        shopItems.add
                (
                        new RunicShopItem(POTION_PRICE, "Coin",
                                HunterItems.SHADOWMELD_POTION_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.SHADOWMELD_POTION_ITEMSTACK, POTION_PRICE))
                );
        shopItems.add
                (
                        new RunicShopItem(TELEPORT_SCROLL_PRICE, "Coin",
                                HunterItems.TELEPORT_OUTLAW_GUILD_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.TELEPORT_OUTLAW_GUILD_ITEMSTACK, TELEPORT_SCROLL_PRICE))
                );
        shopItems.add
                (
                        new RunicShopItem(SCROLL_PRICE, "Coin",
                                HunterItems.TRACKING_SCROLL_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.TRACKING_SCROLL_ITEMSTACK, SCROLL_PRICE))
                );
        shopItems.add
                (
                        new RunicShopItem(COMPASS_PRICE, "Coin",
                                HunterItems.TRACKING_COMPASS_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.TRACKING_COMPASS_ITEMSTACK, COMPASS_PRICE))
                );
        shopItems.add
                (
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                HunterItems.HUNTER_CHEST_ARCHER_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.HUNTER_CHEST_ARCHER_ITEMSTACK, ARMOR_PRICE))
                );
        shopItems.add
                (
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                HunterItems.HUNTER_CHEST_CLERIC_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.HUNTER_CHEST_CLERIC_ITEMSTACK, ARMOR_PRICE))
                );
        shopItems.add
                (
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                HunterItems.HUNTER_CHEST_MAGE_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.HUNTER_CHEST_MAGE_ITEMSTACK, ARMOR_PRICE))
                );
        shopItems.add
                (
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                HunterItems.HUNTER_CHEST_ROGUE_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.HUNTER_CHEST_ROGUE_ITEMSTACK, ARMOR_PRICE))
                );
        shopItems.add
                (
                        new RunicShopItem(ARMOR_PRICE, "Coin",
                                HunterItems.HUNTER_CHEST_WARRIOR_ITEMSTACK, HUNTER_POINTS,
                                runShopBuy(HunterItems.HUNTER_CHEST_WARRIOR_ITEMSTACK, ARMOR_PRICE))
                );
        // we're using hunter points, so we disable default currency functionality
        for (RunicShopItem runicShopItem : shopItems) {
            runicShopItem.setRemovePayment(false);
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
