package com.runicrealms.plugin.professions.event;

import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.model.GatheringData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This custom event is called when a player changes a gathering skill level
 */
public class GatheringLevelChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final GatheringData gatheringData;
    private final GatheringSkill gatheringSkill;
    private final int oldLevel;
    private final int newLevel;

    /**
     * Grab the new gathering skill information
     *
     * @param player         player changed level
     * @param gatheringData  the gathering data wrapper involved in the event
     * @param gatheringSkill the skill which changed level
     * @param oldLevel       the level before the change
     * @param newLevel       the level after the change
     */
    public GatheringLevelChangeEvent(Player player, GatheringData gatheringData, GatheringSkill gatheringSkill,
                                     int oldLevel, int newLevel) {
        this.player = player;
        this.gatheringData = gatheringData;
        this.gatheringSkill = gatheringSkill;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public GatheringData getGatheringData() {
        return this.gatheringData;
    }

    public GatheringSkill getGatheringSkill() {
        return this.gatheringSkill;
    }

    public int getOldLevel() {
        return this.oldLevel;
    }

    public int getNewLevel() {
        return this.newLevel;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
