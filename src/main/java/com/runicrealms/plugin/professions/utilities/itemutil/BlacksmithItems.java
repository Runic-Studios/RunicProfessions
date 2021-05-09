package com.runicrealms.plugin.professions.utilities.itemutil;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.RunicItem;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItemArmor;
import com.runicrealms.runicitems.item.RunicItemOffhand;
import com.runicrealms.runicitems.item.RunicItemWeapon;
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
    public static final RunicItemOffhand OAKEN_SHIELD = (RunicItemOffhand) RunicItemsAPI.generateItemFromTemplate("blacksmith-oaken-shield");

    // level 10
    public static final RunicItemWeapon FORGED_IRON_BROADSWORD = (RunicItemWeapon) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-iron-broadsword");

    public static final RunicItemWeapon FORGED_IRON_REAVER = (RunicItemWeapon) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-iron-reaver");

    // level 15
    public static final RunicItemArmor FORGED_MAIL_GREAVES = (RunicItemArmor) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-mail-greaves");

    public static final RunicItemArmor FORGED_GILDED_BOOTS = (RunicItemArmor) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-gilded-boots");

    // level 20
    public static final RunicItemArmor FORGED_MAIL_TASSEST = (RunicItemArmor) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-mail-tassest");

    public static final RunicItemArmor FORGED_IRON_PLATELEGS = (RunicItemArmor) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-iron-platelegs");

    public static final RunicItemOffhand FLAIL_OF_RETRIBUTION = (RunicItemOffhand) RunicItemsAPI.generateItemFromTemplate("blacksmith-flail-of-retribution");

    public static final RunicItemOffhand ILLUSIONERS_WAND = (RunicItemOffhand) RunicItemsAPI.generateItemFromTemplate("blacksmith-illusioners-wand");

    public static final RunicItemOffhand ETCHED_DAGGER = (RunicItemOffhand) RunicItemsAPI.generateItemFromTemplate("blacksmith-etched-dagger");

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
    public static final RunicItemOffhand BASTION = (RunicItemOffhand) RunicItemsAPI.generateItemFromTemplate("blacksmith-bastion");

    // level 35
    public static final RunicItemWeapon FORGED_IRON_LONGBOW = (RunicItemWeapon) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-iron-longbow");

    public static final RunicItemWeapon FORGED_IRON_SCEPTER = (RunicItemWeapon) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-iron-scepter");

    // level 40
    public static final RunicItemArmor FORGED_GILDED_BODY = (RunicItemArmor) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-gilded-body");

    public static final RunicItemArmor FORGED_IRON_PLATEBODY = (RunicItemArmor) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-iron-platebody");

    // level 45
    public static final RunicItemArmor FORGED_MAIL_HELM = (RunicItemArmor) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-mail-helm");

    public static final RunicItemArmor FORGED_IRON_HELM = (RunicItemArmor) RunicItemsAPI.generateItemFromTemplate("blacksmith-forged-iron-helm");

    // level 50 - legendary weapons
    public static final RunicItemWeapon STORMSONG = (RunicItemWeapon) RunicItemsAPI.generateItemFromTemplate("blacksmith-stormsong");

    public static final RunicItemWeapon VALKYRIE = (RunicItemWeapon) RunicItemsAPI.generateItemFromTemplate("blacksmith-valkyrie");

    public static final RunicItemWeapon THE_MINOTAUR = (RunicItemWeapon) RunicItemsAPI.generateItemFromTemplate("blacksmith-the-minotaur");

    // level 60 - legendary off-hands
    public static final RunicItemOffhand FROST_LORDS_BULWARK = (RunicItemOffhand) RunicItemsAPI.generateItemFromTemplate("blacksmith-frost-lords-bulwark");

    public static final RunicItemOffhand REDEEMERS_FLAIL = (RunicItemOffhand) RunicItemsAPI.generateItemFromTemplate("blacksmith-redeemers-flail");

    public static final RunicItemOffhand ICEFURY_WAND = (RunicItemOffhand) RunicItemsAPI.generateItemFromTemplate("blacksmith-icefury-wand");

    public static final RunicItemOffhand BLACK_STEEL_DIRK = (RunicItemOffhand) RunicItemsAPI.generateItemFromTemplate("blacksmith-black-steel-dirk");
}
