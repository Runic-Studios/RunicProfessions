package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.item.shops.RunicItemShop;
import com.runicrealms.plugin.item.shops.RunicShopItem;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class NewHunterShop implements RunicItemShop {
    //refactor to HunterShop.java
    public NewHunterShop() {
        RunicCoreAPI.registerRunicItemShop(this);
    }

    @Override
    public Map<Integer, RunicShopItem> getContents() {
        return null;
    }

    @Override
    public int getShopSize() {
        return 0;
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public Collection<Integer> getNpcIds() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
