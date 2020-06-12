package com.runicrealms.plugin.professions.crafting.jeweler;

import org.bukkit.Material;

public enum GemEnum {

    CUT_RUBY(Material.REDSTONE, "custom.maxHealth"),
    ORNATE_RUBY(Material.REDSTONE, "custom.healthRegen"),
    CUT_SAPPHIRE(Material.LAPIS_LAZULI, "custom.manaBoost"),
    ORNATE_SAPPHIRE(Material.LAPIS_LAZULI, "custom.manaRegen"),
    CUT_OPAL(Material.QUARTZ, "custom.attackDamage"),
    CUT_EMERALD(Material.EMERALD, "custom.healingBoost"),
    CUT_DIAMOND(Material.DIAMOND, "custom.magicDamage");

    Material material;
    String attributeName;

    /**
     * Enumerated list of all gemstones in the game
     * @param material Material of the ItemStack
     * @param attributeName Custom attribute name
     */
    GemEnum(Material material, String attributeName) {
        this.material = material;
        this.attributeName = attributeName;
    }

    Material getMaterial() {
        return material;
    }

    String getAttributeName() {
        return attributeName;
    }
}
