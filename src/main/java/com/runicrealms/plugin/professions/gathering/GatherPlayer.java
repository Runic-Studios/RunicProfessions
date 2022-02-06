package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.database.PlayerMongoData;
import com.runicrealms.plugin.database.event.CacheSaveEvent;
import com.runicrealms.plugin.player.cache.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Wrapper for player gathering levels
 */
public class GatherPlayer {

    private static final String DATA_SECTION = "gathering.";
    private static final String EXP_SECTION = ".exp";
    private static final String LEVEL_SECTION = ".level";

    private final PlayerCache playerCache;
    private final GatheringData gatheringData;
    private final boolean hasSpecializations;

    /**
     * This...
     *
     * @param playerCache
     * @param gatheringData
     */
    public GatherPlayer(PlayerCache playerCache, GatheringData gatheringData, boolean hasSpecializations) {
        this.playerCache = playerCache;
        this.gatheringData = gatheringData;
        this.hasSpecializations = hasSpecializations;
    }

    /**
     * This...
     *
     * @param gatheringData
     * @return
     */
    public static boolean hasSpecializations(GatheringData gatheringData) {
        return gatheringData.getGatheringSpecializations().getFirstSpecialization() != null
                || gatheringData.getGatheringSpecializations().getSecondSpecialization() != null;
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

        // todo: specialization data
        GatheringData gatheringData = new GatheringData
                (
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
                        gatheringSkillsLevel[5],
                        new GatheringData.GatheringSpecializations(null, null)
                );
        RunicProfessions.getGatherPlayerManager().getGatherPlayers().put(uuid,
                new GatherPlayer(playerCache, gatheringData, hasSpecializations(gatheringData))); // todo: update
    }

    /**
     * ALWAYS ONLY CALL ON CACHE SAVE EVENT
     *
     * @param event the saving event that we'll listen for
     */
    public void save(CacheSaveEvent event) {
        PlayerMongoData data = event.getMongoData();
        data.set(DATA_SECTION + GatheringSkill.COOKING.getIdentifier() + EXP_SECTION, this.getGatheringData().getCookingExp());
        data.set(DATA_SECTION + GatheringSkill.COOKING.getIdentifier() + LEVEL_SECTION, this.getGatheringData().getCookingLevel());
        data.set(DATA_SECTION + GatheringSkill.FARMING.getIdentifier() + EXP_SECTION, this.getGatheringData().getFarmingExp());
        data.set(DATA_SECTION + GatheringSkill.FARMING.getIdentifier() + LEVEL_SECTION, this.getGatheringData().getFarmingLevel());
        data.set(DATA_SECTION + GatheringSkill.FISHING.getIdentifier() + EXP_SECTION, this.getGatheringData().getFishingExp());
        data.set(DATA_SECTION + GatheringSkill.FISHING.getIdentifier() + LEVEL_SECTION, this.getGatheringData().getFishingLevel());
        data.set(DATA_SECTION + GatheringSkill.HARVESTING.getIdentifier() + EXP_SECTION, this.getGatheringData().getHarvestingExp());
        data.set(DATA_SECTION + GatheringSkill.HARVESTING.getIdentifier() + LEVEL_SECTION, this.getGatheringData().getHarvestingLevel());
        data.set(DATA_SECTION + GatheringSkill.MINING.getIdentifier() + EXP_SECTION, this.getGatheringData().getMiningExp());
        data.set(DATA_SECTION + GatheringSkill.MINING.getIdentifier() + LEVEL_SECTION, this.getGatheringData().getMiningLevel());
        data.set(DATA_SECTION + GatheringSkill.WOODCUTTING.getIdentifier() + EXP_SECTION, this.getGatheringData().getWoodcuttingExp());
        data.set(DATA_SECTION + GatheringSkill.WOODCUTTING.getIdentifier() + LEVEL_SECTION, this.getGatheringData().getWoodcuttingLevel());
        // todo: chosen professions
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

    public GatheringData getGatheringData() {
        return this.gatheringData;
    }

    /**
     * A handy general method for grabbing the gathering exp which corresponds to the skill
     *
     * @param gatheringSkill which gathering skill do we want info for
     * @return our gathering exp
     */
    public int getGatheringExp(GatheringSkill gatheringSkill) {
        switch (gatheringSkill) {
            case COOKING:
                return this.getGatheringData().getCookingExp();
            case FARMING:
                return this.getGatheringData().getFarmingExp();
            case FISHING:
                return this.getGatheringData().getFishingExp();
            case HARVESTING:
                return this.getGatheringData().getHarvestingExp();
            case MINING:
                return this.getGatheringData().getMiningExp();
            case WOODCUTTING:
                return this.getGatheringData().getWoodcuttingExp();
            default:
                return 0;
        }
    }

    /**
     * A handy general method for grabbing the gathering level which corresponds to the skill
     *
     * @param gatheringSkill which gathering skill do we want info for
     * @return our gathering level
     */
    public int getGatheringLevel(GatheringSkill gatheringSkill) {
        switch (gatheringSkill) {
            case COOKING:
                return this.getGatheringData().getCookingLevel();
            case FARMING:
                return this.getGatheringData().getFarmingLevel();
            case FISHING:
                return this.getGatheringData().getFishingLevel();
            case HARVESTING:
                return this.getGatheringData().getHarvestingLevel();
            case MINING:
                return this.getGatheringData().getMiningLevel();
            case WOODCUTTING:
                return this.getGatheringData().getWoodcuttingLevel();
            default:
                return 0;
        }
    }

    /**
     * General method to set gathering exp total for specified skill
     *
     * @param gatheringSkill to update
     * @param gatheringExp   total to set to
     */
    public void setGatheringExp(GatheringSkill gatheringSkill, int gatheringExp) {
        switch (gatheringSkill) {
            case COOKING:
                this.getGatheringData().setCookingExp(gatheringExp);
                break;
            case FARMING:
                this.getGatheringData().setFarmingExp(gatheringExp);
                break;
            case FISHING:
                this.getGatheringData().setFishingExp(gatheringExp);
                break;
            case HARVESTING:
                this.getGatheringData().setHarvestingExp(gatheringExp);
                break;
            case MINING:
                this.getGatheringData().setMiningExp(gatheringExp);
                break;
            case WOODCUTTING:
                this.getGatheringData().setWoodcuttingExp(gatheringExp);
                break;
        }
    }

    /**
     * General method to set gathering level total for specified skill
     *
     * @param gatheringSkill to update
     * @param gatheringLevel total to set to
     */
    public void setGatheringLevel(GatheringSkill gatheringSkill, int gatheringLevel) {
        switch (gatheringSkill) {
            case COOKING:
                this.getGatheringData().setCookingLevel(gatheringLevel);
                break;
            case FARMING:
                this.getGatheringData().setFarmingLevel(gatheringLevel);
                break;
            case FISHING:
                this.getGatheringData().setFishingLevel(gatheringLevel);
                break;
            case HARVESTING:
                this.getGatheringData().setHarvestingLevel(gatheringLevel);
                break;
            case MINING:
                this.getGatheringData().setMiningLevel(gatheringLevel);
                break;
            case WOODCUTTING:
                this.getGatheringData().setWoodcuttingLevel(gatheringLevel);
                break;
        }
    }

    /**
     *
     */
    public static class GatheringData {
        private final GatheringSpecializations gatheringSpecializations;
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

        /**
         * @param cookingExp
         * @param cookingLevel
         * @param farmingExp
         * @param farmingLevel
         * @param fishingExp
         * @param fishingLevel
         * @param harvestingExp
         * @param harvestingLevel
         * @param miningExp
         * @param miningLevel
         * @param woodcuttingExp
         * @param woodcuttingLevel
         * @param gatheringSpecializations
         */
        public GatheringData(int cookingExp, int cookingLevel, int farmingExp,
                             int farmingLevel, int fishingExp, int fishingLevel, int harvestingExp, int harvestingLevel,
                             int miningExp, int miningLevel, int woodcuttingExp, int woodcuttingLevel,
                             GatheringSpecializations gatheringSpecializations) {
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
            this.gatheringSpecializations = gatheringSpecializations;
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

        public GatheringSpecializations getGatheringSpecializations() {
            return gatheringSpecializations;
        }

        /**
         *
         */
        public static class GatheringSpecializations {
            private GatheringSkill firstSpecialization;
            private GatheringSkill secondSpecialization;

            /**
             * @param firstSpecialization
             * @param secondSpecialization
             */
            public GatheringSpecializations(@Nullable GatheringSkill firstSpecialization,
                                            @Nullable GatheringSkill secondSpecialization) {
                this.firstSpecialization = firstSpecialization;
                this.secondSpecialization = secondSpecialization;
            }

            public GatheringSkill getFirstSpecialization() {
                return firstSpecialization;
            }

            public void setFirstSpecialization(GatheringSkill gatheringSkill) {
                this.firstSpecialization = gatheringSkill;
            }

            public GatheringSkill getSecondSpecialization() {
                return secondSpecialization;
            }

            public void setSecondSpecialization(GatheringSkill gatheringSkill) {
                this.secondSpecialization = gatheringSkill;
            }
        }
    }

}
