package com.runicrealms.plugin.professions.model;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.model.SessionDataRedis;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CraftingData implements SessionDataRedis {
    public static final List<String> FIELDS = new ArrayList<String>() {{
        add(ProfessionField.PROF_NAME.getField());
        add(ProfessionField.PROF_EXP.getField());
        add(ProfessionField.PROF_LEVEL.getField());
    }};
    private String profName;
    private int profLevel;
    private int profExp;

    @SuppressWarnings("unused")
    public CraftingData() {
        // Default constructor for Spring
    }

    public CraftingData(String profName, int profLevel, int profExp) {
        this.profName = profName;
        this.profLevel = profLevel;
        this.profExp = profExp;
    }

    /**
     * A container of basic info used to load a player character profile, built from redis
     *
     * @param uuid  of the player
     * @param slot  of the character
     * @param jedis the jedis resource
     */
    public CraftingData(UUID uuid, int slot, Jedis jedis) {
        Map<String, String> fieldsMap = getDataMapFromJedis(uuid, jedis, slot);
        this.profName = fieldsMap.get(ProfessionField.PROF_NAME.getField());
        this.profLevel = Integer.parseInt(fieldsMap.get(ProfessionField.PROF_LEVEL.getField()));
        this.profExp = Integer.parseInt(fieldsMap.get(ProfessionField.PROF_EXP.getField()));
    }

    public static String getJedisKey(UUID uuid, int slot) {
        return uuid + ":character:" + slot + ":profession:data";
    }

    @Override
    public Map<String, String> getDataMapFromJedis(UUID uuid, Jedis jedis, int... slot) {
        String database = RunicCore.getDataAPI().getMongoDatabase().getName();
        Map<String, String> fieldsMap = new HashMap<>();
        List<String> fields = new ArrayList<>(getFields());
        String[] fieldsToArray = fields.toArray(new String[0]);
        List<String> values = jedis.hmget(database + ":" + getJedisKey(uuid, slot[0]), fieldsToArray);
        for (int i = 0; i < fieldsToArray.length; i++) {
            fieldsMap.put(fieldsToArray[i], values.get(i));
        }
        return fieldsMap;
    }

    @Override
    public List<String> getFields() {
        return FIELDS;
    }

    /**
     * Returns a map that can be used to set values in redis
     *
     * @return a map of string keys and character info values
     */
    @Override
    public Map<String, String> toMap(UUID uuid, int... slot) {
        return new HashMap<String, String>() {{
            put(ProfessionField.PROF_NAME.getField(), profName);
            put(ProfessionField.PROF_LEVEL.getField(), String.valueOf(profLevel));
            put(ProfessionField.PROF_EXP.getField(), String.valueOf(profExp));
        }};
    }

    @Override
    public void writeToJedis(UUID uuid, Jedis jedis, int... slot) {
        String database = RunicCore.getDataAPI().getMongoDatabase().getName();
        // Inform the server that this player should be saved to mongo on next task (jedis data is refreshed)
        jedis.sadd(database + ":" + "markedForSave:professions", uuid.toString());
        String key = getJedisKey(uuid, slot[0]);
        jedis.hmset(database + ":" + key, this.toMap(uuid));
        jedis.expire(database + ":" + key, RunicCore.getRedisAPI().getExpireTime());
    }

    public int getProfExp() {
        return profExp;
    }

    public void setProfExp(int profExp) {
        this.profExp = profExp;
    }

    public int getProfLevel() {
        return profLevel;
    }

    public void setProfLevel(int profLevel) {
        this.profLevel = profLevel;
    }

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

}
