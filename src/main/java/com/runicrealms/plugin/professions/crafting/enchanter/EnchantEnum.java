package com.runicrealms.plugin.professions.crafting.enchanter;

public enum EnchantEnum {

    CRIT("Crit",
            "Your attacks have a ?% chance" +
                    "\nto crit your enemy for double" +
                    "\ndamage!"),
    DODGE("Dodge",
            "You have a ?% chance to" +
                    "\ndodge incoming attacks!"),
    SPEED("Speed",
            "You passively gain ?% speed!"),
    THORNS("Thorns",
            "You have a ?% chance to reflect" +
                    "\nincoming damage, dealing the same" +
                    "\namount to your attacker!");

    private String name;
    private String description;

    EnchantEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static EnchantEnum getEnum(String value) {
        for(EnchantEnum enchantEnum : values()) {
            if (enchantEnum.getName().equalsIgnoreCase(value)) {
                return enchantEnum;
            }
        }
        throw new IllegalArgumentException();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
