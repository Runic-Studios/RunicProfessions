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
        put("OakWood", 18);
        put("crimson-core", 1);
    }}),
    OFFHAND_DEX_20("blacksmith-offhand-dex-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 8);
        put("SpruceWood", 24);
        put("sapphirine-shard", 2);
    }}),
    OFFHAND_DEX_30("blacksmith-offhand-dex-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 16);
        put("BirchWood", 48);
        put("glacial-fragment", 5);
    }}),
    OFFHAND_DEX_40("blacksmith-offhand-dex-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 28);
        put("DarkOakWood", 84);
        put("jade-gemstone", 9);
    }}),
    OFFHAND_DEX_50("blacksmith-offhand-dex-50", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 40);
        put("JungleWood", 120);
        put("crystal-corpus", 16);
    }}),
    OFFHAND_DEX_60("blacksmith-offhand-dex-60", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("chain-link", 64);
        put("JungleWood", 192);
        put("black-matter", 32);
    }}),
    WEAPON_BOW_20("blacksmith-weapon-bow-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 8);
        put("SpruceWood", 24);
        put("glacial-fragment", 1);
    }}),
    WEAPON_BOW_30("blacksmith-weapon-bow-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 16);
        put("BirchWood", 48);
        put("jade-gemstone", 3);
    }}),
    WEAPON_BOW_40("blacksmith-weapon-bow-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 28);
        put("DarkOakWood", 84);
        put("crystal-corpus", 5);
    }}),
    OFFHAND_WIS_10("blacksmith-offhand-wis-10", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 6);
        put("OakWood", 18);
        put("crimson-core", 1);
    }}),
    OFFHAND_WIS_20("blacksmith-offhand-wis-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 8);
        put("SpruceWood", 24);
        put("sapphirine-shard", 2);
    }}),
    OFFHAND_WIS_30("blacksmith-offhand-wis-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 16);
        put("BirchWood", 48);
        put("glacial-fragment", 5);
    }}),
    OFFHAND_WIS_40("blacksmith-offhand-wis-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 28);
        put("DarkOakWood", 84);
        put("jade-gemstone", 9);
    }}),
    OFFHAND_WIS_50("blacksmith-offhand-wis-50", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 40);
        put("JungleWood", 120);
        put("crystal-corpus", 16);
    }}),
    OFFHAND_WIS_60("blacksmith-offhand-wis-60", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 64);
        put("JungleWood", 192);
        put("black-matter", 32);
    }}),
    WEAPON_MACE_20("blacksmith-weapon-mace-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 8);
        put("SpruceWood", 24);
        put("glacial-fragment", 1);
    }}),
    WEAPON_MACE_30("blacksmith-weapon-mace-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 16);
        put("BirchWood", 48);
        put("jade-gemstone", 3);
    }}),
    WEAPON_MACE_40("blacksmith-weapon-mace-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 28);
        put("DarkOakWood", 84);
        put("crystal-corpus", 5);
    }}),
    OFFHAND_INT_10("blacksmith-offhand-int-10", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 6);
        put("OakWood", 6);
        put("crimson-core", 1);
    }}),
    OFFHAND_INT_20("blacksmith-offhand-int-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 8);
        put("SpruceWood", 24);
        put("sapphirine-shard", 2);
    }}),
    OFFHAND_INT_30("blacksmith-offhand-int-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 16);
        put("BirchWood", 48);
        put("glacial-fragment", 5);
    }}),
    OFFHAND_INT_40("blacksmith-offhand-int-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 28);
        put("DarkOakWood", 84);
        put("jade-gemstone", 9);
    }}),
    OFFHAND_INT_50("blacksmith-offhand-int-50", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 40);
        put("JungleWood", 120);
        put("crystal-corpus", 16);
    }}),
    OFFHAND_INT_60("blacksmith-offhand-int-60", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-bar", 64);
        put("JungleWood", 192);
        put("black-matter", 32);
    }}),
    SHARPSTONE_10("blacksmith-sharpstone-10", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 3);
    }}),
    SHARPSTONE_20("blacksmith-sharpstone-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 6);
    }}),
    SHARPSTONE_30("blacksmith-sharpstone-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 10);
    }}),
    OFFHAND_STR_10("blacksmith-offhand-str-10", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 6);
        put("OakWood", 6);
        put("crimson-core", 1);
    }}),
    OFFHAND_STR_20("blacksmith-offhand-str-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 8);
        put("SpruceWood", 24);
        put("sapphirine-shard", 2);
    }}),
    OFFHAND_STR_30("blacksmith-offhand-str-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 16);
        put("BirchWood", 48);
        put("glacial-fragment", 5);
    }}),
    OFFHAND_STR_40("blacksmith-offhand-str-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 28);
        put("DarkOakWood", 84);
        put("jade-gemstone", 9);
    }}),
    OFFHAND_STR_50("blacksmith-offhand-str-50", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 40);
        put("JungleWood", 120);
        put("crystal-corpus", 16);
    }}),
    OFFHAND_STR_60("blacksmith-offhand-str-60", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 64);
        put("JungleWood", 192);
        put("black-matter", 32);
    }}),
    WEAPON_SWORD_20("blacksmith-weapon-sword-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 8);
        put("SpruceWood", 24);
        put("glacial-fragment", 1);
    }}),
    WEAPON_SWORD_30("blacksmith-weapon-sword-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 16);
        put("BirchWood", 48);
        put("jade-gemstone", 3);
    }}),
    WEAPON_SWORD_40("blacksmith-weapon-sword-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 28);
        put("DarkOakWood", 84);
        put("crystal-corpus", 5);
    }}),
    OFFHAND_VIT_10("blacksmith-offhand-vit-10", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 6);
        put("OakWood", 6);
        put("crimson-core", 1);
    }}),
    OFFHAND_VIT_20("blacksmith-offhand-vit-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 8);
        put("SpruceWood", 24);
        put("sapphirine-shard", 2);
    }}),
    OFFHAND_VIT_30("blacksmith-offhand-vit-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 16);
        put("BirchWood", 48);
        put("glacial-fragment", 5);
    }}),
    OFFHAND_VIT_40("blacksmith-offhand-vit-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 28);
        put("DarkOakWood", 84);
        put("jade-gemstone", 9);
    }}),
    OFFHAND_VIT_50("blacksmith-offhand-vit-50", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 40);
        put("JungleWood", 120);
        put("crystal-corpus", 16);
    }}),
    OFFHAND_VIT_60("blacksmith-offhand-vit-60", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 64);
        put("JungleWood", 192);
        put("black-matter", 32);
    }}),
    WEAPON_AXE_20("blacksmith-weapon-axe-20", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 8);
        put("SpruceWood", 24);
        put("glacial-fragment", 1);
    }}),
    WEAPON_AXE_30("blacksmith-weapon-axe-30", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 16);
        put("BirchWood", 48);
        put("jade-gemstone", 3);
    }}),
    WEAPON_AXE_40("blacksmith-weapon-axe-40", Profession.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 28);
        put("DarkOakWood", 84);
        put("crystal-corpus", 5);
    }}),

    /*
    Jeweler
     */
    CUT_RUBY_I("cut-ruby-i", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-ruby", 16);
        put("AnimalHide", 4);
        put("Comfrey", 4);
    }}),
    CUT_RUBY_II("cut-ruby-ii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-ruby", 32);
        put("AnimalHide", 8);
        put("Wintercress", 8);
    }}),
    CUT_RUBY_III("cut-ruby-iii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-ruby", 64);
        put("ancient-matter", 4);
        put("Lavender", 12);
    }}),
    CUT_RUBY_IV("cut-ruby-iv", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-ruby", 128);
        put("ancient-matter", 3);
        put("Lavender", 16);
    }}),
    CUT_RUBY_V("cut-ruby-v", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-ruby", 192);
        put("ancient-matter", 8);
        put("ancient-core", 1);
    }}),
    CUT_SAPPHIRE_I("cut-sapphire-i", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-sapphire", 16);
        put("AnimalHide", 4);
        put("Comfrey", 4);
    }}),
    CUT_SAPPHIRE_II("cut-sapphire-ii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-sapphire", 32);
        put("AnimalHide", 8);
        put("Wintercress", 8);
    }}),
    CUT_SAPPHIRE_III("cut-sapphire-iii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-sapphire", 64);
        put("ancient-matter", 4);
        put("Lavender", 12);
    }}),
    CUT_SAPPHIRE_IV("cut-sapphire-iv", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-sapphire", 128);
        put("ancient-matter", 3);
        put("Lavender", 16);
    }}),
    CUT_SAPPHIRE_V("cut-sapphire-v", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-sapphire", 192);
        put("ancient-matter", 8);
        put("ancient-core", 1);
    }}),
    CUT_DIAMOND_I("cut-diamond-i", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-diamond", 16);
        put("AnimalHide", 4);
        put("Comfrey", 4);
    }}),
    CUT_DIAMOND_II("cut-diamond-ii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-diamond", 32);
        put("AnimalHide", 8);
        put("Wintercress", 8);
    }}),
    CUT_DIAMOND_III("cut-diamond-iii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-diamond", 64);
        put("ancient-matter", 4);
        put("Lavender", 12);
    }}),
    CUT_DIAMOND_IV("cut-diamond-iv", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-diamond", 128);
        put("ancient-matter", 3);
        put("Lavender", 16);
    }}),
    CUT_DIAMOND_V("cut-diamond-v", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-diamond", 192);
        put("ancient-matter", 8);
        put("ancient-core", 1);
    }}),
    CUT_EMERALD_I("cut-emerald-i", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-emerald", 16);
        put("AnimalHide", 4);
        put("Comfrey", 4);
    }}),
    CUT_EMERALD_II("cut-emerald-ii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-emerald", 32);
        put("AnimalHide", 8);
        put("Wintercress", 8);
    }}),
    CUT_EMERALD_III("cut-emerald-iii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-emerald", 64);
        put("ancient-matter", 4);
        put("Lavender", 12);
    }}),
    CUT_EMERALD_IV("cut-emerald-iv", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-emerald", 128);
        put("ancient-matter", 3);
        put("Lavender", 16);
    }}),
    CUT_EMERALD_V("cut-emerald-v", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-emerald", 192);
        put("ancient-matter", 8);
        put("ancient-core", 1);
    }}),
    CUT_OPAL_I("cut-opal-i", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-opal", 16);
        put("AnimalHide", 4);
        put("Comfrey", 4);
    }}),
    CUT_OPAL_II("cut-opal-ii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-opal", 32);
        put("AnimalHide", 8);
        put("Wintercress", 8);
    }}),
    CUT_OPAL_III("cut-opal-iii", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-opal", 64);
        put("ancient-matter", 4);
        put("Lavender", 12);
    }}),
    CUT_OPAL_IV("cut-opal-iv", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-opal", 128);
        put("ancient-matter", 3);
        put("Lavender", 16);
    }}),
    CUT_OPAL_V("cut-opal-v", Profession.JEWELER, new LinkedHashMap<String, Integer>() {{
        put("uncut-opal", 192);
        put("ancient-matter", 8);
        put("ancient-core", 1);
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