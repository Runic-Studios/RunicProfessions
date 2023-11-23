package com.runicrealms.plugin.professions.event;

import com.runicrealms.plugin.professions.crafting.CraftedResource;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is called when an item is about to be crafted
 *
 * @author BoBoBalloon
 */
public class RunicCraftEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final CraftedResource product;
    private int amount;
    private boolean cancelled;

    public RunicCraftEvent(@NotNull Player who, @NotNull CraftedResource product, int amount) {
        super(who);
        this.product = product;
        this.amount = Math.max(amount, 1);
        this.cancelled = false;
    }

    @NotNull
    public CraftedResource getProduct() {
        return this.product;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(amount, 1);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}