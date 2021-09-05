package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.item.shops.RunicShopGeneric;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import com.runicrealms.plugin.professions.utilities.GatheringUtil;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class GatheringShopFactory {

    public GatheringShopFactory() {
        getFarmerShop();
        getFisherShop();
        getLumberjackShop();
        getMinerShop();
    }

    public RunicShopGeneric getFarmerShop() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<>();
        shopItems.add(new RunicShopItem(10, "Coin", GatheringUtil.GATHERING_HOE_APPRENTICE_ITEMSTACK));
        shopItems.add(new RunicShopItem(45, "Coin", GatheringUtil.GATHERING_HOE_ADEPT_ITEMSTACK));
        shopItems.add(new RunicShopItem(100, "Coin", GatheringUtil.GATHERING_HOE_REFINED_ITEMSTACK));
        shopItems.add(new RunicShopItem(250, "Coin", GatheringUtil.GATHERING_HOE_MASTER_ITEMSTACK));
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Farmer", Arrays.asList(211, 235, 242, 255, 266, 277, 281), shopItems);
    }

    public RunicShopGeneric getFisherShop() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<>();
        shopItems.add(new RunicShopItem(10, "Coin", GatheringUtil.GATHERING_ROD_APPRENTICE_ITEMSTACK));
        shopItems.add(new RunicShopItem(45, "Coin", GatheringUtil.GATHERING_ROD_ADEPT_ITEMSTACK));
        shopItems.add(new RunicShopItem(100, "Coin", GatheringUtil.GATHERING_ROD_REFINED_ITEMSTACK));
        shopItems.add(new RunicShopItem(250, "Coin", GatheringUtil.GATHERING_ROD_MASTER_ITEMSTACK));
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Fisher", Arrays.asList(203, 209, 236, 253, 260, 502, 273, 322), shopItems);
    }

    public RunicShopGeneric getLumberjackShop() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<>();
        shopItems.add(new RunicShopItem(10, "Coin", GatheringUtil.GATHERING_AXE_APPRENTICE_ITEMSTACK));
        shopItems.add(new RunicShopItem(45, "Coin", GatheringUtil.GATHERING_AXE_ADEPT_ITEMSTACK));
        shopItems.add(new RunicShopItem(100, "Coin", GatheringUtil.GATHERING_AXE_REFINED_ITEMSTACK));
        shopItems.add(new RunicShopItem(250, "Coin", GatheringUtil.GATHERING_AXE_MASTER_ITEMSTACK));
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Lumberjack", Arrays.asList(206, 506, 238, 532, 332, 271, 402, 275, 530, 533, 326, 331), shopItems);
    }

    public RunicShopGeneric getMinerShop() {
        LinkedHashSet<RunicShopItem> shopItems = new LinkedHashSet<>();
        shopItems.add(new RunicShopItem(10, "Coin", GatheringUtil.GATHERING_PICKAXE_APPRENTICE_ITEMSTACK));
        shopItems.add(new RunicShopItem(45, "Coin", GatheringUtil.GATHERING_PICKAXE_ADEPT_ITEMSTACK));
        shopItems.add(new RunicShopItem(100, "Coin", GatheringUtil.GATHERING_PICKAXE_REFINED_ITEMSTACK));
        shopItems.add(new RunicShopItem(250, "Coin", GatheringUtil.GATHERING_PICKAXE_MASTER_ITEMSTACK));
        return new RunicShopGeneric(9, ChatColor.YELLOW + "Miner", Arrays.asList(251, 252, 250, 264, 265, 269, 503, 324), shopItems);
    }
}
