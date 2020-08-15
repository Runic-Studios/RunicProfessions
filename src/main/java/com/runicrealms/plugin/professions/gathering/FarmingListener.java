package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Listener for Farming (Gathering Profession)
 * adds blocks material type and coordinates to yml file
 * for the ProfManager to respawn at intervals, checks name of
 * WG region for "farm" to perform tasks
 */
public class FarmingListener implements Listener {

    @EventHandler
    public void onResourceBreak(BlockBreakEvent e) {

        // grab the player, location
        Player pl = e.getPlayer();
        Location plLoc = pl.getLocation();

        // grab all regions the player is standing in
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(plLoc));
        Set<ProtectedRegion> regions = set.getRegions();

        if (regions == null) return;

        boolean canFarm = false;

        // check the region for the keyword 'farm'
        // ignore the rest of this event if the player cannot mine
        for (ProtectedRegion region : regions) {
            if (region.getId().contains("farm")) {
                canFarm = true;
            }
        }

        if (!canFarm) {
            //pl.sendMessage(ChatColor.RED + "You can't harvest this here.");
            e.setCancelled(true);
            return;
        }

        if (e.getBlock().getType() == Material.AIR) return;
        if (pl.getGameMode() == GameMode.CREATIVE) return;

        double chance = ThreadLocalRandom.current().nextDouble(0, 100);

        Block block = e.getBlock();
        Location loc = block.getLocation().add(0.5, 0, 0.5);
        Material oldType = block.getType();

        Material placeHolderType;
        Material itemType;
        String holoString;
        String itemName;
        String desc;

        switch (block.getType()) {
            case WHEAT:
                placeHolderType = Material.AIR;
                itemType = Material.WHEAT;
                holoString = "+ Wheat";
                itemName = "Wheat";
                desc = "Raw Material";
                break;
            case CARROTS:
                placeHolderType = Material.AIR;
                itemType = Material.CARROT;
                holoString = "+ Carrot";
                itemName = "Carrot";
                desc = "Raw Material";
                break;
            case POTATOES:
                placeHolderType = Material.AIR;
                itemType = Material.POTATO;
                holoString = "+ Potato";
                itemName = "Potato";
                desc = "Raw Material";
                break;
            case MELON:
                placeHolderType = Material.AIR;
                itemType = Material.MELON_SLICE;
                holoString = "+ Melon";
                itemName = "Melon Slice";
                desc = "Raw Material";
                break;
            default:
                return;
        }

        e.setCancelled(true);

        if (pl.getInventory().getItemInMainHand().getType() == Material.AIR) {
            pl.sendMessage(ChatColor.RED + "You need a harvesting tool to do that!");
            return;
        }

        // make sure player has harvesting tool
        // this will always be a hoe, so we check for the staff enum
        // we also ensure it has durability 100, arbitrarily chosen.
        ItemStack heldItem = pl.getInventory().getItemInMainHand();
        int slot = pl.getInventory().getHeldItemSlot();
        ItemMeta meta = pl.getInventory().getItemInMainHand().getItemMeta();
        int durability = ((Damageable) Objects.requireNonNull(meta)).getDamage();

        if (heldItem.getType() != Material.IRON_HOE) {
            pl.sendMessage(ChatColor.RED + "You need a harvesting tool to do that!");
            return;
        } else if (heldItem.getType() == Material.IRON_HOE
                && durability != 1
                && durability != 2
                && durability != 3
                && durability != 4
                && durability != 5) {
            pl.sendMessage(ChatColor.RED + "You need a harvesting tool to do that!");
            return;
        }

        // reduce items durability
        double itemDurab = AttributeUtil.getCustomDouble(heldItem, "durability");
        heldItem = AttributeUtil.addCustomStat(heldItem, "durability", itemDurab-1);
        GatheringUtil.generateToolLore(heldItem, durability);
        if (itemDurab - 1 <= 0) {

            pl.playSound(pl.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
            pl.sendMessage(ChatColor.RED + "Your tool broke!");
            pl.getInventory().setItem(slot, null);
        } else {
            pl.getInventory().setItem(slot, heldItem);
        }

        GatheringUtil.gatherMaterial(pl, loc, block, placeHolderType, itemType, holoString,
                itemName, desc, "You fail to gather any resources.", chance, durability);
        RunicProfessions.getProfManager().getBlocksToRestore().put(block.getLocation(), oldType);
    }
}
