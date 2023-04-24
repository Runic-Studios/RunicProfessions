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
import org.bukkit.inventory.ItemStack;

/**
 * This custom event is called when a player catches a fish
 */
public class GatheringEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final GatheringTool gatheringTool;
    private final ItemStack itemStack;
    private final String templateIdOfResource;
    private final Location location;
    private final Block block;
    private final Material placeholderMaterial;
    private final double roll;
    private final Material reagentBlockType;
    private GatheringResource gatheringResource;
    private boolean isCancelled;

    /**
     * Create event with all necessary info to handle gathering
     *
     * @param player               who gathered material
     * @param gatheringResource    the resource being gathered
     * @param gatheringTool        the tool used (wraps runicItemDynamic)
     * @param itemStack            the actual itemStack used to mine
     * @param templateIdOfResource the templateId of the gathered material (iron-ore)
     * @param location             the location of the block to replace
     * @param block                the block itself to replace (not used for fishing)
     * @param placeholderMaterial  the material to set while the block is regenerating (cobblestone)
     * @param roll                 the chance to get a gold coin
     * @param reagentBlockType     for setting the old block type BACK to ore, wheat, etc. (not used for fishing)
     */
    public GatheringEvent(Player player, GatheringResource gatheringResource, GatheringTool gatheringTool,
                          ItemStack itemStack, String templateIdOfResource, Location location, Block block,
                          Material placeholderMaterial, double roll,
                          Material reagentBlockType) {
        this.player = player;
        this.gatheringResource = gatheringResource;
        this.gatheringTool = gatheringTool;
        this.itemStack = itemStack;
        this.templateIdOfResource = templateIdOfResource;
        this.location = location;
        this.block = block;
        this.placeholderMaterial = placeholderMaterial;
        this.roll = roll;
        this.reagentBlockType = reagentBlockType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Block getBlock() {
        return block;
    }

    public GatheringResource getGatheringResource() {
        return gatheringResource;
    }

    public void setGatheringResource(GatheringResource gatheringResource) {
        this.gatheringResource = gatheringResource;
    }

    public GatheringTool getGatheringTool() {
        return gatheringTool;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Location getLocation() {
        return location;
    }

    public Material getPlaceholderMaterial() {
        return placeholderMaterial;
    }

    public Player getPlayer() {
        return player;
    }

    public Material getReagentBlockType() {
        return reagentBlockType;
    }

    public double getRoll() {
        return roll;
    }

    public String getTemplateIdOfResource() {
        return templateIdOfResource;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        this.isCancelled = arg0;
    }
}
