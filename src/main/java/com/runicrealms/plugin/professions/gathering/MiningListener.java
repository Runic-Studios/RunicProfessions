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
 * Listener for Mining (Gathering Profession)
 * adds blocks material type and coordinates to yml file
 * for the ProfManager to respawn at intervals, checks name of
 * WG region for "mine" to perform tasks
 */
public class MiningListener implements Listener {

    @EventHandler
    public void onResourceBreak(BlockBreakEvent e) {

        // grab the player, location
        Player pl = e.getPlayer();
        Location blockLoc = e.getBlock().getLocation();

        // grab all regions the player is standing in
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(blockLoc));
        Set<ProtectedRegion> regions = set.getRegions();

        if (regions == null) return;

        boolean canMine = false;

        // check the region for the keyword 'mine'
        // ignore the rest of this event if the player cannot mine
        for (ProtectedRegion region : regions) {
            if (region.getId().contains("mine")) {
                canMine = true;
            }
        }

        if (!canMine) {
            //pl.sendMessage(ChatColor.RED + "You can't mine this here.");
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
            case IRON_ORE:
                placeHolderType = Material.COBBLESTONE;
                itemType = Material.IRON_ORE;
                holoString = "+ Iron";
                itemName = "Iron Ore";
                break;
            case GOLD_ORE:
                placeHolderType = Material.COBBLESTONE;
                itemType = Material.GOLD_ORE;
                holoString = "+ Gold";
                itemName = "Gold Ore";
                break;
            case REDSTONE_ORE:
                placeHolderType = Material.COBBLESTONE;
                itemType = Material.REDSTONE_ORE;
                holoString = "+ Ruby";
                itemName = "Uncut Ruby";
                break;
            case LAPIS_ORE:
                placeHolderType = Material.COBBLESTONE;
                itemType = Material.LAPIS_ORE;
                holoString = "+ Sapphire";
                itemName = "Uncut Sapphire";
                break;
            case NETHER_QUARTZ_ORE:
                placeHolderType = Material.COBBLESTONE;
                itemType = Material.NETHER_QUARTZ_ORE;
                holoString = "+ Opal";
                itemName = "Uncut Opal";
                break;
            case EMERALD_ORE:
                placeHolderType = Material.COBBLESTONE;
                itemType = Material.EMERALD_ORE;
                holoString = "+ Emerald";
                itemName = "Uncut Emerald";
                break;
            case DIAMOND_ORE:
                placeHolderType = Material.COBBLESTONE;
                itemType = Material.DIAMOND_ORE;
                holoString = "+ Diamond";
                itemName = "Uncut Diamond";
                break;
            case IRON_BLOCK:
                placeHolderType = Material.COBBLESTONE;
                itemType = Material.IRON_BLOCK;
                holoString = "+ True Iron";
                itemName = "True Iron";
                break;
            default:
                return;
        }

        e.setCancelled(true);

        if (pl.getInventory().getItemInMainHand().getType() == Material.AIR) {
            pl.sendMessage(ChatColor.RED + "You need a mining pick to do that!");
            return;
        }

        // make sure player has harvesting tool, corresponding to the tier.
        // with durability magic, a durability 1 iron axe will display as wood, 5 as diamond, etc.
        ItemStack heldItem = pl.getInventory().getItemInMainHand();
        int slot = pl.getInventory().getHeldItemSlot();
        ItemMeta meta = heldItem.getItemMeta();
        int durability = ((Damageable) Objects.requireNonNull(meta)).getDamage();

        if (heldItem.getType() != Material.IRON_PICKAXE) {
            pl.sendMessage(ChatColor.RED + "You need a mining pick to do that!");
            return;
        } else if (heldItem.getType() == Material.IRON_PICKAXE
                && durability != 1
                && durability != 2
                && durability != 3
                && durability != 4
                && durability != 5) {
            pl.sendMessage(ChatColor.RED + "You need a mining pick to do that!");
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

        // gather material
        GatheringUtil.gatherMaterial(pl, loc, block, placeHolderType, itemType, holoString,
                itemName, desc, "You fail to gather any resources.", chance, durability);
        RunicProfessions.getProfManager().getBlocksToRestore().put(block.getLocation(), oldType);
    }
}
