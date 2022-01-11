package com.runicrealms.plugin.professions;

public enum Profession {

    ANY("any"),
    ALCHEMIST("alchemist"),
    BLACKSMITH("blacksmith"),
    COOKING("cooking"),
    ENCHANTER("enchanter"),
    HUNTER("hunter"),
    JEWELER("jeweler");

    private final String name;

    Profession(String name) {
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
