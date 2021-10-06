package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.professions.utilities.GatheringUtil;
import com.runicrealms.runicitems.item.RunicItemDynamic;

/**
 * A wrapper for gathering tools that provides some handy info
 */
public enum GatheringTool {

    /*
    Farming
     */
    GATHERING_HOE_APPRENTICE(GatheringUtil.GATHERING_HOE_APPRENTICE, GatheringSkill.FARMING),
    GATHERING_HOE_ADEPT(GatheringUtil.GATHERING_HOE_ADEPT, GatheringSkill.FARMING),
    GATHERING_HOE_REFINED(GatheringUtil.GATHERING_HOE_REFINED, GatheringSkill.FARMING),
    GATHERING_HOE_MASTER(GatheringUtil.GATHERING_HOE_MASTER, GatheringSkill.FARMING),
    GATHERING_HOE_ARTISAN(GatheringUtil.GATHERING_HOE_ARTISAN, GatheringSkill.FARMING),

    /*
    Fishing
     */
    GATHERING_ROD_APPRENTICE(GatheringUtil.GATHERING_ROD_APPRENTICE, GatheringSkill.FISHING),
    GATHERING_ROD_ADEPT(GatheringUtil.GATHERING_ROD_ADEPT, GatheringSkill.FISHING),
    GATHERING_ROD_REFINED(GatheringUtil.GATHERING_ROD_REFINED, GatheringSkill.FISHING),
    GATHERING_ROD_MASTER(GatheringUtil.GATHERING_ROD_MASTER, GatheringSkill.FISHING),
    GATHERING_ROD_ARTISAN(GatheringUtil.GATHERING_ROD_ARTISAN, GatheringSkill.FISHING),

    /*
    Mining
     */
    GATHERING_PICKAXE_APPRENTICE(GatheringUtil.GATHERING_PICKAXE_APPRENTICE, GatheringSkill.MINING),
    GATHERING_PICKAXE_ADEPT(GatheringUtil.GATHERING_PICKAXE_ADEPT, GatheringSkill.MINING),
    GATHERING_PICKAXE_REFINED(GatheringUtil.GATHERING_PICKAXE_REFINED, GatheringSkill.MINING),
    GATHERING_PICKAXE_MASTER(GatheringUtil.GATHERING_PICKAXE_MASTER, GatheringSkill.MINING),
    GATHERING_PICKAXE_ARTISAN(GatheringUtil.GATHERING_PICKAXE_ARTISAN, GatheringSkill.MINING),

    /*
    Woodcutting
     */
    GATHERING_AXE_APPRENTICE(GatheringUtil.GATHERING_AXE_APPRENTICE, GatheringSkill.WOODCUTTING),
    GATHERING_AXE_ADEPT(GatheringUtil.GATHERING_AXE_ADEPT, GatheringSkill.WOODCUTTING),
    GATHERING_AXE_REFINED(GatheringUtil.GATHERING_AXE_REFINED, GatheringSkill.WOODCUTTING),
    GATHERING_AXE_MASTER(GatheringUtil.GATHERING_AXE_MASTER, GatheringSkill.WOODCUTTING),
    GATHERING_AXE_ARTISAN(GatheringUtil.GATHERING_AXE_ARTISAN, GatheringSkill.WOODCUTTING);

    private static final String REQUIRED_LEVEL_KEY = "requiredLevel";
    private static final String BONUS_LOOT_CEILING_KEY = "bonusLootCeiling";
    private static final String BONUS_LOOT_CHANCE_KEY = "bonusLootChance";
    private final RunicItemDynamic runicItemDynamic;
    private final GatheringSkill gatheringSkill;
    private final int requiredLevel;
    private final int bonusLootAmount;
    private final double bonusLootChance;

    /**
     * Bundles together useful information for a gathering tool
     *
     * @param runicItemDynamic the underlying RunicItemDynamic
     * @param gatheringSkill   the gathering skill the tool corresponds to
     *                         requiredLevel - the min. level for the required gathering skill
     *                         bonusLootCeiling - the maximum amount of extra loot to earn
     *                         bonusLootChance - the chance to earn extra loot
     */
    GatheringTool(RunicItemDynamic runicItemDynamic, GatheringSkill gatheringSkill) {
        this.runicItemDynamic = runicItemDynamic;
        this.gatheringSkill = gatheringSkill;
        this.requiredLevel = GatheringUtil.getRunicItemDataFieldInt(runicItemDynamic, REQUIRED_LEVEL_KEY);
        this.bonusLootAmount = GatheringUtil.getRunicItemDataFieldInt(runicItemDynamic, BONUS_LOOT_CEILING_KEY);
        this.bonusLootChance = GatheringUtil.getRunicItemDataFieldInt(runicItemDynamic, BONUS_LOOT_CHANCE_KEY);
    }

    public RunicItemDynamic getRunicItemDynamic() {
        return runicItemDynamic;
    }

    public GatheringSkill getGatheringSkill() {
        return gatheringSkill;
    }

    public int getRunicItemDataField() {
        return requiredLevel;
    }

    public int getBonusLootAmount() {
        return bonusLootAmount;
    }

    public double getBonusLootChance() {
        return bonusLootChance;
    }
}
