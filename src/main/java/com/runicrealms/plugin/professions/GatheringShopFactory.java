package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.item.shops.RunicShopGeneric;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import com.runicrealms.plugin.professions.utilities.GatheringUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class GatheringShopFactory {

    public GatheringShopFactory() {
//        getFarmerShop();
//        getFisherShop();
        getLumberjackShop();
//        getMinerShop();
    }

//    public RunicShopGeneric getFarmerShop() {
//        LinkedHashMap<ItemStack, RunicShopItem> shopItems = new LinkedHashMap<>();
//        shopItems.put(GatheringUtil.GATHERING_AXE_APPRENTICE_ITEMSTACK, new RunicShopItem(10, "Coin", RunicShopGeneric.iconWithLore(GatheringUtil.GATHERING_AXE_APPRENTICE_ITEMSTACK, 10)));
//        return new RunicShopGeneric(9, ChatColor.YELLOW + "Farmer", Arrays.asList(211, 235, 242, 255, 266, 277, 281), shopItems);
//    }
//
//    public RunicShopGeneric getFisherShop() {
//        LinkedHashMap<ItemStack, RunicShopItem> shopItems = new LinkedHashMap<>();
//        shopItems.put(axeApprentice, new RunicShopItem(10, "Coin", RunicShopGeneric.iconWithLore(axeApprentice, 10)));
//        return new RunicShopGeneric(9, ChatColor.YELLOW + "Fisher", Arrays.asList(203, 209, 236, 253, 260, 502, 273, 322), shopItems);
//    }

    public RunicShopGeneric getLumberjackShop() {
        LinkedHashMap<ItemStack, RunicShopItem> shopItems = new LinkedHashMap<>();
        shopItems.put(GatheringUtil.GATHERING_AXE_APPRENTICE_ITEMSTACK, new RunicShopItem(10, "Coin", RunicShopGeneric.iconWithLore(GatheringUtil.GATHERING_AXE_APPRENTICE_ITEMSTACK, 10)));
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Lumberjack", Arrays.asList(206, 506, 238, 532, 332, 271, 402, 275, 530, 533, 326, 331), shopItems);
    }

//    public RunicShopGeneric getMinerShop() {
//        LinkedHashMap<ItemStack, RunicShopItem> shopItems = new LinkedHashMap<>();
//        shopItems.put(axeApprentice, new RunicShopItem(10, "Coin", RunicShopGeneric.iconWithLore(axeApprentice, 10)));
//        return new RunicShopGeneric(9, ChatColor.YELLOW + "Miner", Arrays.asList(251, 252, 250, 264, 265, 269, 503, 324), shopItems);
//    }
}
