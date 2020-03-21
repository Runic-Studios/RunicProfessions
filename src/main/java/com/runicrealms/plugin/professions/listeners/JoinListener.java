package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicProfessions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * Used for storing hunter data
 */
public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

//        Player pl = e.getPlayer();
//        UUID uuid = pl.getUniqueId();
//
//        if (!RunicProfessions.getInstance().getConfig().isSet(uuid + ".info.prof")) {
//            RunicProfessions.getInstance().getConfig().set(uuid + ".info.prof", "hunter");
//            RunicProfessions.getInstance().getConfig().set(uuid + ".info.prof.hunter_level", 0);
//            RunicProfessions.getInstance().getConfig().set(uuid + ".info.prof.hunter_points", 0);
//            RunicProfessions.getInstance().getConfig().set(uuid + ".info.prof.hunter_mob", "");
//            RunicProfessions.getInstance().saveConfig();
//            RunicProfessions.getInstance().reloadConfig();
//        }
    }
}
