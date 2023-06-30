package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.RunicProfessions;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
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

        // Initialize hologram with 10 white pipe bars, center 2 are green
        String initialHologramText = ChatColor.WHITE + "||||" + ChatColor.DARK_GREEN + "||" + ChatColor.WHITE + "||||";
        hologram.getLines().appendText(initialHologramText);

        return hologram;
    }

    /**
     * ?
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
                    hologram.getLines().insertText(0, "Time Remaining: " + (10 - count) + "s");
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
     * ?
     */
    public void tightenLine() {
        // Player action tightens the line and reduces slack
        this.slack -= 0.1;  // or another suitable value

        if (slack < 0 || slack > SLACK_FAILURE_MAX) {
            stopSession(SessionResult.FAILURE);
            return;
        }

        // Update hologram color according to the slack
        updateHologramColor();

        player.sendMessage(ChatColor.GRAY + "You tighten the line!");
    }

    /**
     * ?
     *
     * @param sessionResult
     */
    private void stopSession(SessionResult sessionResult) {
        // Inform player of status
        if (sessionResult == SessionResult.FAILURE) {
            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 0.5f, 1.0f);
            player.sendMessage(ChatColor.RED + "You failed to reel in the fish.");
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, 0.5f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "You successfully reeled in the fish!");
        }

        // Stop the bukkit task
        sessionTimer.cancel();

        // Remove the hologram
        hologram.delete();

        FISHERS.remove(player.getUniqueId());
    }

    /**
     * ?
     */
    private void updateHologramColor() {
        String updatedHologramText = "";

        if (slack >= 0 && slack < 0.3) {
            // Define hologram text for slack in range [0, 0.3)
            updatedHologramText = ChatColor.WHITE + "||||" + ChatColor.GREEN + "||" + ChatColor.WHITE + "||||";
        } else if (slack >= 0.3 && slack < 0.6) {
            // Define hologram text for slack in range [0.3, 0.6)
            updatedHologramText = ChatColor.WHITE + "||||" + ChatColor.YELLOW + "||||" + ChatColor.WHITE + "||||";
        } else if (slack >= 0.6 && slack < 0.9) {
            // Define hologram text for slack in range [0.6, 0.9)
            updatedHologramText = ChatColor.WHITE + "||" + ChatColor.RED + "||||||" + ChatColor.WHITE + "||";
        } else {
            // Define hologram text for slack >= 0.9
            updatedHologramText = ChatColor.WHITE + "|" + ChatColor.DARK_RED + "||||||||" + ChatColor.WHITE + "|";
        }

        // Update hologram with new slack color
        hologram.getLines().clear();
        hologram.getLines().appendText(updatedHologramText);
    }

    enum SessionResult {
        SUCCESS,
        FAILURE
    }

}

