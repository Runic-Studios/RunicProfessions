package com.runicrealms.plugin.professions.crafting.hunter;

public enum TaskMobs {
    GOBLIN(0, 3, 5, "azana_goblin", "azana"),
    RAT(0, 3, 5, "azana_rat", "azana"),
    BEE(0, 3, 5, "azana_bee", "azana"),

    GOLEM(3, 9, 6, "koldore_golem", "koldore"),
    SKELETON(3, 9, 6, "koldore_skeleton", "koldore"),
    MOLE(3, 9, 6, "koldore_mole", "koldore"),

    SNOWMAN(6, 15, 7, "whaletown_snowman", "whaletown"),
    SNOW_RABBIT(6, 15, 7, "whaletown_rabbit", "whaletown"),
    SNOW_GOLEM(6, 15, 7, "whaletown_golem", "whaletown"),

    ROGUE_HORSE(9, 21, 8, "hilstead_horse", "hilstead"),
    SQUID(9, 21, 8, "hilstead_squid", "hilstead"),
    DROWNED(9, 21, 8, "hilstead_drowned", "hilstead"),

    SNOW_FOX(15, 30, 9, "wintervale_fox", "wintervale"),
    POLAR_BEAR(15, 30, 9, "wintervale_bear", "wintervale"),
    VALE_MAGE(15, 30, 9, "wintervale_mage", "wintervale"),

    ELITE_SKELETON(17, 32, 10, "rest_skeleton", "dead_mans_rest"),
    ZOMBIE(17, 32, 10, "rest_zombie", "dead_mans_rest"),
    SOUL(17, 32, 10, "rest_soul", "dead_mans_rest"),

    SPINNER(25, 45, 11, "isfodar_spider", "isfodar"),
    DRUID(25, 45, 11, "isfodar_druid", "isfodar"),
    SLIME(25, 45, 11, "isfodar_slime", "isfodar"),

    PHANTOM(28, 48, 12, "tireneas_phantom", "tireneas"),
    WATER_NYMPH(28, 48, 12, "tireneas_nymph", "tireneas"),
    ROGUE_FARMER(28, 48, 12, "tireneas_farmer", "tireneas"),

    ZENYTH_BANDIT(31, 52, 13, "zenyth_bandit", "zenyth"),
    SANDWORM(31, 52, 13, "zenyth_worm", "zenyth"),
    SAND_RAT(31, 52, 13, "zenyth_rat", "zenyth"),

    SAND_CAT(31, 52, 13, "naheen_cat", "naheen"),
    LAND_SHARK(31, 52, 13, "naheen_shark", "naheen"),
    INVASIVE_CORAL(31, 52, 13, "naheen_coral", "naheen"),

    APE(41, 70, 14, "nazmora_monkey", "nazmora"),
    ABOMINATION(41, 70, 14, "nazmora_abomination", "nazmora"),
    ORC_BANDIT(41, 70, 14, "nazmora_bandit", "nazmora"),

    YETI(48, 80, 15, "frost_yeti", "frost_end"),
    FROST_BEAR(48, 80, 15, "frost_bear", "frost_end"),
    FROST_GOLEM(48, 80, 15, "frost_golem", "frost_end");

    private final String name;
    private final int level;
    private final int experience;
    private final int points;
    private final String internalName;
    private final String region;

    TaskMobs(int level, int exp, int points, String internalName, String region) {
        this.name = this.buildName();
        this.level = level;
        this.experience = exp;
        this.points = points;
        this.internalName = internalName;
        this.region = region;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public int getExperience() {
        return this.experience;
    }

    public int getPoints() {
        return this.points;
    }

    public String getInternalName() {
        return this.internalName;
    }

    public String getRegion() {
        return this.region;
    }

    private String buildName() {
        if (this.name().contains("_")) {
            StringBuilder builder = new StringBuilder();

            int i = 1;
            String[] splitWord = this.name().split("_");
            for (String word : splitWord) {
                builder.append(word.charAt(0)).append(word.substring(1).toLowerCase());

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
