package com.runicrealms.plugin.professions.crafting.hunter;

import java.util.Arrays;
import java.util.List;

public enum TaskMobs {
    GOBLIN(0, 7, 5, "azana"),
    GOLEM(10, 18, 10, "koldore"),
    DIREWOLF(20, 22, 15, "hilstead", "wintervale"),
    SPINNER(30, 28, 25, "isfodar", "tireneas"),
    BUG(40, 31, 30, "zenyth", "naheen"),
    FIRE_ELEMENTAL(50, 34, 35, "zenyth", "naheen", "nazmora"),
    BLACKFROST_BEAR(60, 40, 100, "frosts_end");

    private final String name;
    private final int level;
    private final int experience;
    private final int points;
    private final List<String> regions;

    TaskMobs(int level, int exp, int points, String... regions) {
        this.name = this.buildName();
        this.level = level;
        this.experience = exp;
        this.points = points;
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

    public List<String> getRegions() {
        return this.regions;
    }

    private String buildName() {
        if (this.name().contains("_")) {
            StringBuilder builder = new StringBuilder();

            int i = 0;
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
