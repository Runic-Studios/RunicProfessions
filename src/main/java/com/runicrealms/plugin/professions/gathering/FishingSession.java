package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.utilities.ActionBarUtil;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used when the player catches a fish to start a fishing session, where the player will need to reel it in over the
 * next 10s
 */
public class FishingSession {
    private static final int SESSION_TIME = 10; // Seconds
    private static final double SLACK_FAILURE_MAX = 0.95;
    private static final Map<UUID, FishingSession> FISHERS = new HashMap<>();
    private final Hologram hologram;
    private final Player player;
    private final Location location;
    private double slack;
    private BukkitRunnable sessionTimer;
    private int count;

    public FishingSession(Player player, Location location) {
        this.player = player;
        this.location = location;
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
        hologram.getLines().appendText(ChatColor.DARK_AQUA + "Time Remaining: " + ChatColor.WHITE + (10 - count) + ChatColor.DARK_AQUA + "s");
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
                    hologram.getLines().insertText(0, ChatColor.DARK_AQUA + "Time Remaining: " + ChatColor.WHITE + (10 - count) + ChatColor.DARK_AQUA + "s");
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
//        GatheringEvent gatheringEvent = new GatheringEvent
//                (
//                        player,
//                        gatheringResource,
//                        gatheringTool.get(),
//                        heldItem,
//                        templateId,
//                        hookLoc,
//                        null,
//                        gatheringResource.getResourceBlockType(),
//                        chance,
//                        gatheringResource.getResourceBlockType()
//                );
//        Bukkit.getPluginManager().callEvent(gatheringEvent);
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

}

