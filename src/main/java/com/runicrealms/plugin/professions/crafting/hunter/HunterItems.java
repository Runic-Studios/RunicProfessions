package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItemGeneric;
import org.bukkit.inventory.ItemStack;

public class HunterItems {

    public static final RunicItemGeneric SHADOWMELD_POTION = (RunicItemGeneric) RunicItemsAPI.generateItemFromTemplate("shadowmeld-potion");
    public static final ItemStack SHADOWMELD_POTION_ITEMSTACK = SHADOWMELD_POTION.generateItem();

    // scroll
    // orb (eye of ender)
    // tracking scroll
    // tracking compass
}
