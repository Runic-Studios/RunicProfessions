package com.runicrealms.plugin.professions.crafting.hunter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HunterCache {
    private final Map<UUID, HunterPlayer> players;

    public HunterCache() {
        this.players = new HashMap<>();
    }

    public Map<UUID, HunterPlayer> getPlayers() {
        return this.players;
    }
}
