package com.runicrealms.plugin.professions.crafting.hunter;

import java.util.Arrays;
import java.util.List;

public enum TaskMobs {
    GOBLIN(0, 7, 5, "Goblin", "azana"),
    GOLEM(10, 18, 10, "Golem", "koldore"),
    DIREWOLF(20, 22, 15, "Direwolf", "hilstead", "wintervale"),
    SPINNER(30, 28, 25, "Spinner", "isfodar", "tireneas"),
    BUG(40, 31, 30, "Bug", "zenyth", "naheen"),
    FIRE_ELEMENTAL(50, 34, 35, "FireElemental", "zenyth", "naheen", "nazmora"),
    BLACKFROST_BEAR(60, 40, 100, "BlackfrostBear", "frosts_end"); //remember to add hunter board to frosts end

    private final String name;
    private final int level;
    private final int experience;
    private final int points;
    private final String internalName;
    private final List<String> regions;

    TaskMobs(int level, int exp, int points, String internalName, String... regions) {
        this.name = this.buildName();
        this.level = level;
        this.experience = exp;
        this.points = points;
        this.internalName = internalName;
        this.regions = Arrays.asList(regions);
    }

    public String getName() {
        return this.name;
    }

    public int getExperience() {
        return this.experience;
    }

    public int getLevel() {
        return this.level;
    }

    public int getPoints() {
        return this.points;
    }

    public String getInternalName() {
        return this.internalName;
    }

    public List<String> getRegions() {
        return this.regions;
    }

    private String buildName() {
        if (this.name().contains("_")) {
            StringBuilder builder = new StringBuilder();

            int i = 1;
            String[] splitWord = this.name().split("_");
            for (String word : splitWord) {
                builder.append(word.charAt(0) + word.substring(1).toLowerCase());

                if (i < splitWord.length) {
                    builder.append(" ");
                }

                i++;
            }

            return builder.toString();
        }

        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}
