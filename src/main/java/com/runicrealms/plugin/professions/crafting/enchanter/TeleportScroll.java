package com.runicrealms.plugin.professions.crafting.enchanter;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.LoreGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportScroll {

    private int reqLevel;
    private TeleportEnum location;
    private ItemStack item;

    public TeleportScroll(TeleportEnum location, int reqLevel) {
        this.location = location;
        this.reqLevel = reqLevel;
        this.buildItem();
    }

    private void buildItem() {
        item = new ItemStack(Material.PURPLE_DYE);
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);
        item = applyStats(item);
        LoreGenerator.generateItemLore(item, ChatColor.WHITE,
                "Teleport Scroll: " + location.getName(), "", false);
        setItem(item);
    }

    private ItemStack applyStats(ItemStack item) {
        if (reqLevel != 0) item = AttributeUtil.addCustomStat(item, "required.level", reqLevel);
        if (location != null) item = AttributeUtil.addCustomStat(item, "scroll.location", location.getName());
        return item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
