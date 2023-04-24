package com.runicrealms.plugin.professions;

public enum Profession {

    ANY("any", ""),
    ALCHEMIST("alchemist", "Craft powerful potions to aid yourself in battle!"),
    BLACKSMITH("blacksmith", "Forge armor, weapons, and off-hand weapons!"),
    COOKING("cooking", ""),
    ENCHANTER("enchanter", "Enchant scrolls and perform rituals to aid your allies in combat!"),
    JEWELER("jeweler", "Cut unique gemstones to increase your combat stats!"),
    NONE("None", "");

    private final String name;
    private final String description;

    Profession(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Profession getFromName(String name) {
        for (Profession profession : Profession.values()) {
            if (profession.name.equalsIgnoreCase(name))
                return profession;
        }
        return NONE;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Formats the profession name w/ an uppercase first letter for scoreboard purposes.
     *
     * @return the formatted name
     */
    public String getName() {
        return this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
    }
}
