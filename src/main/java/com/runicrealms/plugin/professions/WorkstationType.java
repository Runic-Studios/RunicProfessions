package com.runicrealms.plugin.professions;

public enum WorkstationType {

    ANVIL("Anvil", Profession.BLACKSMITH),
    CAULDRON("Cauldron", Profession.ALCHEMIST),
    COOKING_FIRE("Cooking Fire", Profession.COOKING),
    FURNACE("Furnace", Profession.BLACKSMITH),
    GEMCUTTING_BENCH("Gemcutting Bench", Profession.JEWELER),
    WOODWORKING_TABLE("Woodworking Table", Profession.NONE),
    WOODWORKING_TABLE_TUTORIAL("Tutorial Woodworking Table", Profession.NONE);

    private final String name;
    private final Profession profession;

    WorkstationType(String name, Profession profession) {
        this.name = name;
        this.profession = profession;
    }

    public static WorkstationType getFromName(String name) {
        for (WorkstationType workstationType : WorkstationType.values()) {
            if (workstationType.getName().equalsIgnoreCase(name))
                return workstationType;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Profession getProfession() {
        return profession;
    }

}
