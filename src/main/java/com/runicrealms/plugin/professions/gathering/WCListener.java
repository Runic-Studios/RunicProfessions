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
 * Listener for Woodcutting (Gathering Profession)
 * adds blocks material type and coordinates to yml file
 * for the ProfManager to respawn at intervals, checks name of
 * WG region for "grove" to perform tasks
 */
public class WCListener implements Listener {

    @EventHandler
    public void onResourceBreak(BlockBreakEvent e) {

        // grab the player, location
        Player pl = e.getPlayer();
        Location blockLoc = e.getBlock().getLocation();

        // grab all regions the block is in
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(blockLoc));
        Set<ProtectedRegion> regions = set.getRegions();

        if (regions == null) return;

        boolean canChop = false;

        // check the region for the keyword 'grove'
        // ignore the rest of this event if the player cannot mine
        for (ProtectedRegion region : regions) {
            if (region.getId().contains("grove")) {
                canChop = true;
            }
        }

        if (!canChop) {
            //pl.sendMessage(ChatColor.RED + "You can't chop this here.");
            e.setCancelled(true);
            return;
        }

        if (e.getBlock().getType() == null) return;
        if (pl.getGameMode() == GameMode.CREATIVE) return;

        double chance = ThreadLocalRandom.current().nextDouble(0, 100);

        Block block = e.getBlock();
        Location loc = block.getLocation().add(0.5, 0, 0.5);
        Material oldType = block.getType();

        Material placeHolderType;
        Material itemType;
        String holoString;
        String itemName;
        String desc = "Raw Material";

        switch (block.getType()) {
            // woodcutting
            case OAK_WOOD:
                placeHolderType = Material.OAK_PLANKS;
                itemType = Material.OAK_LOG;
                holoString = "+ Oak";
                itemName = "Oak Log";
                break;
            case SPRUCE_WOOD:
                placeHolderType = Material.SPRUCE_PLANKS;
                itemType = Material.SPRUCE_LOG;
                holoString = "+ Spruce";
                itemName = "Spruce Log";
                break;
            case BIRCH_WOOD:
                placeHolderType = Material.BIRCH_PLANKS;
                itemType = Material.BIRCH_LOG;
                holoString = "+ Elder";
                itemName = "Elder Log";
                break;
            case JUNGLE_WOOD:
                placeHolderType = Material.JUNGLE_PLANKS;
                itemType = Material.JUNGLE_LOG;
                holoString = "+ Jungle";
                itemName = "Jungle Log";
                break;
            case ACACIA_WOOD:
                placeHolderType = Material.ACACIA_PLANKS;
                itemType = Material.ACACIA_LOG;
                holoString = "+ Acacia";
                itemName = "Acacia Log";
                break;
            case DARK_OAK_WOOD:
                placeHolderType = Material.DARK_OAK_PLANKS;
                itemType = Material.DARK_OAK_LOG;
                holoString = "+ Dark Oak";
                itemName = "Dark Oak Log";
                break;
            default:
                return;
        }

        e.setCancelled(true);

        if (pl.getInventory().getItemInMainHand().getType() == Material.AIR) {
            pl.sendMessage(ChatColor.RED + "You need a woodcutting axe to do that!");
            return;
        }

        // make sure player has harvesting tool, iron axe of durability 1, 2, 3, 4, or 5, corresponding to the tier.
        // with durability magic, a durability 1 iron axe will display as wood, 5 as diamond, etc.
        ItemStack heldItem = pl.getInventory().getItemInMainHand();
        int slot = pl.getInventory().getHeldItemSlot();
        ItemMeta meta = heldItem.getItemMeta();
        int durability = ((Damageable) Objects.requireNonNull(meta)).getDamage();

        if (heldItem.getType() != Material.IRON_AXE) {
            pl.sendMessage(ChatColor.RED + "You need a woodcutting axe to do that!");
            return;
        } else if (heldItem.getType() == Material.IRON_AXE
                && durability != 1
                && durability != 2
                && durability != 3
                && durability != 4
                && durability != 5) {
            pl.sendMessage(ChatColor.RED + "You need a woodcutting axe to do that!");
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

        // gather the material
        GatheringUtil.gatherMaterial(pl, loc, block,
                placeHolderType, itemType, holoString, itemName, desc,
                "You fail to gather any resources.", chance, durability);
        RunicProfessions.getProfManager().getBlocksToRestore().put(block.getLocation(), oldType);
    }
}
