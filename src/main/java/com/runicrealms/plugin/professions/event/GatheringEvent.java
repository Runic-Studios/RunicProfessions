package com.runicrealms.plugin.professions.event;

import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringTool;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This custom event is called when a player gathers an item
 */
public class GatheringEvent extends Event implements Cancellable {
    private final Player player;
    private final GatheringTool gatheringTool;
    private final ItemStack itemStack;
    private final Location location;
    private final Block block;
    private final double roll;
    private final List<GatheringResource> gatheringResources;
    private boolean isCancelled;

    private static final HandlerList handlers = new HandlerList();

    /**
     * Create event with all necessary info to handle gathering
     *
     * @param player             who gathered material
     * @param gatheringResources the resources being gathered (may contain duplicates, each elements represents one item)
     * @param gatheringTool      the tool used (wraps runicItemDynamic)
     * @param itemStack          the actual itemStack used to mine
     * @param location           the location of the block to replace
     * @param block              the block itself to replace (not used for fishing)
     * @param roll               the chance to get a gold coin
     */
    public GatheringEvent(@NotNull Player player, @NotNull List<GatheringResource> gatheringResources, @NotNull GatheringTool gatheringTool,
                          @NotNull ItemStack itemStack, @NotNull Location location, @Nullable Block block, double roll) {
        this.player = player;
        this.gatheringResources = gatheringResources;
        this.gatheringTool = gatheringTool;
        this.itemStack = itemStack;
        this.location = location;
        this.block = block;
        this.roll = roll;
    }

    /**
     * Create event with all necessary info to handle gathering
     *
     * @param player            who gathered material
     * @param gatheringResource the resource being gathered
     * @param gatheringTool     the tool used (wraps runicItemDynamic)
     * @param itemStack         the actual itemStack used to mine
     * @param location          the location of the block to replace
     * @param block             the block itself to replace (not used for fishing)
     * @param roll              the chance to get a gold coin
     */
    public GatheringEvent(@NotNull Player player, @NotNull GatheringResource gatheringResource, @NotNull GatheringTool gatheringTool,
                          @NotNull ItemStack itemStack, @NotNull Location location, @Nullable Block block, double roll) {
        this(player, new ArrayList<>(Collections.singletonList(gatheringResource)), gatheringTool, itemStack, location, block, roll);
    }

    @NotNull
    public List<GatheringResource> getGatheringResources() {
        return this.gatheringResources;
    }

    @Nullable
    public Block getBlock() {
        return block;
    }

    @NotNull
    public GatheringTool getGatheringTool() {
        return gatheringTool;
    }

    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public double getRoll() {
        return roll;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
