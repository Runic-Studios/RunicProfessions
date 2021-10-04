package com.runicrealms.plugin.professions.crafting.cooking;

import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItemGeneric;
import org.bukkit.inventory.ItemStack;

public class CookingItems {

    public static final RunicItemGeneric COOKED_MEAT = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("CookedMeat");
    public static final ItemStack COOKED_MEAT_ITEMSTACK = COOKED_MEAT.generateItem();

    public static final RunicItemGeneric BREAD = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("Bread");
    public static final ItemStack BREAD_ITEMSTACK = BREAD.generateItem();

    public static final RunicItemGeneric COOKED_COD = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("CookedCod");
    public static final ItemStack COOKED_COD_ITEMSTACK = COOKED_COD.generateItem();

    public static final RunicItemGeneric COOKED_SALMON = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("CookedSalmon");
    public static final ItemStack COOKED_SALMON_ITEMSTACK = COOKED_SALMON.generateItem();

    public static final RunicItemGeneric AMBROSIA_STEW = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("ambrosia-stew");
    public static final ItemStack AMBROSIA_STEW_ITEMSTACK = AMBROSIA_STEW.generateItem();
}
