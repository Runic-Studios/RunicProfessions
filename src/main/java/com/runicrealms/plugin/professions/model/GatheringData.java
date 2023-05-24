package com.runicrealms.plugin.professions.model;

import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.rdb.RunicDatabase;
import com.runicrealms.plugin.rdb.model.SessionDataRedis;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Wrapper for player gathering levels
 */
public class GatheringData implements SessionDataRedis {
    public static final List<String> FIELDS = new ArrayList<String>() {{
        add(GatheringField.COOKING_EXP.getField());
        add(GatheringField.FARMING_EXP.getField());
        add(GatheringField.FISHING_EXP.getField());
        add(GatheringField.HARVESTING_EXP.getField());
        add(GatheringField.MINING_EXP.getField());
        add(GatheringField.WOODCUTTING_EXP.getField());
    }};
    private static final String DATA_SECTION_JEDIS = "gathering:data";
    private int cookingExp = 0;
    private int farmingExp = 0;
    private int fishingExp = 0;
    private int harvestingExp = 0;
    private int miningExp = 0;
    private int woodcuttingExp = 0;

    @SuppressWarnings("unused")
    public GatheringData() {
        // Default constructor for Spring
    }

    /**
     * Build the GatheringData object using Mongo Spring
     */
    public GatheringData(int cookingExp, int farmingExp, int fishingExp, int harvestingExp,
                         int miningExp, int woodcuttingExp) {
        this.cookingExp = cookingExp;
        this.farmingExp = farmingExp;
        this.fishingExp = fishingExp;
        this.harvestingExp = harvestingExp;
        this.miningExp = miningExp;
        this.woodcuttingExp = woodcuttingExp;
    }

    /**
     * Build's the player's gathering data from redis
     *
     * @param uuid  of the player
     * @param jedis the jedis resource from core
     */
    public GatheringData(UUID uuid, Jedis jedis) {
        Map<String, String> dataMap = getDataMapFromJedis(uuid, jedis);
        this.cookingExp = Integer.parseInt(dataMap.get(GatheringField.COOKING_EXP.getField()));
        this.farmingExp = Integer.parseInt(dataMap.get(GatheringField.FARMING_EXP.getField()));
        this.fishingExp = Integer.parseInt(dataMap.get(GatheringField.FISHING_EXP.getField()));
        this.harvestingExp = Integer.parseInt(dataMap.get(GatheringField.HARVESTING_EXP.getField()));
        this.miningExp = Integer.parseInt(dataMap.get(GatheringField.MINING_EXP.getField()));
        this.woodcuttingExp = Integer.parseInt(dataMap.get(GatheringField.WOODCUTTING_EXP.getField()));
    }

    public int getCookingExp() {
        return cookingExp;
    }

    public void setCookingExp(int cookingExp) {
        this.cookingExp = cookingExp;
    }

    public int getCookingLevel() {
        return ProfExpUtil.calculateProfessionLevel(this.cookingExp);
    }

    @Override
    public Map<String, String> getDataMapFromJedis(UUID uuid, Jedis jedis, int... ints) {
        String database = RunicDatabase.getAPI().getDataAPI().getMongoDatabase().getName();
        Map<String, String> fieldsMap = new HashMap<>();
        String[] fieldsToArray = FIELDS.toArray(new String[0]);
        List<String> values = jedis.hmget(database + ":" + uuid.toString() + ":" + DATA_SECTION_JEDIS, fieldsToArray);
        for (int i = 0; i < fieldsToArray.length; i++) {
            fieldsMap.put(fieldsToArray[i], values.get(i));
        }
        return fieldsMap;
    }

    @Override
    public List<String> getFields() {
        return FIELDS;
    }

    @Override
    public Map<String, String> toMap(UUID uuid, int... slot) {
        return new HashMap<String, String>() {{
            put(GatheringField.COOKING_EXP.getField(), String.valueOf(cookingExp));
            put(GatheringField.FARMING_EXP.getField(), String.valueOf(farmingExp));
            put(GatheringField.FISHING_EXP.getField(), String.valueOf(fishingExp));
            put(GatheringField.HARVESTING_EXP.getField(), String.valueOf(harvestingExp));
            put(GatheringField.MINING_EXP.getField(), String.valueOf(miningExp));
            put(GatheringField.WOODCUTTING_EXP.getField(), String.valueOf(woodcuttingExp));
        }};
    }

    /**
     * Adds the object into session storage in redis
     *
     * @param jedis the jedis resource from core
     */
    @Override
    public void writeToJedis(UUID uuid, Jedis jedis, int... ignored) {
        String database = RunicDatabase.getAPI().getDataAPI().getMongoDatabase().getName();
        // Inform the server that this player should be saved to mongo on next task (jedis data is refreshed)
        jedis.sadd(database + ":" + "markedForSave:professions", uuid.toString());
        jedis.hmset(database + ":" + uuid + ":" + DATA_SECTION_JEDIS, this.toMap(uuid));
        jedis.expire(database + ":" + uuid + ":" + DATA_SECTION_JEDIS, RunicDatabase.getAPI().getRedisAPI().getExpireTime());
    }

    public int getFarmingExp() {
        return farmingExp;
    }

    public void setFarmingExp(int farmingExp) {
        this.farmingExp = farmingExp;
    }

    public int getFarmingLevel() {
        return ProfExpUtil.calculateProfessionLevel(this.farmingExp);
    }

    public int getFishingExp() {
        return fishingExp;
    }

    public void setFishingExp(int fishingExp) {
        this.fishingExp = fishingExp;
    }

    public int getFishingLevel() {
        return ProfExpUtil.calculateProfessionLevel(this.fishingExp);
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
                return this.getCookingExp();
            case FARMING:
                return this.getFarmingExp();
            case FISHING:
                return this.getFishingExp();
            case HARVESTING:
                return this.getHarvestingExp();
            case MINING:
                return this.getMiningExp();
            case WOODCUTTING:
                return this.getWoodcuttingExp();
            default:
                return 0;
        }
    }

    public int getGatheringLevel(GatheringSkill gatheringSkill) {
        switch (gatheringSkill) {
            case COOKING:
                return this.getCookingLevel();
            case FARMING:
                return this.getFarmingLevel();
            case FISHING:
                return this.getFishingLevel();
            case HARVESTING:
                return this.getHarvestingLevel();
            case MINING:
                return this.getMiningLevel();
            case WOODCUTTING:
                return this.getWoodcuttingLevel();
            default:
                return 0;
        }
    }

    public int getHarvestingExp() {
        return harvestingExp;
    }

    public void setHarvestingExp(int harvestingExp) {
        this.harvestingExp = harvestingExp;
    }

    public int getHarvestingLevel() {
        return ProfExpUtil.calculateProfessionLevel(this.harvestingExp);
    }

    public int getMiningExp() {
        return miningExp;
    }

    public void setMiningExp(int miningExp) {
        this.miningExp = miningExp;
    }

    public int getMiningLevel() {
        return ProfExpUtil.calculateProfessionLevel(this.miningExp);
    }

    public int getWoodcuttingExp() {
        return woodcuttingExp;
    }

    public void setWoodcuttingExp(int woodcuttingExp) {
        this.woodcuttingExp = woodcuttingExp;
    }

    public int getWoodcuttingLevel() {
        return ProfExpUtil.calculateProfessionLevel(this.woodcuttingExp);
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
                this.setCookingExp(gatheringExp);
                break;
            case FARMING:
                this.setFarmingExp(gatheringExp);
                break;
            case FISHING:
                this.setFishingExp(gatheringExp);
                break;
            case HARVESTING:
                this.setHarvestingExp(gatheringExp);
                break;
            case MINING:
                this.setMiningExp(gatheringExp);
                break;
            case WOODCUTTING:
                this.setWoodcuttingExp(gatheringExp);
                break;
        }
    }

}
