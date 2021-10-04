package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.database.PlayerMongoData;
import com.runicrealms.plugin.database.PlayerMongoDataSection;
import com.runicrealms.plugin.player.cache.PlayerCache;
import org.bukkit.entity.Player;

/**
 * Wrapper for player gathering levels
 */
public class GatherPlayer {

    private static final String DATA_SECTION = "prof.gathering.";
    private static final String EXP_SECTION = ".exp";
    private static final String LEVEL_SECTION = ".level";

    private final Player player;
    private final PlayerCache playerCache;
    private int cookingExp;
    private int cookingLevel;
    private int farmingExp;
    private int farmingLevel;
    private int fishingExp;
    private int fishingLevel;
    private int harvestingExp;
    private int harvestingLevel;
    private int miningExp;
    private int miningLevel;
    private int woodcuttingExp;
    private int woodcuttingLevel;

    public GatherPlayer(Player player, PlayerCache playerCache, int cookingExp, int cookingLevel, int farmingExp,
                        int farmingLevel, int fishingExp, int fishingLevel, int harvestingExp, int harvestingLevel,
                        int miningExp, int miningLevel, int woodcuttingExp, int woodcuttingLevel) {
        this.player = player;
        this.playerCache = playerCache;
        this.cookingExp = cookingExp;
        this.cookingLevel = cookingLevel;
        this.farmingExp = farmingExp;
        this.farmingLevel = farmingLevel;
        this.fishingExp = fishingExp;
        this.fishingLevel = fishingLevel;
        this.harvestingExp = harvestingExp;
        this.harvestingLevel = harvestingLevel;
        this.miningExp = miningExp;
        this.miningLevel = miningLevel;
        this.woodcuttingExp = woodcuttingExp;
        this.woodcuttingLevel = woodcuttingLevel;
    }

    /**
     * ALWAYS ONLY CALL ON CACHE SAVE EVENT
     *
     * @param playerData the player data
     */
    public void save(PlayerMongoData playerData) {
        int slot = this.playerCache.getCharacterSlot();
        PlayerMongoDataSection data = playerData.getCharacter(slot);
        data.set(DATA_SECTION + GatheringSkill.COOKING.getIdentifier() + EXP_SECTION, this.getCookingExp());
        data.set(DATA_SECTION + GatheringSkill.COOKING.getIdentifier() + LEVEL_SECTION, this.getCookingLevel());
        data.set(DATA_SECTION + GatheringSkill.FARMING.getIdentifier() + EXP_SECTION, this.getFarmingExp());
        data.set(DATA_SECTION + GatheringSkill.FARMING.getIdentifier() + LEVEL_SECTION, this.getFarmingLevel());
        data.set(DATA_SECTION + GatheringSkill.FISHING.getIdentifier() + EXP_SECTION, this.getFishingExp());
        data.set(DATA_SECTION + GatheringSkill.FISHING.getIdentifier() + LEVEL_SECTION, this.getFishingLevel());
        data.set(DATA_SECTION + GatheringSkill.HARVESTING.getIdentifier() + EXP_SECTION, this.getHarvestingExp());
        data.set(DATA_SECTION + GatheringSkill.HARVESTING.getIdentifier() + LEVEL_SECTION, this.getHarvestingLevel());
        data.set(DATA_SECTION + GatheringSkill.MINING.getIdentifier() + EXP_SECTION, this.getMiningExp());
        data.set(DATA_SECTION + GatheringSkill.MINING.getIdentifier() + LEVEL_SECTION, this.getMiningLevel());
        data.set(DATA_SECTION + GatheringSkill.WOODCUTTING.getIdentifier() + EXP_SECTION, this.getWoodcuttingExp());
        data.set(DATA_SECTION + GatheringSkill.WOODCUTTING.getIdentifier() + LEVEL_SECTION, this.getWoodcuttingLevel());
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerCache getPlayerCache() {
        return this.playerCache;
    }

    public int getCookingExp() {
        return cookingExp;
    }

    public void setCookingExp(int cookingExp) {
        this.cookingExp = cookingExp;
    }

    public int getCookingLevel() {
        return cookingLevel;
    }

    public void setCookingLevel(int cookingLevel) {
        this.cookingLevel = cookingLevel;
    }

    public int getFarmingExp() {
        return farmingExp;
    }

    public void setFarmingExp(int farmingExp) {
        this.farmingExp = farmingExp;
    }

    public int getFarmingLevel() {
        return farmingLevel;
    }

    public void setFarmingLevel(int farmingLevel) {
        this.farmingLevel = farmingLevel;
    }

    public int getFishingExp() {
        return fishingExp;
    }

    public void setFishingExp(int fishingExp) {
        this.fishingExp = fishingExp;
    }

    public int getFishingLevel() {
        return fishingLevel;
    }

    public void setFishingLevel(int fishingLevel) {
        this.fishingLevel = fishingLevel;
    }

    public int getHarvestingExp() {
        return harvestingExp;
    }

    public void setHarvestingExp(int harvestingExp) {
        this.harvestingExp = harvestingExp;
    }

    public int getHarvestingLevel() {
        return harvestingLevel;
    }

    public void setHarvestingLevel(int harvestingLevel) {
        this.harvestingLevel = harvestingLevel;
    }

    public int getMiningExp() {
        return miningExp;
    }

    public void setMiningExp(int miningExp) {
        this.miningExp = miningExp;
    }

    public int getMiningLevel() {
        return miningLevel;
    }

    public void setMiningLevel(int miningLevel) {
        this.miningLevel = miningLevel;
    }

    public int getWoodcuttingExp() {
        return woodcuttingExp;
    }

    public void setWoodcuttingExp(int woodcuttingExp) {
        this.woodcuttingExp = woodcuttingExp;
    }

    public int getWoodcuttingLevel() {
        return woodcuttingLevel;
    }

    public void setWoodcuttingLevel(int woodcuttingLevel) {
        this.woodcuttingLevel = woodcuttingLevel;
    }

    enum GatheringSkill {

        COOKING("cooking"),
        FARMING("farming"),
        FISHING("fishing"),
        HARVESTING("harvesting"),
        MINING("mining"),
        WOODCUTTING("woodcutting");

        private final String identifier;

        GatheringSkill(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }
    }
}
