package com.runicrealms.plugin.professions.event;

import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This custom event is called when a player catches a fish
 */
public class GatheringEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final GatheringResource gatheringResource;
    private final GatheringTool gatheringTool;
    private final String templateIdOfResource;
    private final Location location;
    private final Block block;
    private final Material placeholderMaterial;
    private final String hologramItemName;
    private final double roll;
    private final Material reagentBlockType;
    private boolean isCancelled;

    /**
     * Create event with all necessary info to handle gathering
     *
     * @param player               who gathered material
     * @param gatheringResource    the resource being gathered
     * @param gatheringTool        the tool used
     * @param templateIdOfResource the templateId of the gathered material (iron-ore)
     * @param location             the location of the block to replace
     * @param block                the block itself to replace
     * @param placeholderMaterial  the material to set while the block is regenerating (cobblestone)
     * @param hologramItemName     the hologram to display upon successful gathering
     * @param roll                 the chance to get a gold coin
     * @param reagentBlockType     for setting the old block type BACK to ore, wheat, etc. (not used for fishing)
     */
    public GatheringEvent(Player player, GatheringResource gatheringResource, GatheringTool gatheringTool,
                          String templateIdOfResource, Location location, Block block, Material placeholderMaterial,
                          String hologramItemName, double roll, Material reagentBlockType) {
        this.player = player;
        this.gatheringResource = gatheringResource;
        this.gatheringTool = gatheringTool;
        this.templateIdOfResource = templateIdOfResource;
        this.location = location;
        this.block = block;
        this.placeholderMaterial = placeholderMaterial;
        this.hologramItemName = hologramItemName;
        this.roll = roll;
        this.reagentBlockType = reagentBlockType;
    }

    // todo second event with fish info

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public GatheringResource getGatheringResource() {
        return gatheringResource;
    }

    public GatheringTool getGatheringTool() {
        return gatheringTool;
    }

    public String getTemplateIdOfResource() {
        return templateIdOfResource;
    }

    public Location getLocation() {
        return location;
    }

    public Block getBlock() {
        return block;
    }

    public Material getPlaceholderMaterial() {
        return placeholderMaterial;
    }

    public String getHologramItemName() {
        return hologramItemName;
    }

    public double getRoll() {
        return roll;
    }

    public Material getReagentBlockType() {
        return reagentBlockType;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
