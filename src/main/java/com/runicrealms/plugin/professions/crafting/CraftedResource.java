package com.runicrealms.plugin.professions.crafting;

import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.utilities.ProfUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItem;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

/**
 * A handy class that bundles some key data for gathering resources
 */
public enum CraftedResource {

    /*
    Cooking
     */
    BREAD("Bread", Profession.ANY, new LinkedHashMap<String, Integer>() {{
        put("Wheat", 5);
        put("OakWood", 1);
    }}),
    COOKED_MEAT("CookedMeat", Profession.ANY, new LinkedHashMap<String, Integer>() {{
        put("RawMeat", 4);
        put("OakWood", 2);
    }}),
    COOKED_COD("CookedCod", Profession.ANY, new LinkedHashMap<String, Integer>() {{
        put("Cod", 1);
        put("SpruceWood", 1);
    }}),
    COOKED_SALMON("CookedSalmon", Profession.ANY, new LinkedHashMap<String, Integer>() {{
        put("Salmon", 1);
        put("SpruceWood", 1);
    }}),
    AMBROSIA_STEW("ambrosia-stew", Profession.ANY, new LinkedHashMap<String, Integer>() {{
        put("AmbrosiaRoot", 1);
        put("UncookedRabbit", 4);
        put("BirchWood", 2);
    }}),

    /*
    Alchemist
     */
    BOTTLE("Bottle", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("glass-shard", 3);
    }}),
    LESSER_POTION_HEALING("lesser-crafted-potion-healing", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-ruby", 4);
        put("Salmon", 3);
    }}),
    LESSER_POTION_MANA("lesser-crafted-potion-mana", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-sapphire", 4);
        put("Cod", 3);
    }}),
    LESSER_POTION_SLAYING("lesser-crafted-potion-slaying", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-opal", 4);
        put("uncut-diamond", 3);
        put("Tropical", 4);
    }}),
    MINOR_POTION_HEALING("minor-crafted-potion-healing", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-ruby", 4);
        put("Salmon", 3);
    }}),
    MINOR_POTION_MANA("minor-crafted-potion-mana", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-sapphire", 4);
        put("Cod", 3);
    }}),
    MINOR_POTION_SLAYING("minor-crafted-potion-slaying", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-opal", 4);
        put("uncut-diamond", 3);
        put("Tropical", 4);
    }}),
    MINOR_POTION_LOOTING("minor-crafted-potion-looting", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("AmbrosiaRoot", 1);
        put("Pufferfish", 4);
    }}),
    MAJOR_POTION_HEALING("major-crafted-potion-healing", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-ruby", 4);
        put("Salmon", 3);
    }}),
    MAJOR_POTION_MANA("major-crafted-potion-mana", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-sapphire", 4);
        put("Cod", 3);
    }}),
    MAJOR_POTION_SLAYING("major-crafted-potion-slaying", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-opal", 4);
        put("uncut-diamond", 3);
        put("Tropical", 4);
    }}),
    MAJOR_POTION_LOOTING("major-crafted-potion-looting", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("AmbrosiaRoot", 1);
        put("Pufferfish", 4);
    }}),
    GREATER_POTION_HEALING("greater-crafted-potion-healing", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-ruby", 4);
        put("Salmon", 3);
    }}),
    GREATER_POTION_MANA("greater-crafted-potion-mana", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-sapphire", 4);
        put("Cod", 3);
    }}),
    GREATER_POTION_SLAYING("greater-crafted-potion-slaying", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-opal", 4);
        put("uncut-diamond", 3);
        put("Tropical", 4);
    }}),
    GREATER_POTION_LOOTING("greater-crafted-potion-looting", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("AmbrosiaRoot", 1);
        put("Pufferfish", 4);
    }}),
    POTION_SACRED_FIRE("crafted-potion-sacred-fire", Profession.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("SacredFlame", 3);
        put("Pufferfish", 4);
    }}),

    /*
    Blacksmith
     */
    CHAIN_LINK("chain-link", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-ore", 4);
        put("SpruceWood", 4);
    }}),
    IRON_BAR("iron-bar", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-ore", 4);
        put("OakWood", 4);
    }}),
    GOLD_BAR("gold-bar", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-ore", 4);
        put("OakWood", 4);
    }}),
    OFFHAND_DEX_10("blacksmith-offhand-dex-10", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 6);
        put("OakWood", 24);
        put("crimson-core", 1);
    }}),
    OFFHAND_DEX_20("blacksmith-offhand-dex-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 8);
        put("SpruceWood", 32);
        put("sapphirine-shard", 2);
    }}),
    OFFHAND_DEX_30("blacksmith-offhand-dex-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 16);
        put("BirchWood", 64);
        put("glacial-fragment", 5);
    }}),
    OFFHAND_DEX_40("blacksmith-offhand-dex-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 28);
        put("DarkOakWood", 112);
        put("jade-gemstone", 9);
    }}),
    OFFHAND_DEX_50("blacksmith-offhand-dex-50", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 40);
        put("JungleWood", 160);
        put("crystal-corpus", 16);
    }}),
    OFFHAND_DEX_60("blacksmith-offhand-dex-60", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 64);
        put("JungleWood", 256);
        put("black-matter", 32);
    }}),
    WEAPON_BOW_20("blacksmith-weapon-bow-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 8);
        put("SpruceWood", 24);
        put("glacial-fragment", 1);
    }}),
    WEAPON_BOW_30("blacksmith-weapon-bow-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 16);
        put("JungleWood", 256);
        put("jade-gemstone", 3);
    }}),
    WEAPON_BOW_40("blacksmith-weapon-bow-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 28);
        put("JungleWood", 256);
        put("crystal-corpus", 5);
    }}),
    OFFHAND_VIT_10("blacksmith-offhand-vit-10", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 6);
        put("OakWood", 6);
        put("crimson-core", 1);
    }}),
    SHARPSTONE_10("blacksmith-sharpstone-10", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 3);
    }}),
    SHARPSTONE_20("blacksmith-sharpstone-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 6);
    }}),
    SHARPSTONE_30("blacksmith-sharpstone-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 10);
    }});

    private final String templateId;
    private final Profession profession;
    private final RunicItem runicItem;
    private final ItemStack itemStack;
    private final LinkedHashMap<ItemStack, Integer> reagents;
    private final int requiredLevel;
    private final int experience;

    /**
     * Initialize an enumerated CraftedResource with all necessary info to handle crafting
     *
     * @param templateId the template of the runic item
     * @param profession which crafting profession the item corresponds to
     */
    CraftedResource(String templateId, Profession profession, LinkedHashMap<String, Integer> reagents) {
        this.templateId = templateId;
        this.profession = profession;
        this.runicItem = RunicItemsAPI.generateItemFromTemplate(templateId);
        this.itemStack = this.runicItem.generateItem();
        this.reagents = new LinkedHashMap<>();
        for (String key : reagents.keySet())
            this.reagents.put(RunicItemsAPI.generateItemFromTemplate(key).generateItem(), reagents.get(key));
        this.requiredLevel = ProfUtil.getRunicItemDataFieldInt(RunicItemsAPI.generateItemFromTemplate(templateId), ProfUtil.REQUIRED_LEVEL_KEY);
        this.experience = ProfUtil.getRunicItemDataFieldInt(RunicItemsAPI.generateItemFromTemplate(templateId), ProfUtil.EXPERIENCE_KEY);
    }

    public String getTemplateId() {
        return templateId;
    }

    public Profession getProfessionEnum() {
        return profession;
    }

    public RunicItem getRunicItem() {
        return runicItem;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public LinkedHashMap<ItemStack, Integer> getReagents() {
        return reagents;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getExperience() {
        return experience;
    }
}
