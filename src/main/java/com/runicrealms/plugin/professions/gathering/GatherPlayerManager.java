package com.runicrealms.plugin.professions.gathering;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GatherPlayerManager {

    private final Map<UUID, GatherPlayer> gatherPlayers;

    public GatherPlayerManager() {
        this.gatherPlayers = new HashMap<>();
    }

    public Map<UUID, GatherPlayer> getGatherPlayers() {
        return this.gatherPlayers;
    }
}
