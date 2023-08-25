package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.api.WeightedRandomBag;
import com.runicrealms.plugin.common.util.ColorUtil;
import com.runicrealms.plugin.loot.LootHolder;
import com.runicrealms.plugin.loot.LootTable;
import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.professions.event.GatheringEvent;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.runicitems.RunicItemsAPI;
import com.runicrealms.plugin.runicitems.item.RunicItemDynamic;
import com.runicrealms.plugin.utilities.ActionBarUtil;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Used when the player catches a fish to start a fishing session, where the player will need to reel it in over the
 * next 10s
 *
 * @author Skyfallin & BoBoBalloon
 */
public class FishingSession {
    private static final int SESSION_TIME = 10; // Seconds
    private static final double RARE_TABLE_CHANCE = .05; // 5%
    private static final double SLACK_FAILURE_MAX = 0.95;
    private static final double MAX_PLAYER_DISTANCE = 15; //blocks
    private static final Map<UUID, FishingSession> FISHERS = new HashMap<>();
    private final Hologram hologram;
    private final Player player;
    private final Location location;
    private final GatheringTool gatheringTool;
    private final ItemStack heldItem;
    private double slack;
    private BukkitTask sessionTimer;
    private int count;

    public FishingSession(@NotNull Player player, @NotNull Location location, @NotNull GatheringTool gatheringTool, @NotNull ItemStack heldItem) {
        this.player = player;
        this.location = location;
        this.gatheringTool = gatheringTool;
        this.heldItem = heldItem;
        this.slack = 0.05 + Math.random() * 0.1; // Start with some slack in the line
        this.count = 0;
        this.hologram = createHologram();
    }

    @NotNull
    public static Map<UUID, FishingSession> getFishers() {
        return FISHERS;
    }

    private Hologram createHologram() {
        // Create a hologram a few blocks above the pond
        Hologram hologram = HolographicDisplaysAPI.get(RunicProfessions.getInstance()).createHologram(this.location.add(0, 3, 0));
        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        hologram.getVisibilitySettings().setIndividualVisibility(this.player, VisibilitySettings.Visibility.VISIBLE);

        // Initialize hologram with 10 white pipe bars, center 2 are green
        String initialHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "||||" + ChatColor.DARK_GREEN + "||" + ChatColor.GRAY + "||||" + ChatColor.GOLD + "]";
        hologram.getLines().appendText(ChatColor.AQUA + "Time Remaining: " + ChatColor.WHITE + (10 - this.count) + ChatColor.AQUA + "s");
        hologram.getLines().appendText(initialHologramText);
        hologram.getLines().appendText(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RIGHT CLICK " + ChatColor.GRAY + " to tighten the line!");

        return hologram;
    }

    /**
     * Starts the fishing session, beginning the time and adding the user to the 'fishers' map for listeners
     */
    public void startSession() {
        FISHERS.put(this.player.getUniqueId(), this);
        // Create a timer for the session. It will run every second.
        this.sessionTimer = Bukkit.getScheduler().runTaskTimer(RunicProfessions.getInstance(), () -> {
            if (this.slack >= 0 && this.slack <= SLACK_FAILURE_MAX) {
                // Add a random amount of slack between 0.1 and 0.25
                this.slack += 0.1 + Math.random() * 0.15;
                // Update hologram color according to the new slack
                updateHologramColor();
                // Move the timer forward
                count++;
                TextHologramLine line = (TextHologramLine) this.hologram.getLines().get(0);
                line.setText(ChatColor.AQUA + "Time Remaining: " + ChatColor.WHITE + (10 - count) + ChatColor.AQUA + "s");
            }

            if (count >= SESSION_TIME || this.slack < 0 || this.slack > SLACK_FAILURE_MAX) {
                if (count >= SESSION_TIME) {
                    stopSession(SessionResult.SUCCESS);
                } else {
                    stopSession(SessionResult.FAILURE);
                }
            }

            if (this.player.getLocation().distance(this.location) > MAX_PLAYER_DISTANCE) {
                stopSession(SessionResult.FAILURE);
            }
        }, 0, 20); // 20 ticks = 1 second
    }

    /**
     * Used when the player interacts with their fishing rod to tighten the line, reducing the slack
     */
    public void tightenLine() {
        // Player action tightens the line and reduces slack
        this.slack -= 0.1;  // or another suitable value

        this.player.playSound(this.player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5f, 1.0f);

        if (this.slack < 0 || this.slack > SLACK_FAILURE_MAX) {
            stopSession(SessionResult.FAILURE);
            return;
        }

        // Update hologram color according to the slack
        updateHologramColor();

        ActionBarUtil.sendTimedMessage
                (
                        this.player,
                        ChatColor.YELLOW + "You tighten the line!",
                        3
                );
    }

    /**
     * @param sessionResult success or failure
     */
    private void stopSession(SessionResult sessionResult) {
        // Inform player of status
        if (sessionResult == SessionResult.FAILURE) {
            String reason;
            if (this.slack < 0) {
                this.player.playSound(this.player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.5f, 2.0f);
                this.player.playSound(this.player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 0.8f);
                reason = "The line was too tight, and it snapped!";
            } else if (this.slack > SLACK_FAILURE_MAX) {
                this.player.playSound(this.player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 0.5f, 1.0f);
                this.player.playSound(this.player.getLocation(), Sound.ENTITY_FISH_SWIM, 0.5f, 1.0f);
                reason = "The line was too loose and the fish got away!";
            } else {
                this.player.playSound(this.player.getLocation(), Sound.BLOCK_BASALT_BREAK, 0.5f, 1.0f);
                reason = "You moved too far away and the line broke!";
            }
            this.player.sendMessage(ColorUtil.format("&cYou failed to reel in the fish! " + reason));
        } else {
            this.player.playSound(this.player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            this.player.sendMessage(ChatColor.GREEN + "You successfully reeled in the fish!");
            distributeReward();
        }

        // Stop the bukkit task
        this.sessionTimer.cancel();

        // Remove the hologram
        this.hologram.delete();

        FISHERS.remove(this.player.getUniqueId());
    }

    private void distributeReward() {
        double chance = ThreadLocalRandom.current().nextDouble();
        // Custom drop table
        if (chance <= RARE_TABLE_CHANCE) {
            GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(this.player.getUniqueId());
            int fishingLevel = gatheringData.getFishingLevel();
            LootTable fishingLootTable = RunicCore.getLootAPI().getLootTable("tier-1");
            int minLevelScriptItem = fishingLevel - 5;
            if (minLevelScriptItem < 0)
                minLevelScriptItem = 0;
            int maxLevelScriptItem = fishingLevel;
            if (maxLevelScriptItem > 60)
                maxLevelScriptItem = 60;
            FishingLootHolder fishingLootHolder = new FishingLootHolder(minLevelScriptItem, maxLevelScriptItem);
            ItemStack loot = fishingLootTable.generateLoot(fishingLootHolder, this.player);
            RunicItemsAPI.addItem(this.player.getInventory(), loot);
            assert loot.getItemMeta() != null;
            this.player.playSound(this.player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.0f);
            this.player.sendMessage(ChatColor.GREEN + "Your line caught an item: " + loot.getItemMeta().getDisplayName() + ChatColor.GREEN + "!");
        }
        // Bundle of fish
        distributeFishBundle();
    }

    private void distributeFishBundle() {
        Slack lineSlack = Slack.getSlack(this.slack);
        int fish = lineSlack == Slack.GREEN ? 5 : lineSlack == Slack.YELLOW ? 4 : lineSlack == Slack.RED ? 3 : 2; //5 fish if green, 4 if yellow, 3 if red, 2 if dark red

        int durability = ((RunicItemDynamic) RunicItemsAPI.getRunicItemFromItemStack(this.heldItem)).getDynamicField();
        int amount = durability - fish > 0 ? fish : fish - Math.abs(durability - fish);

        List<GatheringResource> reward = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            reward.add(determineFishFromRegion());
        }

        GatheringEvent gatheringEvent = new GatheringEvent
                (
                        this.player,
                        reward,
                        this.gatheringTool,
                        this.heldItem,
                        this.location,
                        null,
                        0
                );
        Bukkit.getPluginManager().callEvent(gatheringEvent);
    }

    private void updateHologramColor() {
        String updatedHologramText;

        Slack lineSlack = Slack.getSlack(this.slack);

        if (lineSlack == Slack.GREEN) {
            // Define hologram text for slack in range [0, 0.3)
            updatedHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "||||" + ChatColor.GREEN + "||" + ChatColor.GRAY + "||||" + ChatColor.GOLD + "]";
        } else if (lineSlack == Slack.YELLOW) {
            // Define hologram text for slack in range [0.3, 0.6)
            updatedHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "|||" + ChatColor.YELLOW + "||||" + ChatColor.GRAY + "|||" + ChatColor.GOLD + "]";
        } else if (lineSlack == Slack.RED) {
            // Define hologram text for slack in range [0.6, 0.8)
            updatedHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "||" + ChatColor.RED + "||||||" + ChatColor.GRAY + "||" + ChatColor.GOLD + "]";
        } else {
            // Define hologram text for slack >= 0.8
            updatedHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "|" + ChatColor.DARK_RED + "||||||||" + ChatColor.GRAY + "|" + ChatColor.GOLD + "]";
        }

        // Update hologram with new slack color
        TextHologramLine line = (TextHologramLine) this.hologram.getLines().get(1);
        line.setText(updatedHologramText);
    }

    /**
     * Determine the fish to give the player based on the region they are fishing in
     *
     * @return the appropriate fish to gather
     */
    @NotNull
    private GatheringResource determineFishFromRegion() {
        List<String> regionIds = RunicCore.getRegionAPI().getRegionIds(this.location);

        try {
            Optional<String> fishingRegion = regionIds.stream().filter(region -> region.contains("pond")).findFirst();
            if (fishingRegion.isPresent()) {
                String fishingRegionName = fishingRegion.get();
                String[] availableFish = fishingRegionName.split("/");

                GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(this.player.getUniqueId());
                int fishingLevel = gatheringData.getFishingLevel();

                return determineFish(availableFish, fishingLevel);
            }
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Error: There was an error getting fish from roll!");
        }
        return GatheringResource.COD;
    }

    /**
     * @param hotspotResources a string array of templateIDs for fish
     * @param fishingLevel     of the player
     * @return the selected fish from the drop table
     */
    @NotNull
    private GatheringResource determineFish(@NotNull String[] hotspotResources, int fishingLevel) {
        WeightedRandomBag<GatheringResource> oreDropTable = new WeightedRandomBag<>();
        oreDropTable.addEntry(GatheringResource.SALMON, 400);
        oreDropTable.addEntry(GatheringResource.COD, 400);
        for (String string : hotspotResources) {
            GatheringResource gatheringResource = GatheringResource.getFromTemplateId(string);
            if (gatheringResource == null) continue;
            int reqLevel = gatheringResource.getRequiredLevel();
            // Skip fish that the user has not unlocked
            if (fishingLevel < reqLevel) continue;
            int weight = 100 + (10 * fishingLevel);
            oreDropTable.addEntry(gatheringResource, weight);
        }
        return oreDropTable.getRandom();
    }

    private enum SessionResult {
        SUCCESS,
        FAILURE
    }

    private enum Slack {
        GREEN,
        YELLOW,
        RED,
        DARK_RED;

        @NotNull
        public static Slack getSlack(double slack) {
            if (slack >= 0 && slack < 0.3) {
                // Define hologram text for slack in range [0, 0.3)
                return GREEN;
            } else if (slack >= 0.3 && slack < 0.6) {
                // Define hologram text for slack in range [0.3, 0.6)
                return YELLOW;
            } else if (slack >= 0.6 && slack < 0.8) {
                // Define hologram text for slack in range [0.6, 0.8)
                return RED;
            } else {
                // Define hologram text for slack >= 0.8
                return DARK_RED;
            }
        }
    }

    static class FishingLootHolder implements LootHolder {

        private final int minLevel;
        private final int maxLevel;

        public FishingLootHolder(int minLevel, int maxLevel) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }

        @Override
        public int getItemMinLevel(@NotNull Player player) {
            return minLevel;
        }

        @Override
        public int getItemMaxLevel(@NotNull Player player) {
            return maxLevel;
        }
    }

}

