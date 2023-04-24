package com.runicrealms.plugin.professions.api;

import com.runicrealms.plugin.professions.model.CraftingData;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.professions.model.ProfessionData;

import java.util.UUID;

public interface DataAPI {

    /**
     * Loads the top-level entity representing data for this plugin.
     * Contains data on crafting (per-character) and gathering (acc-wide)
     *
     * @param uuid of the player
     * @return a ProfessionData object with their persistent data
     */
    ProfessionData getProfessionData(UUID uuid);

    /**
     * Tries to retrieve a CraftingData object from server memory, otherwise falls back to redis / mongo
     *
     * @param uuid of the player
     * @param slot of the character
     * @return a CraftingData object
     */
    CraftingData loadCraftingData(UUID uuid, int slot);

    /**
     * Tries to retrieve a GatheringData object from server memory, otherwise falls back to redis / mongo
     *
     * @param uuid of the player
     * @return a GatheringData object
     */
    GatheringData loadGatheringData(UUID uuid);

    /**
     * Loads the profession data from redis and/or mongo (creates data if it does not exist).
     * Should ONLY be used on login / logout
     *
     * @param uuid of the player
     * @return a future, which will eventually have the data
     */
    ProfessionData loadProfessionData(UUID uuid);
}
