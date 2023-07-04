package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.loot.LootHolder;
import com.runicrealms.plugin.loot.LootTable;
import com.runicrealms.plugin.professions.event.GatheringEvent;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.utilities.ActionBarUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItemDynamic;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Used when the player catches a fish to start a fishing session, where the player will need to reel it in over the
 * next 10s
 */
public class FishingSession {
    public static final int FISH_BUNDLE_NUMBER = 4; // How many fish per successful attempt
    private static final int SESSION_TIME = 10; // Seconds
    private static final double RARE_TABLE_CHANCE = .05; // 5%
    private static final double SLACK_FAILURE_MAX = 0.95;
    private static final Map<UUID, FishingSession> FISHERS = new HashMap<>();
    private final Hologram hologram;
    private final Player player;
    private final Location location;
    private final List<GatheringResource> fishBundle; // Reward for successful completion
    private final GatheringTool gatheringTool;
    private final ItemStack heldItem;
    private double slack;
    private BukkitRunnable sessionTimer;
    private int count;

    public FishingSession(Player player, Location location, List<GatheringResource> fishBundle, GatheringTool gatheringTool, ItemStack heldItem) {
        this.player = player;
        this.location = location;
        this.fishBundle = fishBundle;
        this.gatheringTool = gatheringTool;
        this.heldItem = heldItem;
        this.slack = 0.05 + Math.random() * 0.1; // Start with some slack in the line
        this.count = 0;
        this.hologram = createHologram();
    }

    public static Map<UUID, FishingSession> getFishers() {
        return FISHERS;
    }

    private Hologram createHologram() {
        // Create a hologram a few blocks above the pond
        Hologram hologram = HolographicDisplaysAPI.get(RunicProfessions.getInstance()).createHologram(location.add(0, 3, 0));
        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

        // Initialize hologram with 10 white pipe bars, center 2 are green
        String initialHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "||||" + ChatColor.DARK_GREEN + "||" + ChatColor.GRAY + "||||" + ChatColor.GOLD + "]";
        hologram.getLines().appendText(ChatColor.AQUA + "Time Remaining: " + ChatColor.WHITE + (10 - count) + ChatColor.AQUA + "s");
        hologram.getLines().appendText(initialHologramText);
        hologram.getLines().appendText(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "RIGHT CLICK " + ChatColor.GRAY + " to tighten the line!");

        return hologram;
    }

    /**
     * Starts the fishing session, beginning the time and adding the user to the 'fishers' map for listeners
     */
    public void startSession() {
        FISHERS.put(player.getUniqueId(), this);
        // Create a timer for the session. It will run every second.
        this.sessionTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (slack >= 0 && slack <= SLACK_FAILURE_MAX) {
                    // Add a random amount of slack between 0.1 and 0.25
                    slack += 0.1 + Math.random() * 0.15;
                    // Update hologram color according to the new slack
                    updateHologramColor();
                    // Move the timer forward
                    count++;
                    hologram.getLines().remove(0);
                    hologram.getLines().insertText(0, ChatColor.AQUA + "Time Remaining: " + ChatColor.WHITE + (10 - count) + ChatColor.AQUA + "s");
                }

                if (count >= SESSION_TIME || slack < 0 || slack > SLACK_FAILURE_MAX) {
                    if (count >= SESSION_TIME) {
                        stopSession(SessionResult.SUCCESS);
                    } else {
                        stopSession(SessionResult.FAILURE);
                    }
                }
            }
        };

        sessionTimer.runTaskTimer(RunicProfessions.getInstance(), 0, 20);  // 20 ticks = 1 second
    }

    /**
     * Used when the player interacts with their fishing rod to tighten the line, reducing the slack
     */
    public void tightenLine() {
        // Player action tightens the line and reduces slack
        this.slack -= 0.1;  // or another suitable value

        player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5f, 1.0f);

        if (slack < 0 || slack > SLACK_FAILURE_MAX) {
            stopSession(SessionResult.FAILURE);
            return;
        }

        // Update hologram color according to the slack
        updateHologramColor();

        ActionBarUtil.sendTimedMessage
                (
                        player,
                        ChatColor.GRAY + "You tighten the line!",
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
            if (slack < 0) {
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.5f, 2.0f);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 0.8f);
                reason = ChatColor.RED + "The line was too tight, and it snapped!";
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_SPLASH, 0.5f, 1.0f);
                player.playSound(player.getLocation(), Sound.ENTITY_FISH_SWIM, 0.5f, 1.0f);
                reason = ChatColor.RED + "The line was too loose and the fish got away!";
            }
            player.sendMessage(ChatColor.RED + "You failed to reel in the fish! " + reason);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "You successfully reeled in the fish!");
            distributeReward();
        }

        // Stop the bukkit task
        sessionTimer.cancel();

        // Remove the hologram
        hologram.delete();

        FISHERS.remove(player.getUniqueId());
    }

    private void distributeReward() {
        double chance = ThreadLocalRandom.current().nextDouble();
        // Custom drop table
        if (chance <= RARE_TABLE_CHANCE) {
            GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(player.getUniqueId());
            int fishingLevel = gatheringData.getFishingLevel();
            LootTable fishingLootTable = RunicCore.getLootAPI().getLootTable("tier-1");
            int minLevelScriptItem = fishingLevel - 5;
            if (minLevelScriptItem < 0)
                minLevelScriptItem = 0;
            int maxLevelScriptItem = fishingLevel;
            if (maxLevelScriptItem > 60)
                maxLevelScriptItem = 60;
            FishingLootHolder fishingLootHolder = new FishingLootHolder(minLevelScriptItem, maxLevelScriptItem);
            ItemStack loot = fishingLootTable.generateLoot(fishingLootHolder, player);
            RunicItemsAPI.addItem(player.getInventory(), loot);
            assert loot.getItemMeta() != null;
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "Your line caught an item: " + loot.getItemMeta().getDisplayName() + ChatColor.GREEN + "!");
        }
        // Bundle of fish
        distributeFishBundle();
    }

    private void distributeFishBundle() {
        BukkitRunnable rewardTask = new BukkitRunnable() {
            int rewardCount = 0;

            @Override
            public void run() {
                // Stop the process early if the tool breaks
                int durability = ((RunicItemDynamic) RunicItemsAPI.getRunicItemFromItemStack(heldItem)).getDynamicField();
                if (durability < 1 || rewardCount >= FISH_BUNDLE_NUMBER) {
                    this.cancel();
                } else {
                    GatheringResource gatheringResource = fishBundle.get(rewardCount);
                    String templateId = gatheringResource.getTemplateId();
                    GatheringEvent gatheringEvent = new GatheringEvent
                            (
                                    player,
                                    gatheringResource,
                                    gatheringTool,
                                    heldItem,
                                    templateId,
                                    location,
                                    null,
                                    gatheringResource.getResourceBlockType(),
                                    0,
                                    gatheringResource.getResourceBlockType()
                            );
                    Bukkit.getPluginManager().callEvent(gatheringEvent);
                    rewardCount++;
                }
            }
        };

        rewardTask.runTaskTimer(RunicProfessions.getInstance(), 0, 20L);
    }

    private void updateHologramColor() {
        String updatedHologramText;

        if (slack >= 0 && slack < 0.3) {
            // Define hologram text for slack in range [0, 0.3)
            updatedHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "||||" + ChatColor.GREEN + "||" + ChatColor.GRAY + "||||" + ChatColor.GOLD + "]";
        } else if (slack >= 0.3 && slack < 0.6) {
            // Define hologram text for slack in range [0.3, 0.6)
            updatedHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "|||" + ChatColor.YELLOW + "||||" + ChatColor.GRAY + "|||" + ChatColor.GOLD + "]";
        } else if (slack >= 0.6 && slack < 0.8) {
            // Define hologram text for slack in range [0.6, 0.8)
            updatedHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "||" + ChatColor.RED + "||||||" + ChatColor.GRAY + "||" + ChatColor.GOLD + "]";
        } else {
            // Define hologram text for slack >= 0.8
            updatedHologramText = ChatColor.GOLD + "[" + ChatColor.GRAY + "|" + ChatColor.DARK_RED + "||||||||" + ChatColor.GRAY + "|" + ChatColor.GOLD + "]";
        }

        // Update hologram with new slack color
        hologram.getLines().remove(1);
        hologram.getLines().insertText(1, updatedHologramText);
    }

    enum SessionResult {
        SUCCESS,
        FAILURE
    }

    static class FishingLootHolder implements LootHolder {

        private final int minLevel;
        private final int maxLevel;

        public FishingLootHolder(int minLevel, int maxLevel) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }

        @Override
        public int getItemMinLevel(Player player) {
            return minLevel;
        }

        @Override
        public int getItemMaxLevel(Player player) {
            return maxLevel;
        }
    }

}

