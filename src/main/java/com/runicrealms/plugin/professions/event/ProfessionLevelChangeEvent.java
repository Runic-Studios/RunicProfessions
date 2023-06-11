package com.runicrealms.plugin.professions.event;

import com.runicrealms.plugin.professions.Profession;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import redis.clients.jedis.Jedis;

/**
 * This custom event is called when a player changes their profession level
 */
public class ProfessionLevelChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Profession profession;
    private final int oldLevel;
    private final int newLevel;

    /**
     * Grab the new profession the player swapped to.
     *
     * @param player     player who swapped
     * @param profession the profession that was chosen
     * @param oldLevel   the level before the change
     * @param newLevel   the level after the change
     */
    public ProfessionLevelChangeEvent(Player player, Profession profession, int oldLevel, int newLevel, Jedis jedis) {
        this.player = player;
        this.profession = profession;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public int getNewLevel() {
        return this.newLevel;
    }

    public int getOldLevel() {
        return this.oldLevel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Profession getProfession() {
        return this.profession;
    }
}
