package com.runicrealms.plugin.professions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * This custom event is called when a player catches a fish
 */
public class CustomFishEvent extends Event implements Cancellable {

    private Player player;
    private ItemStack fish;
    private boolean isCancelled;

    public CustomFishEvent(Player player, ItemStack fish) {
        this.player = player;
        this.fish = fish;
        this.isCancelled = false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ItemStack getFish() {
        return this.fish;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
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
