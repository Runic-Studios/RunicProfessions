package com.runicrealms.plugin.professions.utilities.itemutil;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.RunicItem;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItemArmor;
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
        lore.add("");
        lore.add(ChatColor.YELLOW + "Increase your weapon⚔ damage by +" + WHETSTONE_DAMAGE_BONUS + " for 3 min!");
        lore.add("");
        lore.add(ChatColor.GRAY + "Consumable");
        Objects.requireNonNull(meta).setLore(lore);
        meta.setDisplayName(ChatColor.WHITE + "Whetstone");
        sharpStone.setItemMeta(meta);
        sharpStone = AttributeUtil.addCustomStat(sharpStone, "custom.weaponDamageBonus", WHETSTONE_DAMAGE_BONUS);
        return sharpStone;
    }

    // level 5
    public static ItemStack oakenShield() {
        return new RunicItem
                (Material.SHIELD, ChatColor.WHITE, "Oaken Shield", 0, 5,
                        25, 0, 0, 0, 0, 2,
                        "offhand", false).getItem();
    }

    // level 10
    public static ItemStack ironBroadsword() {
        return new RunicItem
                (Material.WOODEN_SWORD, ChatColor.WHITE, "Forged Iron Broadsword", 7, 10,
                        0, 0, 0, 0, 0,
                        7, 8, "", false).getItem();
    }
    public static ItemStack ironReaver() {
        return new RunicItem
                (Material.WOODEN_AXE, ChatColor.WHITE, "Forged Iron Reaver", 7, 10,
                        0, 0, 0, 0, 0,
                        5, 8, "", false).getItem();
    }

    // level 15
    public static ItemStack mailGreaves() {
        return new RunicItem
                (Material.CHAINMAIL_BOOTS, ChatColor.WHITE, "Forged Mail Greaves", 0, 15,
                        15, 0, 3, 0, 0, 0,
                        "feet", false).getItem();
    }
    public static ItemStack gildedBoots() {
        return new RunicItem
                (Material.GOLDEN_BOOTS, ChatColor.WHITE, "Forged Gilded Boots", 0, 15,
                        20, 0, 0, 2, 0, 0,
                        "feet", false).getItem();
    }

    // level 20
    public static ItemStack mailTassets() {
        return new RunicItem
                (Material.CHAINMAIL_LEGGINGS, ChatColor.WHITE, "Forged Mail Tassets", 0, 20,
                        25, 0, 2, 0, 0, 1,
                        "legs", false).getItem();
    }
    public static ItemStack plateLegs() {
        return new RunicItem
                (Material.IRON_LEGGINGS, ChatColor.WHITE, "Forged Iron Platelegs", 0, 20,
                        40, 0, 0, 0, 0, 2,
                        "legs", false).getItem();
    }
    public static ItemStack flailOfRetribution() {
        return new RunicItem
                (Material.STONE_SHOVEL, ChatColor.WHITE, "Flail of Retribution", 0, 20,
                        0, 0, 2, 10, 0, 0,
                        "offhand", false).getItem();
    }
    public static ItemStack illusionersWand() {
        return new RunicItem
                (Material.STONE_HOE, ChatColor.WHITE, "Illusioner's Wand", 0, 20,
                        0, 0, 0, 0, 12, 0,
                        "offhand", false).getItem();
    }
    public static ItemStack etchedDagger() {
        return new RunicItem
                (Material.STONE_SWORD, ChatColor.WHITE, "Etched Dagger", 0, 20,
                        0, 0, 6, 0, 0, 0,
                        "offhand", false).getItem();
    }

    // level 25
    private static final int SHARP_STONE_DAMAGE_BONUS = 3;
    public static ItemStack sharpStone() {
        ItemStack sharpStone = new ItemStack(Material.FLINT);
        ItemMeta meta = sharpStone.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.YELLOW + "Increase your weapon⚔ damage by +" + SHARP_STONE_DAMAGE_BONUS + " for 3 min!");
        lore.add("");
        lore.add(ChatColor.GRAY + "Consumable");
        Objects.requireNonNull(meta).setLore(lore);
        meta.setDisplayName(ChatColor.WHITE + "Sharpening Stone");
        sharpStone.setItemMeta(meta);
        sharpStone = AttributeUtil.addCustomStat(sharpStone, "custom.weaponDamageBonus", SHARP_STONE_DAMAGE_BONUS);
        return sharpStone;
    }

    // level 30
    public static ItemStack bastion() {
        return new RunicItem
                (Material.SHIELD, ChatColor.WHITE, "Bastion", 0, 25,
                        50, 0, 0, 0, 0, 4,
                        "offhand", false).getItem();
    }

    // level 35
    public static ItemStack ironLongbow() {
        return new RunicItem
                (Material.BOW, ChatColor.WHITE, "Forged Iron Longbow", 7, 35,
                        0, 0, 0, 0, 0,
                        17, 21, "", false).getItem();
    }
    public static ItemStack ironScepter() {
        return new RunicItem
                (Material.WOODEN_HOE, ChatColor.WHITE, "Forged Iron Scepter", 7, 35,
                        0, 0, 0, 0, 3,
                        14, 19, "", false).getItem();
    }

    // level 40
    public static ItemStack gildedBody() {
        return new RunicItem
                (Material.GOLDEN_CHESTPLATE, ChatColor.WHITE, "Forged Gilded Body", 0, 40,
                        50, 0, 0, 0, 3, 0,
                        "chest", false).getItem();
    }
    public static ItemStack plateBody() {
        return new RunicItem
                (Material.IRON_CHESTPLATE, ChatColor.WHITE, "Forged Iron Platebody", 0, 40,
                        50, 0, 0, 0, 0, 0,
                        "chest", false).getItem();
    }

    // level 45
    public static ItemStack mailHelm() {
        return new RunicItem
                (Material.CHAINMAIL_HELMET, ChatColor.WHITE, "Forged Mail Helm", 0, 45,
                        50, 0, 0, 0, 3, 0,
                        "head", false).getItem();
    }

    public static final RunicItemArmor FORGED_IRON_HELM = (RunicItemArmor) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-iron-helmet");

    // level 50 - legendary weapons
    public static ItemStack Stormsong() {
        return new RunicItem
                (Material.BOW, ChatColor.GOLD, "Stormsong", 10, 50,
                        0, 0, 0, 0, 0,
                        54, 58, "", false).getItem();
    }
    public static ItemStack Valkyrie() {
        return new RunicItem
                (Material.WOODEN_SWORD, ChatColor.GOLD, "Valkyrie", 10, 50,
                        0, 0, 0, 0, 0,
                        48, 52, "", false).getItem();
    }
    public static ItemStack theMinotaur() {
        return new RunicItem
                (Material.WOODEN_AXE, ChatColor.GOLD, "The Minotaur", 10, 50,
                        0, 0, 0, 0, 0,
                        45, 48, "", false).getItem();
    }

    // level 60 - legendary off-hands
    public static ItemStack bulwark() {
        return new RunicItem
                (Material.SHIELD, ChatColor.GOLD, "Frost Lord's Bulwark", 0, 60,
                        100, 0, 0, 0, 0, 8,
                        "offhand", false).getItem();
    }
    public static ItemStack redeemersFlail() {
        return new RunicItem
                (Material.STONE_SHOVEL, ChatColor.GOLD, "Redeemer's Flail", 0, 60,
                        0, 0, 4, 20, 0, 0,
                        "offhand", false).getItem();
    }
    public static ItemStack icefuryWand() {
        return new RunicItem
                (Material.STONE_HOE, ChatColor.GOLD, "Icefury Wand", 0, 60,
                        0, 0, 0, 0, 24, 0,
                        "offhand", false).getItem();
    }
    public static ItemStack blackSteelDirk() {
        return new RunicItem
                (Material.STONE_SWORD, ChatColor.GOLD, "Black Steel Dirk", 0, 60,
                        0, 0, 12, 0, 0, 0,
                        "offhand", false).getItem();
    }
}
