package com.runicrealms.plugin.professions.api;

import com.runicrealms.plugin.ProfessionEnum;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.database.PlayerMongoData;
import com.runicrealms.plugin.database.PlayerMongoDataSection;
import com.runicrealms.plugin.player.cache.PlayerCache;
import com.runicrealms.plugin.professions.GatheringRegion;
import com.runicrealms.plugin.professions.crafting.hunter.HunterPlayer;
import com.runicrealms.plugin.professions.event.ProfessionChangeEvent;
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

public class RunicProfessionsAPI {

    /**
     * This is the only accepted method for updating a player's profession.
     *
     * @param player     player to update
     * @param profession the new profession
     */
    public static void changePlayerProfession(Player player, ProfessionEnum profession) {
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
}
