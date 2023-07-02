package com.runicrealms.plugin.professions.gathering.mining;

import org.bukkit.Material;

public enum OreType {

    DIAMOND(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE),
    EMERALD(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE),
    GOLD(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE),
    IRON(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
    OPAL(Material.NETHER_QUARTZ_ORE, Material.DEEPSLATE_COAL_ORE),
    RUBY(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE),
    SAPPHIRE(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE);

    private final Material materialSecondTier;
    private final Material materialFourthTier;

    /**
     * @param secondTier the material of the node if this is spawned at tier two
     * @param fourthTier the material of the node if this is spawned at tier four
     */
    OreType(Material secondTier, Material fourthTier) {
        this.materialSecondTier = secondTier;
        this.materialFourthTier = fourthTier;
    }

    /**
     * Determines the material of the ore node based on type
     *
     * @param oreTier should be either TWO or FOUR
     * @return the material of the ore node
     */
    public Material getMaterial(OreTier oreTier) {
        if (oreTier == OreTier.TWO)
            return materialSecondTier;
        else if (oreTier == OreTier.FOUR)
            return materialFourthTier;
        return Material.STONE;
    }
}
