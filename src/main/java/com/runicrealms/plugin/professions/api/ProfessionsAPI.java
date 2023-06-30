package com.runicrealms.plugin.professions.api;

import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.gathering.GatheringRegion;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface ProfessionsAPI {

    /**
     * This is the only accepted method for updating a player's profession.
     *
     * @param player     player to update
     * @param profession the new profession
     */
    void changePlayerProfession(Player player, Profession profession);

    /**
     * Retrieves the gathering level for the player for the associated gathering skill
     *
     * @param uuid           of player to check
     * @param gatheringSkill (mining, fishing, etc.) to check
     * @return the level
     */
    int determineCurrentGatheringLevel(UUID uuid, GatheringSkill gatheringSkill);

    /**
     * @return a map of blocks which have been mined and will be replenished
     */
    ConcurrentHashMap<Location, Material> getBlocksToRestore();

    /**
     * Returns a list of players who are currently crafting.
     * Used to prevent other mechanics during the crafting process.
     *
     * @return a list of players currently crafting items
     */
    ArrayList<Player> getCurrentCrafters();

    /**
     * Returns the current profession of the player
     *
     * @param uuid of player to check
     * @param slot of the character
     * @return their profession enum
     */
    Profession getPlayerProfession(UUID uuid, int slot);

    /**
     * Returns the current exp of the player in their profession
     *
     * @param uuid of player to check
     * @param slot of the character
     * @return their profession exp
     */
    int getPlayerProfessionExp(UUID uuid, int slot);

    /**
     * Returns the current level of the player in their profession
     *
     * @param uuid of player to check
     * @param slot of the character
     * @return their profession level
     */
    int getPlayerProfessionLevel(UUID uuid, int slot);

    /**
     * Used when the player is currently inside a crafting workstation
     *
     * @param player to check
     * @return the workstation object they are inside
     */
    Workstation getPlayerWorkstation(Player player);

    /**
     * Checks if the given location in within a gathering region. Location can be a player location or the location
     * of a mined block
     *
     * @param gatheringRegion an enum of applicable regions for checking for the right one (mine, etc.)
     * @param location        the location of a given player or block
     * @return true if the location is in a gathering region
     */
    boolean isInGatheringRegion(GatheringRegion gatheringRegion, Location location);

    /**
     * Opens the menu which displays all gathering info for the given player
     *
     * @param player to display menu to
     */
    void openGatheringGUI(Player player);

    /**
     * Display skill-specific menu for given gathering skill
     *
     * @param player         to display menu to
     * @param gatheringSkill to select
     */
    void openGatheringSkillGUI(Player player, GatheringSkill gatheringSkill);

    /**
     * Sets the current workstation the player is inside
     *
     * @param player  who is in a menu
     * @param station the workstation that they are in
     */
    void setPlayerWorkstation(Player player, Workstation station);

    /**
     * @return a map of in-memory workstation locations and their type
     */
    Map<Location, String> getStoredStationLocations();

    /**
     * Removes the workstation from memory and flat file storage
     *
     * @param location of the workstation to remove
     */
    void removeWorkstation(Location location);

}
