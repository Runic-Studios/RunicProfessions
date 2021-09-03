package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.GatheringRegion;
import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.professions.utilities.GatheringUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItem;
import com.runicrealms.runicitems.item.RunicItemDynamic;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Listener for Mining (Gathering Profession)
 * adds blocks material type and coordinates to yml file
 * for the ProfManager to respawn at intervals, checks name of
 * WG region for "mine" to perform tasks
 */
public class MiningListener implements Listener {

    @EventHandler
    public void onResourceBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Location blockLoc = e.getBlock().getLocation();
        if (!RunicProfessionsAPI.isInGatheringRegion(GatheringRegion.MINE, blockLoc)) return;
        double chance = ThreadLocalRandom.current().nextDouble();
        Block block = e.getBlock();
        Location loc = block.getLocation().add(0.5, 0, 0.5);
        Material oldType = block.getType();

        // ensure the proper type of block is being mined
        GatheringUtil.GatheringReagentWrapper gatheringReagentWrapper = buildGatheringReagentWrapper(oldType);
        if (gatheringReagentWrapper == null) return;
        String templateId = gatheringReagentWrapper.getTemplateId();
        Material placeHolderType = gatheringReagentWrapper.getBlockPlaceholderType();
        String holoString = gatheringReagentWrapper.getHologramDisplayString();
        e.setCancelled(true);
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        // verify the player is holding a tool
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You need a mining pick to do that!");
            return;
        }

        // todo: right tools
        // verify held tool is a woodcutting axe
        RunicItem runicItem = RunicItemsAPI.getRunicItemFromItemStack(heldItem);
        String templateIdHeldItem = runicItem.getTemplateId();
        if (GatheringUtil.getWoodcuttingAxes().stream().noneMatch(item -> item.getTemplateId().equals(templateIdHeldItem))) {
            player.sendMessage(ChatColor.RED + "You need a mining pick to do that!");
            return;
        }

        // reduce tool durability
        RunicItemDynamic woodcuttingAxe = (RunicItemDynamic) runicItem;
        GatheringUtil.reduceGatheringToolDurability(player, woodcuttingAxe);

        // gather the material
        GatheringUtil.gatherMaterial(player, woodcuttingAxe, templateId, loc, block, placeHolderType, holoString, chance);
        RunicProfessions.getProfManager().getBlocksToRestore().put(block.getLocation(), oldType);
    }

    /**
     * Builds out a handy wrapper for matching the runic item template id and changing the hologram display and placeholder block types
     * If null, will stop the listener (prevents breaking other kinds of blocks)
     *
     * @param material of the block being mined
     * @return a wrapper with a material and a string
     */
    private GatheringUtil.GatheringReagentWrapper buildGatheringReagentWrapper(Material material) {
        String templateId;
        Material placeHolderType;
        String holoString;
        switch (material) {
            case IRON_ORE:
                templateId = "iron-ore";
                placeHolderType = Material.COBBLESTONE;
                holoString = "+ Iron";
                break;
            case GOLD_ORE:
                templateId = "gold-ore";
                placeHolderType = Material.COBBLESTONE;
                holoString = "+ Gold";
                break;
            case REDSTONE_ORE:
                templateId = "uncut-ruby";
                placeHolderType = Material.COBBLESTONE;
                holoString = "+ Ruby";
                break;
            case LAPIS_ORE:
                templateId = "uncut-sapphire";
                placeHolderType = Material.COBBLESTONE;
                holoString = "+ Sapphire";
                break;
            case NETHER_QUARTZ_ORE:
                templateId = "uncut-opal";
                placeHolderType = Material.COBBLESTONE;
                holoString = "+ Opal";
                break;
            case EMERALD_ORE:
                templateId = "uncut-emerald";
                placeHolderType = Material.COBBLESTONE;
                holoString = "+ Emerald";
                break;
            case DIAMOND_ORE:
                templateId = "uncut-diamond";
                placeHolderType = Material.COBBLESTONE;
                holoString = "+ Diamond";
                break;
            case IRON_BLOCK:
                templateId = "TrueIron";
                placeHolderType = Material.COBBLESTONE;
                holoString = "+ True Iron";
                break;
            default:
                return null;
        }
        return new GatheringUtil.GatheringReagentWrapper(templateId, placeHolderType, holoString);
    }
}
