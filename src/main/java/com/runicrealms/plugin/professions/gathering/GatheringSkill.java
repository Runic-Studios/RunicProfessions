package com.runicrealms.plugin.professions.gathering;

import org.bukkit.ChatColor;

public enum GatheringSkill {

    COOKING("cooking", ""),
    FARMING("farming", "You need a farming hoe to do that!"),
    FISHING("fishing", "You need a fishing rod to do that!"),
    HARVESTING("harvesting", ""),
    MINING("mining", "You need a mining pickaxe to do that!"),
    WOODCUTTING("woodcutting", "You need a woodcutting axe to do that!");

    private final String identifier;
    private final String noToolMessage;

    GatheringSkill(String identifier, String noToolMessage) {
        this.identifier = identifier;
        this.noToolMessage = noToolMessage;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFormattedIdentifier() {
        return identifier.substring(0, 1).toUpperCase() + identifier.substring(1);
    }

    public String getNoToolMessage() {
        return ChatColor.RED + noToolMessage;
    }
}
