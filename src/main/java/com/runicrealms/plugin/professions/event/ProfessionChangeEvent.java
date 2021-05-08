package com.runicrealms.plugin.professions.event;

import com.runicrealms.plugin.ProfessionEnum;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This custom event is called when a player swaps their profession after creating a character
 */
public class ProfessionChangeEvent extends Event {

    private final Player player;
    private final ProfessionEnum profession;

    /**
     * Grab the new profession the player swapped to.
     * @param player player who swapped
     * @param profession the profession that was chosen
     */
    public ProfessionChangeEvent(Player player, ProfessionEnum profession) {
        this.player = player;
        this.profession = profession;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ProfessionEnum getProfession() {
        return this.profession;
    }

    private static final HandlerList handlers = new HandlerList();

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}