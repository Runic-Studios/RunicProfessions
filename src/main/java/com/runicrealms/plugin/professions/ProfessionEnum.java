package com.runicrealms.plugin.professions;

public enum ProfessionEnum {

    ALCHEMIST("alchemist"),
    BLACKSMITH("blacksmith"),
    ENCHANTER("enchanter"),
    HUNTER("hunter"),
    JEWELER("jeweler");

    private final String name;

    ProfessionEnum(String name) {
        this.name = name;
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
