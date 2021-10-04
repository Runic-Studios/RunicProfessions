package com.runicrealms.plugin.professions;

public enum GatheringSkill {

    COOKING("cooking"),
    FARMING("farming"),
    FISHING("fishing"),
    HARVESTING("harvesting"),
    MINING("mining"),
    WOODCUTTING("woodcutting");

    private final String identifier;

    GatheringSkill(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
