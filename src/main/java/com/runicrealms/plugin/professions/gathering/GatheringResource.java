package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.professions.utilities.GatheringUtil;
import com.runicrealms.plugin.professions.utilities.ProfUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A handy class that bundles some key data for gathering resources
 */
public enum GatheringResource {

    /*
    Farming
     */
    WHEAT("Wheat", GatheringSkill.FARMING, GatheringRegion.FARM, Material.WHEAT, Material.AIR, "+ Wheat"),
    CARROTS("carrot", GatheringSkill.FARMING, GatheringRegion.FARM, Material.CARROTS, Material.AIR, "+ Carrot"),
    POTATOES("potato", GatheringSkill.FARMING, GatheringRegion.FARM, Material.POTATOES, Material.AIR, "+ Potato"),
    MELON("melon", GatheringSkill.FARMING, GatheringRegion.FARM, Material.MELON, Material.AIR, "+ Melon"),

    /*
    Fishing
     */
    SALMON("Salmon", GatheringSkill.FISHING, GatheringRegion.POND, Material.SALMON, Material.AIR, "+ Salmon"),
    COD("Cod", GatheringSkill.FISHING, GatheringRegion.POND, Material.COD, Material.AIR, "+ Cod"),
    TROPICAL_FISH("Tropical", GatheringSkill.FISHING, GatheringRegion.POND, Material.TROPICAL_FISH, Material.AIR, "+ Tropical"),
    PUFFERFISH("Pufferfish", GatheringSkill.FISHING, GatheringRegion.POND, Material.PUFFERFISH, Material.AIR, "+ Pufferfish"),

    /*
    Harvesting
     */
    COMFREY("Comfrey", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    TURMERIC("Turmeric", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    PSYLLIUM("Psyllium", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    PETUNIA("Petunia", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    VALERIAN("Valerian", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    SNOWDROP("Snowdrop", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    CHAMOMILE("Chamomile", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    WINTERCRESS("Wintercress", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    HIBISCUS("Hibiscus", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    LAVENDER("Lavender", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    BOSWELLIA("Boswellia", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),
    ARUGULA("Arugula", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR, ""),

    /*
    Mining
     */
    IRON_ORE("iron-ore", GatheringSkill.MINING, GatheringRegion.MINE, Material.IRON_ORE, Material.COBBLESTONE, "+ Iron"),
    GOLD_ORE("gold-ore", GatheringSkill.MINING, GatheringRegion.MINE, Material.GOLD_ORE, Material.COBBLESTONE, "+ Gold"),
    UNCUT_RUBY("uncut-ruby", GatheringSkill.MINING, GatheringRegion.MINE, Material.REDSTONE_ORE, Material.COBBLESTONE, "+ Ruby"),
    UNCUT_SAPPHIRE("uncut-sapphire", GatheringSkill.MINING, GatheringRegion.MINE, Material.LAPIS_ORE, Material.COBBLESTONE, "+ Sapphire"),
    UNCUT_OPAL("uncut-opal", GatheringSkill.MINING, GatheringRegion.MINE, Material.NETHER_QUARTZ_ORE, Material.COBBLESTONE, "+ Opal"),
    UNCUT_EMERALD("uncut-emerald", GatheringSkill.MINING, GatheringRegion.MINE, Material.EMERALD_ORE, Material.COBBLESTONE, "+ Emerald"),
    UNCUT_DIAMOND("uncut-diamond", GatheringSkill.MINING, GatheringRegion.MINE, Material.DIAMOND_ORE, Material.COBBLESTONE, "+ Diamond"),

    /*
    Woodcutting
     */
    OAK_WOOD("OakWood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.OAK_WOOD, Material.OAK_PLANKS, "+ Oak"),
    SPRUCE_WOOD("SpruceWood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.SPRUCE_WOOD, Material.SPRUCE_PLANKS, "+ Spruce"),
    BIRCH_WOOD("BirchWood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.BIRCH_WOOD, Material.BIRCH_PLANKS, "+ Elder"),
    JUNGLE_WOOD("JungleWood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.JUNGLE_WOOD, Material.JUNGLE_PLANKS, "+ Jungle"),
    ACACIA_WOOD("AcaciaWood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.ACACIA_WOOD, Material.ACACIA_PLANKS, "+ Acacia"),
    DARK_OAK_WOOD("DarkOakWood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.DARK_OAK_WOOD, Material.DARK_OAK_PLANKS, "+ Dark Oak");

    private final String templateId;
    private final GatheringSkill gatheringSkill;
    private final GatheringRegion gatheringRegion;
    private final Material resourceBlockType;
    private final int requiredLevel;
    private final int experience;
    private final Material placeholderBlockType;
    private final String hologramDisplayString;

    /**
     * Bundles some handy information on the gathering resource being mined
     *
     * @param templateId            the templateId of the resource to grant
     * @param gatheringSkill        the associated gathering skill
     * @param gatheringRegion       the identifier of the required gathering region
     * @param resourceBlockType     the block that displays when the resource can be gathered
     * @param placeholderBlockType  the block to be placed while the resource is on cooldown
     * @param hologramDisplayString the hologram to display on gather
     *                              requiredLevel - the min. level for the required gathering skill
     *                              experience - the experience earned per unit
     */
    GatheringResource(String templateId, GatheringSkill gatheringSkill, @Nullable GatheringRegion gatheringRegion,
                      Material resourceBlockType, Material placeholderBlockType, String hologramDisplayString) {
        this.templateId = templateId;
        this.gatheringSkill = gatheringSkill;
        this.gatheringRegion = gatheringRegion;
        this.resourceBlockType = resourceBlockType;
        this.placeholderBlockType = placeholderBlockType;
        this.hologramDisplayString = hologramDisplayString;
        this.requiredLevel = ProfUtil.getRunicItemDataFieldInt(RunicItemsAPI.generateItemFromTemplate(templateId), ProfUtil.REQUIRED_LEVEL_KEY);
        this.experience = ProfUtil.getRunicItemDataFieldInt(RunicItemsAPI.generateItemFromTemplate(templateId), ProfUtil.EXPERIENCE_KEY);
    }

    /**
     * Matches a GatheringResource enum value to the block type which was mined (wheat, iron ore, etc.)
     *
     * @param resourceBlockType the material of the block which was gathered
     * @return a GatheringResource wrapper
     */
    public static GatheringResource getFromResourceBlockType(Material resourceBlockType) {
        for (GatheringResource gatheringResource : GatheringResource.values()) {
            if (gatheringResource.getResourceBlockType() == resourceBlockType)
                return gatheringResource;
        }
        return null;
    }

    /**
     * Based on the associated gathering skill of the resource (farming, mining, etc.), returns a set of tools,
     * any of which the player must be holding
     *
     * @param gatheringResource which was mined (iron ore, etc.)
     * @return a set of tools to check if they are holding
     */
    public static Set<GatheringTool> determineToolSet(GatheringResource gatheringResource) {
        switch (gatheringResource.getGatheringSkill()) {
            case FARMING:
                return GatheringUtil.getHoes();
            case FISHING:
                return GatheringUtil.getRods();
            case MINING:
                return GatheringUtil.getPickaxes();
            case WOODCUTTING:
                return GatheringUtil.getAxes();
        }
        return null;
    }

    public String getTemplateId() {
        return templateId;
    }

    public GatheringSkill getGatheringSkill() {
        return gatheringSkill;
    }

    public GatheringRegion getGatheringRegion() {
        return gatheringRegion;
    }

    public Material getResourceBlockType() {
        return resourceBlockType;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getExperience() {
        return experience;
    }

    public Material getPlaceholderBlockType() {
        return placeholderBlockType;
    }

    public String getHologramDisplayString() {
        return hologramDisplayString;
    }
}
