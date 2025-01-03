package com.runicrealms.plugin.professions.listeners;

import com.google.common.base.Preconditions;
import com.runicrealms.plugin.api.WeightedRandomBag;
import com.runicrealms.plugin.common.util.ColorUtil;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.professions.event.GatheringEvent;
import com.runicrealms.plugin.professions.event.RunicGatheringExpEvent;
import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.gathering.GatheringTool;
import com.runicrealms.plugin.runicitems.RunicItemsAPI;
import com.runicrealms.plugin.runicitems.item.RunicItem;
import com.runicrealms.plugin.runicitems.item.RunicItemDynamic;
import com.runicrealms.plugin.runicitems.util.CurrencyUtil;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * @param player         to show the hologram to
     * @param location       of the hologram
     * @param height         of the hologram
     * @param duration       of the hologram (in secs)
     * @param itemsToDisplay for the hologram's contents
     * @return the hologram
     */
    private Hologram createHologram(@NotNull Player player, @NotNull Location location, double height, double duration, @NotNull List<RunicItem> itemsToDisplay) {
        Hologram hologram = HolographicDisplaysAPI.get(RunicProfessions.getInstance()).createHologram(location.clone().add(0, height, 0));
        hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);

        Map<String, Integer> items = new HashMap<>();

        for (RunicItem item : itemsToDisplay) {
            Integer count = items.get(item.getTemplateId());

            items.put(item.getTemplateId(), count != null ? count + 1 : 1);
        }

        for (RunicItem item : itemsToDisplay) {
            Integer count = items.remove(item.getTemplateId());

            if (count == null) {
                continue;
            }

            hologram.getLines().appendItem(item.getDisplayableItem().generateItem(1));
            hologram.getLines().appendText(ColorUtil.format("&a+" + count + " " + item.getDisplayableItem().getDisplayName()));
        }

        Bukkit.getScheduler().runTaskLater(RunicProfessions.getInstance(), hologram::delete, (long) duration * 20);
        return hologram;
    }

    /**
     * @param player         to show the hologram to
     * @param location       of the hologram
     * @param height         of the hologram
     * @param duration       of the hologram (in secs)
     * @param linesToDisplay for the hologram's contents
     * @return the hologram
     */
    private Hologram createHologram(@NotNull Player player, @NotNull Location location, double height, double duration, @NotNull String... linesToDisplay) {
        Hologram hologram = HolographicDisplaysAPI.get(RunicProfessions.getInstance()).createHologram(location.clone().add(0, height, 0));
        hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);

        for (String line : linesToDisplay) {
            hologram.getLines().appendText(line);
        }

        Bukkit.getScheduler().runTaskLater(RunicProfessions.getInstance(), hologram::delete, (long) duration * 20);
        return hologram;
    }

    /**
     * @param player        to show the hologram to
     * @param location      of the hologram
     * @param lineToDisplay for the hologram's contents
     * @param height        of the hologram
     * @param duration      of the hologram (in secs)
     * @return the hologram
     */
    private Hologram createHologram(@NotNull Player player, @NotNull Location location, @NotNull String lineToDisplay, float height, double duration) {
        return createHologram(player, location, height, duration, lineToDisplay);
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
     * @param gatheringTool     the tool used to gather material (for loot rates)
     * @param gatheringResource the resource which is to be gathered
     * @param location          the location of the block to replace
     * @param block             the block itself to replace
     * @param chance            the chance to gather the material
     */
    private void gatherMaterial(@NotNull Player player, @NotNull GatheringTool gatheringTool, @NotNull GatheringResource gatheringResource,
                                @NotNull Location location, @NotNull Block block, double chance) {
        block.setType(gatheringResource.getPlaceholderBlockType());

        RunicGatheringExpEvent event = new RunicGatheringExpEvent(gatheringResource.getExperience(), true, player, gatheringResource.getGatheringSkill());
        Bukkit.getPluginManager().callEvent(event);

        if (location.clone().add(0, 2, 0).getBlock().getType() == Material.AIR) {
            Hologram hologram = createHologram
                    (
                            player,
                            location,
                            ChatColor.GREEN + "+ " + RunicItemsAPI.generateItemFromTemplate(gatheringResource.getTemplateId()).getDisplayableItem().getDisplayName(),
                            2f,
                            2
                    );

            if (!event.isCancelled()) {
                ChatColor expColor = event.getAmountNoBonuses() == 0 ? ChatColor.RED : ChatColor.WHITE;
                hologram.getLines().appendText(ColorUtil.format("&7+ " + expColor + event.getAmountNoBonuses() + " &7exp"));
                int boostBonus = event.getExpFromBonus(RunicGatheringExpEvent.BonusType.BOOST);
                if (boostBonus != 0)
                    hologram.getLines().appendText(ColorUtil.format("&7+ &d" + boostBonus + " &7boost exp"));
            }
        }
        // give experience and resource
        RunicItemsAPI.addItem(player.getInventory(), RunicItemsAPI.generateItemFromTemplate(gatheringResource.getTemplateId()).generateItem(), player.getLocation());

        // gathering luck logic
        giveAdditionalResources(player, gatheringResource.getTemplateId(), gatheringTool, gatheringTool.getBonusLootChance());
        // give the player a coin
        givePlayerCoin(player, location, chance);
        // add block to respawn task
        RunicProfessions.getAPI().getBlocksToRestore().put(block.getLocation(), gatheringResource.getResourceBlockType());
    }

    /**
     * Modified function to handle gathering for FISHING
     *
     * @param player             who gathered material
     * @param gatheringTool      the tool used to gather material (for loot rates)
     * @param gatheringResources the resources which are to be gathered
     * @param location           the location of the block to replace
     * @param chance             the chance to gather the material
     */
    private void gatherMaterial(@NotNull Player player, @NotNull GatheringTool gatheringTool, @NotNull List<GatheringResource> gatheringResources,
                                @NotNull Location location, double chance) {
        //ItemStack fish = RunicItemsAPI.generateItemFromTemplate(templateId).generateItem();
        // Give the player experience the gathered item, drop on floor if inventory is full
        List<RunicItem> fishes = new ArrayList<>();
        for (GatheringResource resource : gatheringResources) {
            fishes.add(RunicItemsAPI.generateItemFromTemplate(resource.getTemplateId()));
        }

        // Grant experience, give fish
        RunicGatheringExpEvent event = new RunicGatheringExpEvent(gatheringResources.stream().mapToInt(GatheringResource::getExperience).sum(), true, player, GatheringSkill.FISHING);
        Bukkit.getPluginManager().callEvent(event);
        Hologram hologram = createHologram
                (
                        player,
                        location,
                        0.5,
                        2.5,
                        fishes
                );

        if (!event.isCancelled()) {
            ChatColor expColor = event.getAmountNoBonuses() == 0 ? ChatColor.RED : ChatColor.WHITE;
            hologram.getLines().appendText(ColorUtil.format("&7+ " + expColor + event.getAmountNoBonuses() + " &7exp"));
        }

        int boostBonus = event.getExpFromBonus(RunicGatheringExpEvent.BonusType.BOOST);
        if (boostBonus != 0 && !event.isCancelled()) {
            hologram.getLines().appendText(ColorUtil.format("&7+ &d" + boostBonus + " &7boost exp"));
        }

        for (RunicItem item : fishes) {
            RunicItemsAPI.addItem(player.getInventory(), item.generateItem(), player.getLocation());

            // Gathering luck logic
            giveAdditionalResources(player, item.getTemplateId(), gatheringTool, gatheringTool.getBonusLootChance());
        }

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
            RunicItemsAPI.addItem
                    (
                            player.getInventory(),
                            RunicItemsAPI.generateItemFromTemplate(templateId, gatheringTool.getBonusLootAmount()).generateItem(),
                            player.getLocation()
                    );
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
            createHologram(player, location, ChatColor.GOLD + "+ Gold Coin", 1.25f, 2);
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

        for (GatheringResource resource : event.getGatheringResources()) {
            // Validate resource level requirement has been met
            int requiredGatheringLevel = resource.getRequiredLevel();
            if (currentGatheringLevel < requiredGatheringLevel) {
                event.setCancelled(true);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                event.getPlayer().sendMessage
                        (
                                ChatColor.RED + "You must reach level " + requiredGatheringLevel + " " +
                                        resource.getGatheringSkill().getIdentifier() + " to gather this resource!"
                        );
                return;
            }
        }

        // Reduce tool durability
        RunicItemDynamic runicItemDynamic = (RunicItemDynamic) RunicItemsAPI.getRunicItemFromItemStack(event.getItemStack());
        reduceGatheringToolDurability(event.getPlayer(), runicItemDynamic, event.getItemStack(), event.getGatheringResources().size());
        if (event.getGatheringTool().getGatheringSkill() == GatheringSkill.FISHING) {
            gatherMaterial
                    (
                            event.getPlayer(),
                            event.getGatheringTool(),
                            event.getGatheringResources(),
                            event.getLocation(),
                            event.getRoll()
                    );
            return;
        }

        if (event.getGatheringTool().getGatheringSkill() == GatheringSkill.MINING) {
            GatheringResource resource = determineMiningResource(event.getGatheringResources().get(0), currentGatheringLevel);
            event.getGatheringResources().clear();
            event.getGatheringResources().add(resource);
        }

        if (event.getBlock() == null) {
            throw new IllegalStateException("The block may not be null for mining gathering event!");
        }

        gatherMaterial
                (
                        event.getPlayer(),
                        event.getGatheringTool(),
                        event.getGatheringResources().get(0),
                        event.getLocation(),
                        event.getBlock(),
                        event.getRoll()
                );
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
        if (gatheringTool.isEmpty()) {
            player.sendMessage(gatheringResource.getGatheringSkill().getNoToolMessage());
            return;
        }

        GatheringEvent gatheringEvent = new GatheringEvent
                (
                        player,
                        gatheringResource,
                        gatheringTool.get(),
                        heldItem,
                        loc,
                        block,
                        chance
                );
        Bukkit.getPluginManager().callEvent(gatheringEvent);
    }

    /**
     * Reduces the durability of a RunicItemDynamic after gathering a material
     *
     * @param player           who gathered material
     * @param runicItemDynamic the item to reduce the durability of
     * @param amount           the amount it should be reduced by
     */
    private void reduceGatheringToolDurability(@NotNull Player player, @NotNull RunicItemDynamic runicItemDynamic, @NotNull ItemStack itemStack, int amount) {
        Preconditions.checkArgument(amount > 0); //assert that amount must be greater than 0

        int durability = runicItemDynamic.getDynamicField();
        int newDurability = durability - amount;

        if (newDurability <= 0) {
            player.getInventory().setItemInMainHand(null);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
            player.sendMessage(ChatColor.RED + "Your gathering tool broke!");
            return;
        }

        runicItemDynamic.setDynamicField(newDurability);
        ItemStack newGatheringTool = runicItemDynamic.updateItemStack(itemStack);
        player.getInventory().setItemInMainHand(newGatheringTool);
    }
}
