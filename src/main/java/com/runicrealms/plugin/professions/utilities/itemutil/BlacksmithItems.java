package com.runicrealms.plugin.professions.utilities.itemutil;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.RunicItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
                        0, 0, 0, 0, 2,
                        25, "offhand").getItem();
    }

    // level 10
    public static ItemStack ironBroadsword() {
        return new RunicItem
                (Material.WOODEN_SWORD, ChatColor.WHITE, "Forged Iron Broadsword", 6, 10,
                        0, 0, 0, 0, 0,
                        7, 8, "").getItem();
    }
    public static ItemStack ironReaver() {
        return new RunicItem
                (Material.WOODEN_AXE, ChatColor.WHITE, "Forged Iron Reaver", 6, 10,
                        0, 0, 0, 0, 0,
                        5, 8, "").getItem();
    }

    // level 15
    public static ItemStack mailGreaves() {
        return new RunicItem
                (Material.CHAINMAIL_BOOTS, ChatColor.WHITE, "Forged Mail Greaves", 0, 15,
                        0, 3, 0, 0, 0,
                        15, "feet").getItem();
    }
    public static ItemStack gildedBoots() {
        return new RunicItem
                (Material.GOLDEN_BOOTS, ChatColor.WHITE, "Forged Gilded Boots", 0, 15,
                        0, 0, 2, 0, 0,
                        20, "feet").getItem();
    }

    // level 20
    public static ItemStack mailTassets() {
        return new RunicItem
                (Material.CHAINMAIL_LEGGINGS, ChatColor.WHITE, "Forged Mail Tassets", 0, 20,
                        0, 2, 0, 0, 1,
                        25, "legs").getItem();
    }
    public static ItemStack plateLegs() {
        return new RunicItem
                (Material.IRON_LEGGINGS, ChatColor.WHITE, "Forged Iron Platelegs", 0, 20,
                        0, 0, 0, 0, 2,
                        40, "legs").getItem();
    }

    // level 25
    private static final int SHARP_STONE_DAMAGE_BONUS = 3;
    public static ItemStack sharpStone() {
        ItemStack sharpStone = new ItemStack(Material.FLINT);
        ItemMeta meta = sharpStone.getItemMeta();
        List<String> lore = new ArrayList<>();
        sharpStone = AttributeUtil.addCustomStat(sharpStone, "custom.weaponDamageBonus", WHETSTONE_DAMAGE_BONUS);
        lore.add("");
        lore.add(ChatColor.YELLOW + "Increase your weapon⚔ damage by +" + SHARP_STONE_DAMAGE_BONUS + " for 3 min!");
        lore.add("");
        lore.add(ChatColor.GRAY + "Consumable");
        Objects.requireNonNull(meta).setLore(lore);
        meta.setDisplayName(ChatColor.WHITE + "Sharpening Stone");
        sharpStone.setItemMeta(meta);
        return sharpStone;
    }

    // level 30
    public static ItemStack bastion() {
        return new RunicItem
                (Material.SHIELD, ChatColor.WHITE, "Bastion", 0, 25,
                        0, 0, 0, 0, 4,
                        50, "offhand").getItem();
    }

    // level 35
    public static ItemStack ironLongbow() {
        return new RunicItem
                (Material.BOW, ChatColor.WHITE, "Forged Iron Longbow", 6, 35,
                        0, 0, 0, 0, 0,
                        17, 21, "").getItem();
    }
    public static ItemStack ironScepter() {
        return new RunicItem
                (Material.WOODEN_HOE, ChatColor.WHITE, "Forged Iron Scepter", 6, 35,
                        0, 0, 0, 0, 3,
                        14, 19, "").getItem();
    }

    // level 40
    public static ItemStack gildedBody() {
        return new RunicItem
                (Material.GOLDEN_CHESTPLATE, ChatColor.WHITE, "Forged Gilded Body", 6, 40,
                        0, 0, 0, 3, 0,
                        50, "chest").getItem();
    }
    public static ItemStack plateBody() {
        return new RunicItem
                (Material.IRON_CHESTPLATE, ChatColor.WHITE, "Forged Iron Platebody", 6, 40,
                        0, 0, 0, 0, 0,
                        50, "chest").getItem();
    }

    // level 45
    public static ItemStack mailHelm() {
        return new RunicItem
                (Material.CHAINMAIL_HELMET, ChatColor.WHITE, "Forged Mail Helm", 6, 45,
                        0, 0, 0, 3, 0,
                        50, "head").getItem();
    }
    public static ItemStack ironHelm() {
        return new RunicItem
                (Material.IRON_HELMET, ChatColor.WHITE, "Forged Iron Helm", 6, 45,
                        0, 0, 0, 0, 0,
                        50, "head").getItem();
    }

    // level 50 - legendary archer
    public static ItemStack scorpion() {
        return new RunicItem
                (Material.BOW, ChatColor.GOLD, "Scorpion", 7, 50,
                        0, 0, 0, 0, 0,
                        50, 50, "").getItem();
    }

    // level 55 - legendary rogue
    public static ItemStack nightshade() {
        return new RunicItem
                (Material.WOODEN_SWORD, ChatColor.GOLD, "Nightshade", 7, 50,
                        0, 0, 0, 0, 0,
                        50, 50, "").getItem();
    }

    // level 60 - legendary warrior
    public static ItemStack warmonger() {
        return new RunicItem
                (Material.WOODEN_AXE, ChatColor.GOLD, "Warmonger", 7, 50,
                        0, 0, 0, 0, 0,
                        50, 50, "").getItem();
    }
}
