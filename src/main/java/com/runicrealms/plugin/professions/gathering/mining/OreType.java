package com.runicrealms.plugin.professions.gathering.mining;

import org.bukkit.Material;

public enum OreType {
    DIAMOND("ore_diamond", Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE),
    EMERALD("ore_emerald", Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE),
    GOLD("ore_gold", Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE),
    IRON("ore_iron", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
    OPAL("ore_topaz", Material.NETHER_QUARTZ_ORE, Material.DEEPSLATE_COAL_ORE),
    RUBY("ore_ruby", Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE),
    SAPPHIRE("ore_sapphire", Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE);

    private final String modelId;
    private final Material materialSecondTier;
    private final Material materialFourthTier;

    /**
     * @param modelId    the string identifier of the model (used for model engine)
     * @param secondTier the material of the node if this is spawned at tier two
     * @param fourthTier the material of the node if this is spawned at tier four
     */
    OreType(String modelId, Material secondTier, Material fourthTier) {
        this.modelId = modelId;
        this.materialSecondTier = secondTier;
        this.materialFourthTier = fourthTier;
    }

    public String getModelId() {
        return modelId;
    }

    /**
     * Determines the material of the ore node based on type
     *
     * @param oreTier tier of the ore (one, two, three, four)
     * @return the material of the ore node
     */
    public Material getMaterial(OreTier oreTier) {
        if (oreTier == OreTier.ONE || oreTier == OreTier.TWO)
            return materialSecondTier;
        else if (oreTier == OreTier.THREE || oreTier == OreTier.FOUR)
            return materialFourthTier;
        return Material.STONE;
    }
}
