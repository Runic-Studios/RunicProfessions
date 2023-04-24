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
    WHEAT("wheat", GatheringSkill.FARMING, GatheringRegion.FARM, Material.WHEAT, Material.AIR),
    CARROTS("carrot", GatheringSkill.FARMING, GatheringRegion.FARM, Material.CARROTS, Material.AIR),
    POTATOES("potato", GatheringSkill.FARMING, GatheringRegion.FARM, Material.POTATOES, Material.AIR),
    MELON("melon", GatheringSkill.FARMING, GatheringRegion.FARM, Material.MELON, Material.AIR),

    /*
    Fishing
     */
    SALMON("salmon", GatheringSkill.FISHING, GatheringRegion.POND, Material.SALMON, Material.AIR),
    COD("cod", GatheringSkill.FISHING, GatheringRegion.POND, Material.COD, Material.AIR),
    PRAWN("prawn", GatheringSkill.FISHING, GatheringRegion.POND, Material.MAGENTA_STAINED_GLASS, Material.AIR),
    RED_SNAPPER("red-snapper", GatheringSkill.FISHING, GatheringRegion.POND, Material.ORANGE_STAINED_GLASS, Material.AIR),
    SHRIMP("shrimp", GatheringSkill.FISHING, GatheringRegion.POND, Material.PINK_STAINED_GLASS, Material.AIR),
    MANAFISH("manafish", GatheringSkill.FISHING, GatheringRegion.POND, Material.LIGHT_BLUE_STAINED_GLASS, Material.AIR),
    EEL("murkwood-eel", GatheringSkill.FISHING, GatheringRegion.POND, Material.GREEN_STAINED_GLASS, Material.AIR),
    ANGELFISH("angelfish", GatheringSkill.FISHING, GatheringRegion.POND, Material.WHITE_STAINED_GLASS, Material.AIR),
    CARP("carp", GatheringSkill.FISHING, GatheringRegion.POND, Material.BROWN_STAINED_GLASS, Material.AIR),
    OASIS_DRIFTER("oasis-drifter", GatheringSkill.FISHING, GatheringRegion.POND, Material.PURPLE_STAINED_GLASS, Material.AIR),
    TROPICAL_FISH("tropical", GatheringSkill.FISHING, GatheringRegion.POND, Material.TROPICAL_FISH, Material.AIR),
    CRAB("crab", GatheringSkill.FISHING, GatheringRegion.POND, Material.YELLOW_STAINED_GLASS, Material.AIR),
    LAVA_EEL("lava-eel", GatheringSkill.FISHING, GatheringRegion.POND, Material.LIME_STAINED_GLASS, Material.AIR),
    MONKFISH("monkfish", GatheringSkill.FISHING, GatheringRegion.POND, Material.GRAY_STAINED_GLASS, Material.AIR),
    STRONGFISH("strongfish", GatheringSkill.FISHING, GatheringRegion.POND, Material.BLACK_STAINED_GLASS, Material.AIR),
    PUFFERFISH("pufferfish", GatheringSkill.FISHING, GatheringRegion.POND, Material.PUFFERFISH, Material.AIR),
    VAMPIRE_SQUID("vampire-squid", GatheringSkill.FISHING, GatheringRegion.POND, Material.RED_STAINED_GLASS, Material.AIR),
    FROSTFIN_TUNA("frostfin-tuna", GatheringSkill.FISHING, GatheringRegion.POND, Material.BLUE_STAINED_GLASS, Material.AIR),

    /*
    Harvesting
     */
    COMFREY("comfrey", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    TURMERIC("turmeric", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    PSYLLIUM("psyllium", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    PETUNIA("petunia", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    VALERIAN("valerian", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    SNOWDROP("snowdrop", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    CHAMOMILE("chamomile", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    WINTERCRESS("wintercress", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    HIBISCUS("hibiscus", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    LAVENDER("lavender", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    BOSWELLIA("boswellia", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),
    ARUGULA("arugula", GatheringSkill.HARVESTING, null, Material.AIR, Material.AIR),

    /*
    Mining
     */
    IRON_ORE("iron-ore", GatheringSkill.MINING, GatheringRegion.MINE, Material.IRON_ORE, Material.COBBLESTONE),
    GOLD_ORE("gold-ore", GatheringSkill.MINING, GatheringRegion.MINE, Material.GOLD_ORE, Material.COBBLESTONE),
    UNCUT_RUBY("uncut-ruby", GatheringSkill.MINING, GatheringRegion.MINE, Material.REDSTONE_ORE, Material.COBBLESTONE),
    UNCUT_SAPPHIRE("uncut-sapphire", GatheringSkill.MINING, GatheringRegion.MINE, Material.LAPIS_ORE, Material.COBBLESTONE),
    UNCUT_DIAMOND("uncut-diamond", GatheringSkill.MINING, GatheringRegion.MINE, Material.DIAMOND_ORE, Material.COBBLESTONE),
    UNCUT_EMERALD("uncut-emerald", GatheringSkill.MINING, GatheringRegion.MINE, Material.EMERALD_ORE, Material.COBBLESTONE),
    UNCUT_OPAL("uncut-opal", GatheringSkill.MINING, GatheringRegion.MINE, Material.NETHER_QUARTZ_ORE, Material.COBBLESTONE),

    IRON_ORE_MODERATE("iron-ore-moderate", GatheringSkill.MINING, GatheringRegion.MINE, Material.IRON_ORE, Material.COBBLESTONE),
    GOLD_ORE_MODERATE("gold-ore-moderate", GatheringSkill.MINING, GatheringRegion.MINE, Material.GOLD_ORE, Material.COBBLESTONE),
    UNCUT_RUBY_MODERATE("uncut-ruby-moderate", GatheringSkill.MINING, GatheringRegion.MINE, Material.REDSTONE_ORE, Material.COBBLESTONE),
    UNCUT_SAPPHIRE_MODERATE("uncut-sapphire-moderate", GatheringSkill.MINING, GatheringRegion.MINE, Material.LAPIS_ORE, Material.COBBLESTONE),
    UNCUT_DIAMOND_MODERATE("uncut-diamond-moderate", GatheringSkill.MINING, GatheringRegion.MINE, Material.DIAMOND_ORE, Material.COBBLESTONE),
    UNCUT_EMERALD_MODERATE("uncut-emerald-moderate", GatheringSkill.MINING, GatheringRegion.MINE, Material.EMERALD_ORE, Material.COBBLESTONE),
    UNCUT_OPAL_MODERATE("uncut-opal-moderate", GatheringSkill.MINING, GatheringRegion.MINE, Material.NETHER_QUARTZ_ORE, Material.COBBLESTONE),

    IRON_ORE_EXCELLENT("iron-ore-excellent", GatheringSkill.MINING, GatheringRegion.MINE, Material.IRON_ORE, Material.COBBLESTONE),
    GOLD_ORE_EXCELLENT("gold-ore-excellent", GatheringSkill.MINING, GatheringRegion.MINE, Material.GOLD_ORE, Material.COBBLESTONE),
    UNCUT_RUBY_EXCELLENT("uncut-ruby-excellent", GatheringSkill.MINING, GatheringRegion.MINE, Material.REDSTONE_ORE, Material.COBBLESTONE),
    UNCUT_SAPPHIRE_EXCELLENT("uncut-sapphire-excellent", GatheringSkill.MINING, GatheringRegion.MINE, Material.LAPIS_ORE, Material.COBBLESTONE),
    UNCUT_DIAMOND_EXCELLENT("uncut-diamond-excellent", GatheringSkill.MINING, GatheringRegion.MINE, Material.DIAMOND_ORE, Material.COBBLESTONE),
    UNCUT_EMERALD_EXCELLENT("uncut-emerald-excellent", GatheringSkill.MINING, GatheringRegion.MINE, Material.EMERALD_ORE, Material.COBBLESTONE),
    UNCUT_OPAL_EXCELLENT("uncut-opal-excellent", GatheringSkill.MINING, GatheringRegion.MINE, Material.NETHER_QUARTZ_ORE, Material.COBBLESTONE),

    IRON_ORE_PURE("iron-ore-pure", GatheringSkill.MINING, GatheringRegion.MINE, Material.IRON_ORE, Material.COBBLESTONE),
    GOLD_ORE_PURE("gold-ore-pure", GatheringSkill.MINING, GatheringRegion.MINE, Material.GOLD_ORE, Material.COBBLESTONE),
    UNCUT_RUBY_PURE("uncut-ruby-pure", GatheringSkill.MINING, GatheringRegion.MINE, Material.REDSTONE_ORE, Material.COBBLESTONE),
    UNCUT_SAPPHIRE_PURE("uncut-sapphire-pure", GatheringSkill.MINING, GatheringRegion.MINE, Material.LAPIS_ORE, Material.COBBLESTONE),
    UNCUT_DIAMOND_PURE("uncut-diamond-pure", GatheringSkill.MINING, GatheringRegion.MINE, Material.DIAMOND_ORE, Material.COBBLESTONE),
    UNCUT_EMERALD_PURE("uncut-emerald-pure", GatheringSkill.MINING, GatheringRegion.MINE, Material.EMERALD_ORE, Material.COBBLESTONE),
    UNCUT_OPAL_PURE("uncut-opal-pure", GatheringSkill.MINING, GatheringRegion.MINE, Material.NETHER_QUARTZ_ORE, Material.COBBLESTONE),

    /*
    Woodcutting
     */
    OAK_WOOD("oak-wood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.OAK_WOOD, Material.OAK_PLANKS),
    SPRUCE_WOOD("spruce-wood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.SPRUCE_WOOD, Material.SPRUCE_PLANKS),
    BIRCH_WOOD("birch-wood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.BIRCH_WOOD, Material.BIRCH_PLANKS),
    JUNGLE_WOOD("jungle-wood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.JUNGLE_WOOD, Material.JUNGLE_PLANKS),
    ACACIA_WOOD("acacia-wood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.ACACIA_WOOD, Material.ACACIA_PLANKS),
    DARK_OAK_WOOD("dark-oak-wood", GatheringSkill.WOODCUTTING, GatheringRegion.GROVE, Material.DARK_OAK_WOOD, Material.DARK_OAK_PLANKS);

    private final String templateId;
    private final GatheringSkill gatheringSkill;
    private final GatheringRegion gatheringRegion;
    private final Material resourceBlockType;
    private final int requiredLevel;
    private final int experience;
    private final Material placeholderBlockType;

    /**
     * Bundles some handy information on the gathering resource being mined
     *
     * @param templateId           the templateId of the resource to grant
     * @param gatheringSkill       the associated gathering skill
     * @param gatheringRegion      the identifier of the required gathering region
     * @param resourceBlockType    the block that displays when the resource can be gathered
     * @param placeholderBlockType the block to be placed while the resource is on cooldown
     *                             requiredLevel - the min. level for the required gathering skill
     *                             experience - the experience earned per unit
     */
    GatheringResource(String templateId, GatheringSkill gatheringSkill, @Nullable GatheringRegion gatheringRegion,
                      Material resourceBlockType, Material placeholderBlockType) {
        this.templateId = templateId;
        this.gatheringSkill = gatheringSkill;
        this.gatheringRegion = gatheringRegion;
        this.resourceBlockType = resourceBlockType;
        this.placeholderBlockType = placeholderBlockType;
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
     * @param templateId of the gathering resource "crab"
     * @return the gathering resource enum
     */
    public static GatheringResource getFromTemplateId(String templateId) {
        for (GatheringResource gatheringResource : GatheringResource.values()) {
            if (gatheringResource.getTemplateId().equalsIgnoreCase(templateId))
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

    public int getExperience() {
        return experience;
    }

    public GatheringRegion getGatheringRegion() {
        return gatheringRegion;
    }

    public GatheringSkill getGatheringSkill() {
        return gatheringSkill;
    }

    public Material getPlaceholderBlockType() {
        return placeholderBlockType;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public Material getResourceBlockType() {
        return resourceBlockType;
    }

    public String getTemplateId() {
        return templateId;
    }
}
