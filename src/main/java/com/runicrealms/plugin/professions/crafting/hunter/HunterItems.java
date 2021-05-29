package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItemGeneric;
import org.bukkit.inventory.ItemStack;

public class HunterItems {

    public static final RunicItemGeneric SHADOWMELD_POTION = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("shadowmeld-potion");
    public static final ItemStack SHADOWMELD_POTION_ITEMSTACK = SHADOWMELD_POTION.generateItem();

    public static final RunicItemGeneric TELEPORT_OUTLAW_GUILD = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("teleport-scroll-outlaw-guild");
    public static final ItemStack TELEPORT_OUTLAW_GUILD_ITEMSTACK = TELEPORT_OUTLAW_GUILD.generateItem();

    public static final RunicItemGeneric SCRYING_ORB = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("scrying-orb");
    public static final ItemStack SCRYING_ORB_ITEMSTACK = SCRYING_ORB.generateItem();

    public static final RunicItemGeneric TRACKING_SCROLL = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("shadowmeld-potion");
    public static final ItemStack TRACKING_SCROLL_ITEMSTACK = TRACKING_SCROLL.generateItem();

    public static final RunicItemGeneric TRACKING_COMPASS = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("shadowmeld-potion");
    public static final ItemStack TRACKING_COMPASS_ITEMSTACK = TRACKING_COMPASS.generateItem();

    public static final RunicItemGeneric HUNTER_CHEST_ARCHER = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("hunter-shop-archer-chest");
    public static final ItemStack HUNTER_CHEST_ARCHER_ITEMSTACK = HUNTER_CHEST_ARCHER.generateItem();

    public static final RunicItemGeneric HUNTER_CHEST_CLERIC = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("hunter-shop-cleric-chest");
    public static final ItemStack HUNTER_CHEST_CLERIC_ITEMSTACK = HUNTER_CHEST_CLERIC.generateItem();

    public static final RunicItemGeneric HUNTER_CHEST_MAGE = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("hunter-shop-mage-chest");
    public static final ItemStack HUNTER_CHEST_MAGE_ITEMSTACK = HUNTER_CHEST_MAGE.generateItem();

    public static final RunicItemGeneric HUNTER_CHEST_ROGUE = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("hunter-shop-rogue-chest");
    public static final ItemStack HUNTER_CHEST_ROGUE_ITEMSTACK = HUNTER_CHEST_ROGUE.generateItem();

    public static final RunicItemGeneric HUNTER_CHEST_WARRIOR = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("hunter-shop-warrior-chest");
    public static final ItemStack HUNTER_CHEST_WARRIOR_ITEMSTACK = HUNTER_CHEST_WARRIOR.generateItem();
}
