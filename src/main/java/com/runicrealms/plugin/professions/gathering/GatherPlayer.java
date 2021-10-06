package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.database.PlayerMongoData;
import com.runicrealms.plugin.database.event.CacheSaveEvent;
import com.runicrealms.plugin.player.cache.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Wrapper for player gathering levels
 */
public class GatherPlayer {

    private static final String DATA_SECTION = "gathering.";
    private static final String EXP_SECTION = ".exp";
    private static final String LEVEL_SECTION = ".level";

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

    public GatherPlayer(PlayerCache playerCache, int cookingExp, int cookingLevel, int farmingExp,
                        int farmingLevel, int fishingExp, int fishingLevel, int harvestingExp, int harvestingLevel,
                        int miningExp, int miningLevel, int woodcuttingExp, int woodcuttingLevel) {
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
     * A method used to register a player into the in-memory storage for gathering
     *
     * @param player the player to be registered
     */
    public static void registerGatherPlayer(Player player) {
        PlayerCache playerCache = RunicCoreAPI.getPlayerCache(player);
        PlayerMongoData playerData = (PlayerMongoData) playerCache.getMongoData();
        UUID uuid = player.getUniqueId();

        int cookingExp = 0;
        int cookingLevel = 0;
        int farmingExp = 0;
        int farmingLevel = 0;
        int fishingExp = 0;
        int fishingLevel = 0;
        int harvestingExp = 0;
        int harvestingLevel = 0;
        int miningExp = 0;
        int miningLevel = 0;
        int woodcuttingExp = 0;
        int woodcuttingLevel = 0;
        int[] gatheringSkillsExp = new int[]{cookingExp, farmingExp, fishingExp, harvestingExp, miningExp, woodcuttingExp};
        int[] gatheringSkillsLevel = new int[]{cookingLevel, farmingLevel, fishingLevel, harvestingLevel, miningLevel, woodcuttingLevel};

        // initialize all stored gathering exp values if the value is found
        int i = 0;
        for (GatheringSkill gatheringSkill : GatheringSkill.values()) {
            String gatheringSkillDataSectionKey = DATA_SECTION + gatheringSkill.getIdentifier() + EXP_SECTION;
            if (playerData.has(gatheringSkillDataSectionKey)) {
                gatheringSkillsExp[i] = playerData.get(gatheringSkillDataSectionKey, Integer.class);
            }
            i++;
        }

        // initialize all stored gathering level values if the value is found
        int j = 0;
        for (GatheringSkill gatheringSkill : GatheringSkill.values()) {
            String gatheringSkillDataSectionKey = DATA_SECTION + gatheringSkill.getIdentifier() + LEVEL_SECTION;
            if (playerData.has(gatheringSkillDataSectionKey)) {
                gatheringSkillsLevel[j] = playerData.get(gatheringSkillDataSectionKey, Integer.class);
            }
            j++;
        }

        RunicProfessions.getGatherPlayerManager().getGatherPlayers().put(uuid,
                new GatherPlayer
                        (
                                playerCache,
                                gatheringSkillsExp[0],
                                gatheringSkillsLevel[0],
                                gatheringSkillsExp[1],
                                gatheringSkillsLevel[1],
                                gatheringSkillsExp[2],
                                gatheringSkillsLevel[2],
                                gatheringSkillsExp[3],
                                gatheringSkillsLevel[3],
                                gatheringSkillsExp[4],
                                gatheringSkillsLevel[4],
                                gatheringSkillsExp[5],
                                gatheringSkillsLevel[5]
                        ));
    }

    /**
     * ALWAYS ONLY CALL ON CACHE SAVE EVENT
     *
     * @param event the saving event that we'll listen for
     */
    public void save(CacheSaveEvent event) {
        PlayerMongoData data = event.getMongoData();
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
        try {
            return Bukkit.getPlayer(this.getPlayerCache().getPlayerID());
        } catch (NullPointerException e) {
            Bukkit.getLogger().info(ChatColor.DARK_RED + "Player cache for gather player was not found!");
            return null;
        }
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

}
