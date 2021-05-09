package com.runicrealms.plugin.professions.api;

import com.runicrealms.plugin.ProfessionEnum;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.database.PlayerMongoData;
import com.runicrealms.plugin.database.PlayerMongoDataSection;
import com.runicrealms.plugin.player.cache.PlayerCache;
import com.runicrealms.plugin.professions.crafting.hunter.HunterPlayer;
import com.runicrealms.plugin.professions.event.ProfessionChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RunicProfessionsAPI {

    /**
     * This is the only accepted method for updating a player's profession.
     * @param player player to update
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
}
