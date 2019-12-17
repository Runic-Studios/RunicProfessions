package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.attributes.AttributeUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class MenuUtil {

    /**
     * Reads all stats on an item and converts them to a string for use in GUI menus
     */
    public static String itemStatsToString(ItemStack item) {

        String s = "";

        // for armor/items
        int health = (int) AttributeUtil.getGenericDouble(item, "generic.maxHealth");
        if (health != 0) s = s.concat(ChatColor.RED + "+ " + health + "❤\n");

        // -------------------------------------------------------------------------------------------
        // for weapons/gemstones/custom boosts
        int minDamage = (int) AttributeUtil.getCustomDouble(item, "custom.minDamage");
        int maxDamage = (int) AttributeUtil.getCustomDouble(item, "custom.maxDamage");
        int customHealth = (int) AttributeUtil.getCustomDouble(item, "custom.maxHealth");
        int manaBoost = (int) AttributeUtil.getCustomDouble(item, "custom.manaBoost");
        double damageBoost = AttributeUtil.getCustomDouble(item, "custom.attackDamage");
        double healingBoost = AttributeUtil.getCustomDouble(item, "custom.healingBoost");
        double magicBoost = AttributeUtil.getCustomDouble(item, "custom.magicDamage");
        double shieldAmt = AttributeUtil.getCustomDouble(item, "custom.shield");
        // -------------------------------------------------------------------------------------------

        if (minDamage != 0 && maxDamage != 0) s = s.concat(ChatColor.RED + "+ " + minDamage + "-" + maxDamage + "⚔\n");
        if (customHealth != 0) s = s.concat(ChatColor.RED + "+ " + customHealth + "❤\n");
        if (manaBoost != 0) s = s.concat(ChatColor.DARK_AQUA + "+ " + manaBoost + "✸\n");
        if (damageBoost != 0) s = s.concat(ChatColor.RED + "+ " + (int) damageBoost + "⚔\n");
        if (healingBoost != 0) s = s.concat(ChatColor.GREEN + "+ " + (int) healingBoost + "✦\n");
        if (magicBoost != 0) s = s.concat(ChatColor.DARK_AQUA + "+ " + (int) magicBoost + "ʔ\n");
        if (shieldAmt != 0) s = s.concat(ChatColor.WHITE + "+ " + (int) shieldAmt + "■\n");

        return s;
    }
}
