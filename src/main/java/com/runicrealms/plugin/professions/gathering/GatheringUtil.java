package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.utilities.ActionBarUtil;
import com.runicrealms.plugin.utilities.ColorUtil;
import com.runicrealms.plugin.utilities.CurrencyUtil;
import com.runicrealms.plugin.utilities.HologramUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GatheringUtil {

    /**
     *
     * @param item
     * @param tier
     */
    public static void generateToolLore(ItemStack item, int tier) {

        int maxDurab = 0;
        String successRate = "";
        String tierStr = "";
        switch (tier) {
            case 5:
                maxDurab = 800;
                successRate = "&a75";
                tierStr = "&6Legendary";
                break;
            case 4:
                maxDurab = 400;
                successRate = "&a62.5";
                tierStr = "&dEpic";
                break;
            case 3:
                maxDurab = 200;
                successRate = "&e50";
                tierStr = "&bRare";
                break;
            case 2:
                maxDurab = 100;
                successRate = "&c37.5";
                tierStr = "&aUncommon";
                break;
            case 1:
                maxDurab = 50;
                successRate = "&c25";
                tierStr = "&7Common";
                break;
        }

        // grab our material, ItemMeta, ItemLore
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        // build the lore
        String desc = "\n&7Durability: &f" + (int) AttributeUtil.getCustomDouble(item, "durability")
                + "&7/" + maxDurab + "\n&7Success Rate: " + successRate + "%\n\n" + tierStr;
        for (String s : desc.split("\n")) {
            lore.add(ColorUtil.format(s));
        }

        // set other flags
        Objects.requireNonNull(meta).setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        // update lore, meta
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /**
     *
     * @param type
     * @param tier
     * @return
     */
    public static ItemStack getGatheringTool(Material type, int tier) {

        // create gathering tool
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();

        String weapType = "";
        int durability;
        String tierName;

        switch (type) {
            case FISHING_ROD:
                weapType = "Fishing Rod";
                break;
            case IRON_AXE:
                weapType = "Woodcutting Axe";
                break;
            case IRON_HOE:
                weapType = "Farming Hoe";
            break;
            case IRON_PICKAXE:
                weapType = "Mining Pick";
                break;
        }

        switch (tier) {
            case 5:
                tierName = "&6Artisan";
                durability = 800;
                break;
            case 4:
                tierName = "&dMaster";
                durability = 400;
                break;
            case 3:
                tierName = "&bRefined";
                durability = 200;
                break;
            case 2:
                tierName = "&aAdept";
                durability = 100;
                break;
            case 1:
            default:
                tierName = "&7Basic";
                durability = 50;
                break;
        }

        // set the name
        Objects.requireNonNull(meta).setDisplayName(ColorUtil.format(tierName + " " + weapType));

        // complete this stuff first
        ((Damageable) meta).setDamage(tier);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);

        // must update durability stat AFTER setting the item meta
        item = AttributeUtil.addCustomStat(item, "durability", durability);

        // generate the lore
        GatheringUtil.generateToolLore(item, tier);

        return item;
    }

    /**
     *
     * @param pl
     * @param loc
     * @param b
     * @param placeholder
     * @param gathered
     * @param name
     * @param itemName
     * @param desc
     * @param failMssg
     * @param chance
     * @param tier
     */
    public static void gatherMaterial(Player pl, Location loc, Block b,
                                Material placeholder, Material gathered, String name, String itemName,
                                String desc, String failMssg, double chance, int tier) {

        b.setType(placeholder);

        double successRate;
        switch (tier) {
            case 5:
                successRate = 75;
                break;
            case 4:
                successRate = 62.5;
                break;
            case 3:
                successRate = 50;
                break;
            case 2:
                successRate = 37.5;
                break;
            case 1:
            default:
                successRate = 25;
                break;
        }

        if (chance < (100 - successRate)) {
            ActionBarUtil.sendTimedMessage(pl, "&c" + failMssg, 3);
            return;
        }

        // give the player the gathered item
        if (loc.clone().add(0, 1.5, 0).getBlock().getType() == Material.AIR) {
            HologramUtil.createStaticHologram(pl, loc, ChatColor.GREEN + "" + ChatColor.BOLD + name, 0, 2, 0);
        }

        HashMap<Integer, ItemStack> leftOver = pl.getInventory().addItem(GatheringUtil.gatheredItem(gathered, itemName, desc));
        for (ItemStack is : leftOver.values()) {
            pl.getWorld().dropItem(pl.getLocation(), is);
        }

        // give the player a coin
        if (chance >= (95)) {
            b.getWorld().playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);
            HologramUtil.createStaticHologram(pl, loc, ChatColor.GOLD + "" + ChatColor.BOLD + "+ Coin", 0, 1.25, 0);
            if (pl.getInventory().firstEmpty() != -1) {
                pl.getInventory().addItem(CurrencyUtil.goldCoin());
            } else {
                pl.getWorld().dropItem(pl.getLocation(), CurrencyUtil.goldCoin());
            }
        }
    }

    public static ItemStack gatheredItem(Material material, String itemName, String desc) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.WHITE + itemName);
        lore.add(ChatColor.GRAY + desc);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
