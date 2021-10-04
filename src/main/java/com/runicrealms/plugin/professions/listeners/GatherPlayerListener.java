package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.character.api.CharacterLoadEvent;
import com.runicrealms.plugin.database.event.CacheSaveEvent;
import com.runicrealms.plugin.database.event.CacheSaveReason;
import com.runicrealms.plugin.professions.GatherPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;

public class GatherPlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST) // run event last
    public void onCharacterLoad(CharacterLoadEvent event) {
        GatherPlayer.registerGatherPlayer(event.getPlayer());
    }

    @EventHandler
    public void onCacheSave(CacheSaveEvent event) {
        Map<UUID, GatherPlayer> gatherPlayerMap = RunicProfessions.getGatherPlayerManager().getGatherPlayers();
        UUID uuid = event.getPlayer().getUniqueId();
        if (!gatherPlayerMap.containsKey(uuid)) return;
        gatherPlayerMap.get(uuid).save(event);
        if (event.cacheSaveReason() == CacheSaveReason.LOGOUT) {
            gatherPlayerMap.remove(uuid);
        }
    }
}
