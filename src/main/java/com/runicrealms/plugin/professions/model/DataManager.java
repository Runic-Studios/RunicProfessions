package com.runicrealms.plugin.professions.model;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainAbortAction;
import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.character.api.CharacterDeleteEvent;
import com.runicrealms.plugin.character.api.CharacterQuitEvent;
import com.runicrealms.plugin.character.api.CharacterSelectEvent;
import com.runicrealms.plugin.database.event.MongoSaveEvent;
import com.runicrealms.plugin.model.CharacterField;
import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.api.DataAPI;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Some of our data classes (HunterData, GatheringData) would read/write from Redis too often,
 * so we memoize them. This class handles the memoization of any over-active model data, determining
 * when it should read/write from/to redis.
 *
 * @author Skyfallin
 */
public class DataManager implements DataAPI, Listener {

    public static final TaskChainAbortAction<Player, String, ?> CONSOLE_LOG = new TaskChainAbortAction<Player, String, Object>() {
        public void onAbort(TaskChain<?> chain, Player player, String message) {
            Bukkit.getLogger().log(Level.SEVERE, ChatColor.translateAlternateColorCodes('&', message));
        }
    };
    private final Map<UUID, ProfessionData> professionDataMap;

    public DataManager() {
        this.professionDataMap = new HashMap<>();
        RunicProfessions.getInstance().getServer().getPluginManager().registerEvents(this, RunicProfessions.getInstance());
    }

    /**
     * Checks redis to see if the currently selected character's gathering data is cached.
     * And if it is, returns the GatheringData object
     *
     * @param uuid  of player to check
     * @param jedis the jedis resource
     * @return a GatheringData object if it is found in redis
     */
    public GatheringData checkRedisForGatheringData(UUID uuid, Jedis jedis) {
        String database = RunicCore.getDataAPI().getMongoDatabase().getName();
        if (jedis.exists(database + ":" + uuid + ":gathering:data")) {
            jedis.expire(database + ":" + uuid, RunicCore.getRedisAPI().getExpireTime());
            return new GatheringData(uuid, jedis);
        }
        return null;
    }

    /**
     * Checks redis to see if the currently selected character's profession data is cached.
     * And if it is, returns the CraftingData object
     *
     * @param uuid  of player to check
     * @param jedis the jedis resource
     * @return a CraftingData object if it is found in redis
     */
    public CraftingData checkRedisForProfessionData(UUID uuid, int slot, Jedis jedis) {
        String database = RunicCore.getDataAPI().getMongoDatabase().getName();
        String key = CraftingData.getJedisKey(uuid, slot);
        if (jedis.exists(database + ":" + key)) {
            jedis.expire(database + ":" + key, RunicCore.getRedisAPI().getExpireTime());
            return new CraftingData(uuid, slot, jedis);
        }
        return null;
    }

    @Override
    public ProfessionData getProfessionData(UUID uuid) {
        return professionDataMap.get(uuid);
    }

    @Override
    public CraftingData loadCraftingData(UUID uuid, int slot) {
        // Step 1: Reference in-memory profession data and check for crafting data
//        Bukkit.getLogger().info("crafting data profession lookup");
        ProfessionData professionData = getProfessionData(uuid);
        CraftingData craftingData = professionData.getCraftingDataMap().get(slot);
        if (craftingData != null) return craftingData;
        // Step 2: Create new crafting data for slot
        craftingData = new CraftingData
                (
                        Profession.NONE.getName(),
                        0,
                        0
                );
        professionData.getCraftingDataMap().put(slot, craftingData);
        try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
            craftingData.writeToJedis(uuid, jedis, slot);
        }
        return professionData.getCraftingDataMap().get(slot);
    }

    @Override
    public GatheringData loadGatheringData(UUID uuid) {
        // Step 1: Reference in-memory profession data and check for gathering data
//        Bukkit.getLogger().info("gathering data profession lookup");
        ProfessionData professionData = getProfessionData(uuid);
        GatheringData gatheringData = professionData.getGatheringData();
        if (gatheringData != null) return gatheringData;
//        Bukkit.getLogger().info("no in-memory gathering data found. building now");
        // Step 2: Create new crafting data (this should never run?)
        gatheringData = new GatheringData();
        professionData.setGatheringData(gatheringData);
        try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
            gatheringData.writeToJedis(uuid, jedis);
        }
        return gatheringData;
    }

    @Override
    public ProfessionData loadProfessionData(UUID uuid) {
        // Step 1: Check redis
        try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
            GatheringData gatheringData = checkRedisForGatheringData(uuid, jedis);
            // Gathering data is acc-wide, so if this exists, we'll build a new data object here
            // Then, we build all the crafting data we need
            if (gatheringData != null) {
                HashMap<Integer, CraftingData> craftingDataMap = new HashMap<>();
                for (int i = 1; i <= RunicCore.getDataAPI().getMaxCharacterSlot(); i++) {
                    CraftingData craftingData = checkRedisForProfessionData(uuid, i, jedis);
                    if (craftingData != null) {
                        craftingDataMap.put(i, craftingData);
                    }
                }
                return new ProfessionData(null, uuid, gatheringData, craftingDataMap);
            }
            // Step 2: Check the mongo database
            Query query = new Query();
            query.addCriteria(Criteria.where(CharacterField.PLAYER_UUID.getField()).is(uuid));
            MongoTemplate mongoTemplate = RunicCore.getDataAPI().getMongoTemplate();
            List<ProfessionData> results = mongoTemplate.find(query, ProfessionData.class);
            if (results.size() > 0) {
                ProfessionData result = results.get(0);
                result.writeToJedis(jedis);
                return result;
            }
            // Step 3: If no data is found, we create some data and save it to the collection
            ProfessionData newData = new ProfessionData
                    (
                            new ObjectId(),
                            uuid,
                            new GatheringData(),
                            new HashMap<>()
                    );
            newData.addDocumentToMongo();
            newData.writeToJedis(jedis);
            return newData;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCharacterDelete(CharacterDeleteEvent event) {
        event.getPluginsToDeleteData().add("professions");
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int slot = event.getSlot();
        // Removes player from the save task
        try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
            String database = RunicCore.getDataAPI().getMongoDatabase().getName();
            jedis.srem(database + ":markedForSave:professions", String.valueOf(player.getUniqueId()));
        }
        // Delete from Mongo
        Query query = new Query();
        query.addCriteria(Criteria.where(CharacterField.PLAYER_UUID.getField()).is(uuid));
        Update update = new Update();
        update.unset("craftingDataMap." + slot);
        MongoTemplate mongoTemplate = RunicCore.getDataAPI().getMongoTemplate();
        mongoTemplate.updateFirst(query, update, ProfessionData.class);
        // Mark this deletion as complete
        event.getPluginsToDeleteData().remove("professions");
    }

    @EventHandler
    public void onCharacterQuit(CharacterQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
            if (professionDataMap.get(uuid) != null) {
                // Write gathering data to redis on logout
                GatheringData gatheringData = professionDataMap.get(uuid).getGatheringData();
                gatheringData.writeToJedis(uuid, jedis);
                // Write crafting data to redis on logout
                Map<Integer, CraftingData> craftingDataMap = professionDataMap.get(uuid).getCraftingDataMap();
                for (Integer slot : craftingDataMap.keySet()) {
                    craftingDataMap.get(slot).writeToJedis(uuid, jedis, slot);
                }
                // Remove memoized data map from memory
                professionDataMap.remove(uuid);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW) // early
    public void onCharacterSelect(CharacterSelectEvent event) {
        // For benchmarking
        long startTime = System.nanoTime();
        event.getPluginsToLoadData().add("professions");
        Player player = event.getPlayer();
        UUID uuid = event.getPlayer().getUniqueId();
        TaskChain<?> chain = RunicProfessions.newChain();
        chain
                .asyncFirst(() -> loadProfessionData(uuid))
                .abortIfNull(CONSOLE_LOG, player, "RunicProfessions failed to load on select!")
                .syncLast(professionData -> {
                    this.professionDataMap.put(uuid, professionData); // Add to in-game memory
                    event.getPluginsToLoadData().remove("professions");
                    // Calculate elapsed time
                    long endTime = System.nanoTime();
                    long elapsedTime = endTime - startTime;
                    // Log elapsed time in milliseconds
                    Bukkit.getLogger().info("RunicProfessions took: " + elapsedTime / 1_000_000 + "ms to load");
                })
                .execute();
    }

    /**
     * Saves player profession info when the server is shut down
     * Works even if the player is now offline
     */
    @EventHandler
    public void onDatabaseSave(MongoSaveEvent event) {
        // Cancel the task timer
        RunicProfessions.getMongoTask().getTask().cancel();
        // Manually save all data (flush players marked for save)
        RunicProfessions.getMongoTask().saveAllToMongo(() -> event.markPluginSaved("professions"));
    }

}
