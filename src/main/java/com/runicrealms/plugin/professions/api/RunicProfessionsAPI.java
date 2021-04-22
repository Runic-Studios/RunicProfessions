package com.runicrealms.plugin.professions.api;

import com.runicrealms.plugin.ProfessionEnum;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
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

        ProfessionChangeEvent e = new ProfessionChangeEvent(player, profession);
        Bukkit.getServer().getPluginManager().callEvent(e);
        if (e.isCancelled()) return;

        // reset profession, level, exp
        RunicCoreAPI.getPlayerCache(player).setProfName(profession.getName());
        RunicCoreAPI.getPlayerCache(player).setProfLevel(0);
        RunicCoreAPI.getPlayerCache(player).setProfExp(0);

        // reset hunter info
        RunicProfessions.getInstance().getConfig().set(player.getUniqueId() + ".info.prof.hunter_mob", null);
        RunicProfessions.getInstance().getConfig().set(player.getUniqueId() + ".info.prof.hunter_points", null);
        RunicProfessions.getInstance().saveConfig();
        RunicProfessions.getInstance().reloadConfig();
    }
}
