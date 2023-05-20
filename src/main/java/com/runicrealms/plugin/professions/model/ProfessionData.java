package com.runicrealms.plugin.professions.model;

import com.runicrealms.plugin.rdb.RunicDatabase;
import com.runicrealms.plugin.rdb.model.SessionDataMongo;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.UUID;

/**
 * This is our top-level Data Transfer Object (DTO) that handles read-writing to redis and mongo
 */
@Document(collection = "professions")
@SuppressWarnings("unused")
public class ProfessionData implements SessionDataMongo {
    @Id
    private ObjectId id;
    @Field("playerUuid")
    private UUID uuid;
    private GatheringData gatheringData;
    private HashMap<Integer, CraftingData> craftingDataMap = new HashMap<>();

    @SuppressWarnings("unused")
    public ProfessionData() {
        // Default constructor for Spring
    }

    /**
     * Constructor for new players
     *
     * @param id              of the profession document in mongo
     * @param uuid            of the player
     * @param gatheringData   a blank gathering data object
     * @param craftingDataMap a blank crafting data object
     */
    public ProfessionData(ObjectId id, UUID uuid, GatheringData gatheringData, HashMap<Integer, CraftingData> craftingDataMap) {
        this.id = id;
        this.uuid = uuid;
        this.gatheringData = gatheringData;
        this.craftingDataMap = craftingDataMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ProfessionData addDocumentToMongo() {
        MongoTemplate mongoTemplate = RunicDatabase.getAPI().getDataAPI().getMongoTemplate();
        return mongoTemplate.save(this);
    }

    public HashMap<Integer, CraftingData> getCraftingDataMap() {
        return craftingDataMap;
    }

    public void setCraftingDataMap(HashMap<Integer, CraftingData> craftingDataMap) {
        this.craftingDataMap = craftingDataMap;
    }

    public GatheringData getGatheringData() {
        return gatheringData;
    }

    public void setGatheringData(GatheringData gatheringData) {
        this.gatheringData = gatheringData;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * A jedis write method that writes the underlying data structures
     *
     * @param jedis some new jedis resource
     */
    public void writeToJedis(Jedis jedis) {
        this.gatheringData.writeToJedis(uuid, jedis);
        this.craftingDataMap.forEach((slot, craftingData) -> craftingData.writeToJedis(uuid, jedis, slot));
    }
}
