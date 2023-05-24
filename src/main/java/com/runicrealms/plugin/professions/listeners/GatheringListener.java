package com.runicrealms.plugin.professions.listeners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.WeightedRandomBag;
import com.runicrealms.plugin.professions.event.GatheringEvent;
import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.gathering.GatheringTool;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItem;
import com.runicrealms.runicitems.item.RunicItemDynamic;
import com.runicrealms.runicitems.util.CurrencyUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Works with our custom GatheringEvent to provide gathering exp and resources when the player gathers a material
 */
public class GatheringListener implements Listener {

    private static final double COIN_CHANCE = .95;

    /**
     * @param player        to show the hologram to
     * @param location      of the hologram
     * @param lineToDisplay for the hologram's contents
     * @param height        of the hologram
     * @return the hologram
     */
    private Hologram createHologram(Player player, Location location, String lineToDisplay, float height) {
        Hologram hologram = HologramsAPI.createHologram(RunicProfessions.getInstance(), location.clone().add(0, height, 0));
        hologram.getVisibilityManager().showTo(player);
        hologram.getVisibilityManager().setVisibleByDefault(false);
        hologram.appendTextLine(lineToDisplay);
        Bukkit.getScheduler().runTaskLater(RunicProfessions.getInstance(), hologram::delete, 40L); // 2s
        return hologram;
    }

    /**
     * Creates a quick drop table for mining ores based on the mining level of the player
     *
     * @param gatheringResource the original resource mined (e.g. iron-ore)
     * @param miningLevel       the mining level of the player
     * @return the templateId of the quality of ore (e.g. iron-ore-moderate)
     */
    private GatheringResource determineMiningResource(GatheringResource gatheringResource, int miningLevel) {
        WeightedRandomBag<GatheringResource> oreDropTable = new WeightedRandomBag<>();
        oreDropTable.addEntry(gatheringResource, 1000);
        for (GatheringResource resource : GatheringResource.values()) {
            if (resource.getGatheringSkill() != GatheringSkill.MINING) continue;
            if (resource.getResourceBlockType() != gatheringResource.getResourceBlockType())
                continue;
            if (resource.getTemplateId().equalsIgnoreCase(gatheringResource.getTemplateId()))
                continue;
            int reqLevel = resource.getRequiredLevel();
            if (miningLevel < reqLevel) continue;
            int weight;
            if (resource.getRequiredLevel() == ProfExpUtil.MAX_CAPPED_GATHERING_PROF_LEVEL) {
                weight = 15; // .006
            } else {
                weight = 100 + (10 * miningLevel);
            }
            oreDropTable.addEntry(resource, weight);
        }
        return oreDropTable.getRandom();
    }

    /**
     * General function to handle gathering
     *
     * @param player            who gathered material
     * @param templateId        the templateId of the gathered material (iron-ore)
     * @param gatheringTool     the tool used to gather material (for loot rates)
     * @param gatheringResource the resource which is to be gathered
     * @param location          the location of the block to replace
     * @param block             the block itself to replace
     * @param reagentBlockType  the material of the resource (iron ore)
     * @param chance            the chance to gather the material
     */
    private void gatherMaterial(Player player, String templateId, GatheringTool gatheringTool, GatheringResource gatheringResource,
                                Location location, Block block, Material reagentBlockType, double chance) {
        block.setType(gatheringResource.getPlaceholderBlockType());
        if (location.clone().add(0, 1.5, 0).getBlock().getType() == Material.AIR) {
            createHologram
                    (
                            player,
                            location,
                            ChatColor.GREEN + "+ " + RunicItemsAPI.generateItemFromTemplate(templateId).getDisplayableItem().getDisplayName(),
                            1.5f
                    );
        }
        // give experience and resource
        ProfExpUtil.giveGatheringExperience(player, gatheringResource.getGatheringSkill(), gatheringResource.getExperience());
        RunicItemsAPI.addItem(player.getInventory(), RunicItemsAPI.generateItemFromTemplate(templateId).generateItem(), player.getLocation());
        // gathering luck logic
        giveAdditionalResources(player, templateId, gatheringTool, gatheringTool.getBonusLootChance());
        // give the player a coin
        givePlayerCoin(player, location, chance);
        // add block to respawn task
        RunicProfessions.getAPI().getBlocksToRestore().put(block.getLocation(), reagentBlockType);
    }

    /**
     * Modified function to handle gathering for FISHING
     *
     * @param player            who gathered material
     * @param templateId        the templateId of the gathered material (iron-ore)
     * @param gatheringTool     the tool used to gather material (for loot rates)
     * @param gatheringResource the resource which is to be gathered
     * @param location          the location of the block to replace
     * @param fishItemToDisplay the floating item which will display
     * @param chance            the chance to gather the material
     */
    private void gatherMaterial(Player player, String templateId, GatheringTool gatheringTool, GatheringResource gatheringResource,
                                Location location, Material fishItemToDisplay, double chance) {
        ItemStack fish = RunicItemsAPI.generateItemFromTemplate(templateId).generateItem();
        // Give the player experience the gathered item, drop on floor if inventory is full
        Hologram hologram = createHologram
                (
                        player,
                        location,
                        ChatColor.GREEN + "+ " + RunicItemsAPI.generateItemFromTemplate(templateId).getDisplayableItem().getDisplayName(),
                        1.5f
                );
        // Spawn floating fish
        hologram.appendItemLine(new ItemStack(fishItemToDisplay));
        // Grant experience, give fish
        ProfExpUtil.giveGatheringExperience(player, gatheringResource.getGatheringSkill(), gatheringResource.getExperience());
        RunicItemsAPI.addItem(player.getInventory(), fish, player.getLocation());
        // Gathering luck logic
        giveAdditionalResources(player, templateId, gatheringTool, gatheringTool.getBonusLootChance());
        // Give the player a coin
        givePlayerCoin(player, location, chance);
    }

    /**
     * Attempts to grant the player bonus resources based on the gathering perk on their tool
     *
     * @param player          to receive additional gathering resources
     * @param templateId      of the resource to give (iron-ore)
     * @param gatheringTool   they used (apprentice pickaxe)
     * @param bonusLootChance the chance of the item to grant additional loot
     */
    private void giveAdditionalResources(Player player, String templateId, GatheringTool gatheringTool, double bonusLootChance) {
        if (bonusLootChance <= 0) return;
        double bonusLootRoll = ThreadLocalRandom.current().nextDouble();
        if (bonusLootRoll <= bonusLootChance && gatheringTool.getBonusLootAmount() > 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 0.5f, 0.5f);
            int bonusLootAmount = ThreadLocalRandom.current().nextInt(1, gatheringTool.getBonusLootAmount() + 1);
            RunicItemsAPI.addItem(player.getInventory(), RunicItemsAPI.generateItemFromTemplate(templateId, bonusLootAmount).generateItem(), player.getLocation());
        }
    }

    /**
     * Chance during gathering for player to receive a gold coin
     *
     * @param player   to receive coin
     * @param location to display hologram
     * @param chance   to obtain coin
     */
    private void givePlayerCoin(Player player, Location location, double chance) {
        if (chance >= COIN_CHANCE) {
            player.getWorld().playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);
            createHologram(player, location, ChatColor.GOLD + "+ Gold Coin", 1.25f);
            RunicItemsAPI.addItem(player.getInventory(), CurrencyUtil.goldCoin(), player.getLocation());
        }
    }

    @EventHandler
    public void onGather(GatheringEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        int currentGatheringLevel = RunicProfessions.getAPI().determineCurrentGatheringLevel(uuid, event.getGatheringTool().getGatheringSkill());
        // Validate tool level requirement has been met
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
        // Validate resource level requirement has been met
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
        // Reduce tool durability
        RunicItemDynamic runicItemDynamic = (RunicItemDynamic) RunicItemsAPI.getRunicItemFromItemStack(event.getItemStack());
        reduceGatheringToolDurability(event.getPlayer(), runicItemDynamic, event.getItemStack());
        if (event.getGatheringTool().getGatheringSkill() == GatheringSkill.FISHING) {
            gatherMaterial
                    (
                            event.getPlayer(),
                            event.getTemplateIdOfResource(),
                            event.getGatheringTool(),
                            event.getGatheringResource(),
                            event.getLocation(),
                            event.getReagentBlockType(),
                            event.getRoll()
                    );
        } else {
            if (event.getGatheringTool().getGatheringSkill() == GatheringSkill.MINING) {
                event.setGatheringResource(determineMiningResource(event.getGatheringResource(), currentGatheringLevel));
            }
            gatherMaterial
                    (
                            event.getPlayer(),
                            event.getGatheringResource().getTemplateId(),
                            event.getGatheringTool(),
                            event.getGatheringResource(),
                            event.getLocation(),
                            event.getBlock(),
                            event.getReagentBlockType(),
                            event.getRoll()
                    );
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onResourceBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Block block = event.getBlock();
        Location blockLoc = event.getBlock().getLocation();
        Material oldType = block.getType();
        GatheringResource gatheringResource = GatheringResource.getFromResourceBlockType(oldType);
        if (gatheringResource == null) return;
        if (gatheringResource.getGatheringSkill() != GatheringSkill.MINING) { // You can mine from anywhere!
            if (!RunicProfessions.getAPI().isInGatheringRegion(gatheringResource.getGatheringRegion(), blockLoc))
                return;
        }
        double chance = ThreadLocalRandom.current().nextDouble();
        Location loc = block.getLocation().add(0.5, 0, 0.5);

        // ensure the proper type of block is being mined
        String templateId = gatheringResource.getTemplateId();
        event.setCancelled(true);
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

        GatheringEvent gatheringEvent = new GatheringEvent
                (
                        player,
                        gatheringResource,
                        gatheringTool.get(),
                        heldItem,
                        templateId,
                        loc,
                        block,
                        gatheringResource.getPlaceholderBlockType(),
                        chance,
                        gatheringResource.getResourceBlockType()
                );
        Bukkit.getPluginManager().callEvent(gatheringEvent);
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
