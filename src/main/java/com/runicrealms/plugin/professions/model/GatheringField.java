package com.runicrealms.plugin.professions.model;

public enum GatheringField {

    COOKING_EXP("cookingExp"),
    COOKING_LEVEL("cookingLevel"),
    FARMING_EXP("farmingExp"),
    FARMING_LEVEL("farmingLevel"),
    FISHING_EXP("fishingExp"),
    FISHING_LEVEL("fishingLevel"),
    HARVESTING_EXP("harvestingExp"),
    HARVESTING_LEVEL("harvestingLevel"),
    MINING_EXP("miningExp"),
    MINING_LEVEL("miningLevel"),
    WOODCUTTING_EXP("woodcuttingExp"),
    WOODCUTTING_LEVEL("woodcuttingLevel");

    private final String field;

    GatheringField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    /**
     * Returns the corresponding GatheringRedisField from the given string version
     *
     * @param field a string matching a constant
     * @return the constant
     */
    public static GatheringField getFromFieldString(String field) {
        for (GatheringField gatheringField : GatheringField.values()) {
            if (gatheringField.getField().equalsIgnoreCase(field))
                return gatheringField;
        }
        return null;
    }
}
