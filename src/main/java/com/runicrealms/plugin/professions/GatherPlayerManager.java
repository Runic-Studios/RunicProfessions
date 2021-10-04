package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.listeners.GatherPlayerListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GatherPlayerManager {

    private final Map<UUID, GatherPlayer> gatherPlayers;

    public GatherPlayerManager() {
        this.gatherPlayers = new HashMap<>();
        RunicProfessions.getInstance().getServer().getPluginManager().registerEvents(new GatherPlayerListener(), RunicProfessions.getInstance());
    }

    public Map<UUID, GatherPlayer> getGatherPlayers() {
        return this.gatherPlayers;
    }
}
