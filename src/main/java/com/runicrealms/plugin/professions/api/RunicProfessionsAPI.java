package com.runicrealms.plugin.professions.api;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.database.PlayerMongoData;
import com.runicrealms.plugin.database.PlayerMongoDataSection;
import com.runicrealms.plugin.player.cache.PlayerCache;
import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.crafting.hunter.HunterPlayer;
import com.runicrealms.plugin.professions.event.ProfessionChangeEvent;
import com.runicrealms.plugin.professions.gathering.GatherPlayer;
import com.runicrealms.plugin.professions.gathering.GatheringGUI;
import com.runicrealms.plugin.professions.gathering.GatheringRegion;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class RunicProfessionsAPI {

    /**
     * This is the only accepted method for updating a player's profession.
     *
     * @param player     player to update
     * @param profession the new profession
     */
    public static void changePlayerProfession(Player player, Profession profession) {
        PlayerCache playerCache = RunicCoreAPI.getPlayerCache(player);

        // reset profession, level, exp
        playerCache.setProfName(profession.getName());
        playerCache.setProfLevel(0);
        playerCache.setProfExp(0);

        HunterPlayer hunter = RunicProfessions.getHunterCache().getPlayers().remove(player.getUniqueId());

        //check if the player is currently a hunter and if so remove all hunter data
        if (hunter != null) {
            PlayerMongoData playerData = (PlayerMongoData) playerCache.getMongoData();
            int slot = playerCache.getCharacterSlot();
            PlayerMongoDataSection data = playerData.getCharacter(slot);

            data.remove("prof.hunter_points");
            data.remove("prof.hunter_mob");
            data.remove("prof.hunter_kills");
            data.remove("prof.hunter_kills_max");
            data.save();
        }

        ProfessionChangeEvent event = new ProfessionChangeEvent(player, profession);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Retrieves the gathering level for the player for the associated gathering skill
     *
     * @param player         to check
     * @param gatheringSkill (mining, fishing, etc.) to check
     * @return the level
     */
    public static int determineCurrentGatheringLevel(Player player, GatheringSkill gatheringSkill) {
        GatherPlayer gatherPlayer = RunicProfessionsAPI.getGatherPlayer(player.getUniqueId());
        switch (gatheringSkill) {
            case COOKING:
                return gatherPlayer.getCookingLevel();
            case FARMING:
                return gatherPlayer.getFarmingLevel();
            case FISHING:
                return gatherPlayer.getFishingLevel();
            case HARVESTING:
                return gatherPlayer.getHarvestingLevel();
            case MINING:
                return gatherPlayer.getMiningLevel();
            case WOODCUTTING:
                return gatherPlayer.getWoodcuttingLevel();
            default:
                return 0;
        }
    }

    /**
     * Returns the GatherPlayer wrapper for the given player
     *
     * @param uuid of player to grab wrapper for
     * @return the gather player wrapper
     */
    public static GatherPlayer getGatherPlayer(UUID uuid) {
        return RunicProfessions.getGatherPlayerManager().getGatherPlayers().get(uuid);
    }

    /**
     * Checks if the given location in within a gathering region. Location can be a player location or the location
     * of a mined block
     *
     * @param gatheringRegion an enum of applicable regions for checking for the right one (mine, etc.)
     * @param location        the location of a given player or block
     * @return true if the location is in a gathering region
     */
    public static boolean isInGatheringRegion(GatheringRegion gatheringRegion, Location location) {
        // grab all regions the block is in
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));
        Set<ProtectedRegion> regions = set.getRegions();

        if (regions == null) return false;

        boolean isInRegion = false;

        // check the region for the keyword
        for (ProtectedRegion region : regions) {
            if (region.getId().contains(gatheringRegion.getIdentifier())) {
                isInRegion = true;
            }
        }

        return isInRegion;
    }

    /**
     * Opens the menu which displays all gathering info for the given player
     *
     * @param player to display menu to
     */
    public static void openGatheringGUI(Player player) {
        player.openInventory(new GatheringGUI(player).getInventory());
    }
}
