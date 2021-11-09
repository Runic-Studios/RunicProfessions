package com.runicrealms.plugin.professions.gathering;

public enum GatheringRegion {

    FARM("farm"),
    GROVE("grove"),
    MINE("mine"),
    POND("pond");

    private final String identifier;

    GatheringRegion(String identifier) {
        this.identifier = identifier;
    }

    public static GatheringRegion getFromIdentifier(String identifier) {
        for (GatheringRegion gatheringRegion : GatheringRegion.values()) {
            if (gatheringRegion.getIdentifier().equals(identifier))
                return gatheringRegion;
        }
        return null;
    }

    public String getIdentifier() {
        return identifier;
    }
}
