package com.runicrealms.plugin.professions.crafting.enchanter;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.LoreGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantScroll {

    private double percent;
    private int reqLevel;
    private EnchantEnum enchantEnum;
    private ItemStack item;

    public EnchantScroll(EnchantEnum enchantEnum, double percent, int reqLevel) {
        this.enchantEnum = enchantEnum;
        this.percent = percent;
        this.reqLevel = reqLevel;
        this.buildItem();
    }

    private void buildItem() {
        item = new ItemStack(Material.PURPLE_DYE);
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);
        item = applyStats(item);
        String description = enchantEnum.getDescription().replace("?", (int) percent + "");
        LoreGenerator.generateItemLore(item, ChatColor.WHITE,
                "Enchant Scroll: " + enchantEnum.getName(),
                description, false, "");
        setItem(item);
    }

    private ItemStack applyStats(ItemStack item) {
        if (percent != 0) item = AttributeUtil.addCustomStat(item, "scroll.percent", percent);
        if (reqLevel != 0) item = AttributeUtil.addCustomStat(item, "required.level", reqLevel);
        if (enchantEnum != null) item = AttributeUtil.addCustomStat(item, "scroll.enchantment", enchantEnum.getName());
        return item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
