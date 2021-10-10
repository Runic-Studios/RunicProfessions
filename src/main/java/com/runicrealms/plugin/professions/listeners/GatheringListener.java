package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.professions.event.CustomFishEvent;
import com.runicrealms.plugin.professions.event.GatheringEvent;
import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.gathering.GatheringTool;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.utilities.CurrencyUtil;
import com.runicrealms.plugin.utilities.FloatingItemUtil;
import com.runicrealms.plugin.utilities.HologramUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItem;
import com.runicrealms.runicitems.item.RunicItemDynamic;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Works with our custom GatheringEvent to provide gathering exp and resources when the player gathers a material
 */
public class GatheringListener implements Listener {

    private static final double COIN_CHANCE = .95;

    @EventHandler
    public void onResourceBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Block block = e.getBlock();
        Location blockLoc = e.getBlock().getLocation();
        Material oldType = block.getType();
        GatheringResource gatheringResource = GatheringResource.getFromResourceBlockType(oldType);
        if (gatheringResource == null) return;
        if (!RunicProfessionsAPI.isInGatheringRegion(gatheringResource.getGatheringRegion(), blockLoc)) return;
        double chance = ThreadLocalRandom.current().nextDouble();
        Location loc = block.getLocation().add(0.5, 0, 0.5);

        // ensure the proper type of block is being mined
        String templateId = gatheringResource.getTemplateId();
        Material placeHolderType = gatheringResource.getPlaceholderBlockType();
        String holoString = gatheringResource.getHologramDisplayString();
        e.setCancelled(true);
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        // verify the player is holding a tool
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(gatheringResource.getGatheringSkill().getNoToolMessage());
            return;
        }

        // verify held tool corresponds to the correct gathering skill
        RunicItem runicItem = RunicItemsAPI.getRunicItemFromItemStack(heldItem);
        String templateIdHeldItem = runicItem.getTemplateId();
        Set<GatheringTool> toolSet = GatheringResource.determineToolSet(gatheringResource);
        if (toolSet == null) {
            player.sendMessage(gatheringResource.getGatheringSkill().getNoToolMessage());
            return;
        }
        Optional<GatheringTool> gatheringTool = toolSet.stream().filter(tool -> tool.getRunicItemDynamic().getTemplateId().equals(templateIdHeldItem)).findFirst();
        if (!gatheringTool.isPresent()) {
            player.sendMessage(gatheringResource.getGatheringSkill().getNoToolMessage());
            return;
        }

        GatheringEvent gatheringEvent = new GatheringEvent(player, gatheringResource, gatheringTool.get(), heldItem, templateId, loc, block, placeHolderType, holoString, chance, gatheringResource.getResourceBlockType());
        Bukkit.getPluginManager().callEvent(gatheringEvent);
    }

    @EventHandler
    public void onGather(GatheringEvent event) {
        int currentGatheringLevel = RunicProfessionsAPI.determineCurrentGatheringLevel(event.getPlayer(), event.getGatheringTool().getGatheringSkill());
        // validate tool level requirement has been met
        int requiredToolLevel = event.getGatheringTool().getRunicItemDataField();
        if (currentGatheringLevel < requiredToolLevel) {
            event.setCancelled(true);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
            event.getPlayer().sendMessage
                    (
                            ChatColor.RED + "You must reach level " + requiredToolLevel + " " +
                                    event.getGatheringTool().getGatheringSkill().getIdentifier() + " to use this tool!"
                    );
            return;
        }
        // validate resource level requirement has been met
        int requiredGatheringLevel = event.getGatheringResource().getRequiredLevel();
        if (currentGatheringLevel < requiredGatheringLevel) {
            event.setCancelled(true);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
            event.getPlayer().sendMessage
                    (
                            ChatColor.RED + "You must reach level " + requiredGatheringLevel + " " +
                                    event.getGatheringResource().getGatheringSkill().getIdentifier() + " to gather this resource!"
                    );
            return;
        }
        // reduce tool durability
        RunicItemDynamic runicItemDynamic = (RunicItemDynamic) RunicItemsAPI.getRunicItemFromItemStack(event.getItemStack());
        reduceGatheringToolDurability(event.getPlayer(), runicItemDynamic, event.getItemStack());
        if (event.getGatheringTool().getGatheringSkill() == GatheringSkill.FISHING) {
            // todo if check for fishing method
        } else {
            // gather the material
            gatherMaterial(event.getPlayer(), event.getTemplateIdOfResource(), event.getGatheringTool(), event.getGatheringResource(),
                    event.getLocation(), event.getBlock(), event.getReagentBlockType(), event.getPlaceholderMaterial(),
                    event.getHologramItemName(), event.getRoll());
        }
    }

    /**
     * General function to handle gathering
     *
     * @param player              who gathered material
     * @param templateId          the templateId of the gathered material (iron-ore)
     * @param gatheringTool       the tool used to gather material (for loot rates)
     * @param gatheringResource   the resource which is to be gathered
     * @param location            the location of the block to replace
     * @param block               the block itself to replace
     * @param reagentBlockType    the material of the resource (iron ore)
     * @param placeholderMaterial the material to set while the block is regenerating (cobblestone)
     * @param hologramItemName    the hologram to display upon successful gathering
     * @param chance              the chance to gather the material
     */
    private void gatherMaterial(Player player, String templateId, GatheringTool gatheringTool, GatheringResource gatheringResource,
                                Location location, Block block, Material reagentBlockType, Material placeholderMaterial,
                                String hologramItemName, double chance) {
        block.setType(placeholderMaterial);
        if (location.clone().add(0, 1.5, 0).getBlock().getType() == Material.AIR) {
            HologramUtil.createStaticHologram(player, location, ChatColor.GREEN + "" + ChatColor.BOLD + hologramItemName, 0, 2, 0);
        }
        // give experience and resource
        ProfExpUtil.giveGatheringExperience(player, gatheringResource.getGatheringSkill(), gatheringResource.getExperience());
        RunicItemsAPI.addItem(player.getInventory(), RunicItemsAPI.generateItemFromTemplate(templateId).generateItem(), player.getLocation());
        // gathering luck logic
        double bonusLootChance = gatheringTool.getBonusLootChance();
        if (bonusLootChance > 0) {
            double bonusLootRoll = ThreadLocalRandom.current().nextDouble();
            if (bonusLootRoll <= bonusLootChance) {
                int bonusLootAmount = ThreadLocalRandom.current().nextInt(1, gatheringTool.getBonusLootAmount() + 1);
                RunicItemsAPI.addItem(player.getInventory(), RunicItemsAPI.generateItemFromTemplate(templateId, bonusLootAmount).generateItem(), player.getLocation());
            }
        }
        // give the player a coin
        if (chance >= (COIN_CHANCE)) {
            block.getWorld().playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);
            HologramUtil.createStaticHologram(player, location, ChatColor.GOLD + "" + ChatColor.BOLD + "+ Coin", 0, 1.25, 0);
            RunicItemsAPI.addItem(player.getInventory(), CurrencyUtil.goldCoin(), player.getLocation());
        }
        RunicProfessions.getProfManager().getBlocksToRestore().put(block.getLocation(), reagentBlockType);
    }

    /**
     * Modified function to handle gathering for fishing
     *
     * @param player            who gathered material
     * @param gatheringTool     the tool used
     * @param templateId        the templateId of the gathered material (iron-ore)
     * @param location          the location of the block to replace
     * @param fishLocation      the location where the fish item will be displayed
     * @param fishItemToDisplay the material of the fish item
     * @param hologramItemName  the hologram to display upon successful gathering
     * @param chance            the chance to gather the material
     * @param fishPath          a vector where the fish item will travel
     */
    private void gatherMaterial(Player player, RunicItemDynamic gatheringTool, String templateId, Location location,
                                Location fishLocation, Material fishItemToDisplay, String hologramItemName, double chance,
                                Vector fishPath) {

        // call the fishing event
        ItemStack fish = RunicItemsAPI.generateItemFromTemplate(templateId).generateItem();
        CustomFishEvent event = new CustomFishEvent(player, fish);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        // spawn floating fish
        FloatingItemUtil.spawnFloatingItem(player, fishLocation, fishItemToDisplay, 1, fishPath);

        // give the player the gathered item, drop on floor if inventory is full
        HologramUtil.createStaticHologram(player, location, ChatColor.GREEN + "" + ChatColor.BOLD + hologramItemName, 0, 2, 0);
        RunicItemsAPI.addItem(player.getInventory(), fish, player.getLocation());

        // give the player a coin
        if (chance >= COIN_CHANCE) {
            player.getWorld().playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);
            HologramUtil.createStaticHologram(player, location, ChatColor.GOLD + "" + ChatColor.BOLD + "+ Coin", 0, 1.25, 0);
            RunicItemsAPI.addItem(player.getInventory(), CurrencyUtil.goldCoin(), player.getLocation());
        }
    }

    /**
     * Reduces the durability of a RunicItemDynamic after gathering a material
     *
     * @param player           who gathered material
     * @param runicItemDynamic the item to reduce the durability of
     */
    private void reduceGatheringToolDurability(Player player, RunicItemDynamic runicItemDynamic, ItemStack itemStack) {
        int durability = runicItemDynamic.getDynamicField();
        int newDurability = durability - 1;
        runicItemDynamic.setDynamicField(newDurability);
        ItemStack newGatheringTool = runicItemDynamic.updateItemStack(itemStack);
        if (newDurability <= 0) {
            player.getInventory().setItemInMainHand(null);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
            player.sendMessage(ChatColor.RED + "Your gathering tool broke!");
        } else {
            player.getInventory().setItemInMainHand(newGatheringTool);
        }
    }
}
