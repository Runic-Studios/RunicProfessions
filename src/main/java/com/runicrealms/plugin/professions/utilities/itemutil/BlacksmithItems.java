package com.runicrealms.plugin.professions.utilities.itemutil;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.LoreGenerator;
import com.runicrealms.plugin.item.RunicItem;
import net.minecraft.server.v1_13_R2.Item;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlacksmithItems {

    // level 1
    private static final int WHETSTONE_DAMAGE_BONUS = 1;
    public static ItemStack whetStone() {
        ItemStack sharpStone = new ItemStack(Material.FLINT);
        ItemMeta meta = sharpStone.getItemMeta();
        List<String> lore = new ArrayList<>();
        sharpStone = AttributeUtil.addCustomStat(sharpStone, "custom.weaponDamageBonus", WHETSTONE_DAMAGE_BONUS);
        lore.add("");
        lore.add(ChatColor.YELLOW + "Increase your weapon⚔ damage by +" + WHETSTONE_DAMAGE_BONUS + " for 3 min!");
        lore.add("");
        lore.add(ChatColor.GRAY + "Consumable");
        Objects.requireNonNull(meta).setLore(lore);
        meta.setDisplayName(ChatColor.WHITE + "Whetstone");
        sharpStone.setItemMeta(meta);
        return sharpStone;
    }

    // level 5
    public static ItemStack oakenShield() {
        return new RunicItem
                (Material.SHIELD, ChatColor.WHITE, "Oaken Shield", 0, 5,
                        0, 0, 0, 0, 0, 4,
                        0, 0, "").getItem();
    }

    // level 10
    public static ItemStack ironBroadsword() {
        return new RunicItem
                (Material.WOODEN_SWORD, ChatColor.WHITE, "Iron Broadsword", 6, 10,
                        0, 0, 0, 0, 0, 0,
                        7, 8, "").getItem();
    }

    // level 10
    public static ItemStack ironReaver() {
        return new RunicItem
                (Material.WOODEN_AXE, ChatColor.WHITE, "Iron Reaver", 6, 10,
                        0, 0, 0, 0, 0, 0,
                        5, 8, "").getItem();
    }
}
