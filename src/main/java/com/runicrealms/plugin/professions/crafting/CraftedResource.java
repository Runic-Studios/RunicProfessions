package com.runicrealms.plugin.professions.crafting;

import com.runicrealms.plugin.professions.ProfessionEnum;
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
    Alchemist
     */
    BOTTLE("Bottle", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("glass-shard", 3);
    }}),
    LESSER_POTION_HEALING("lesser-crafted-potion-healing", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-ruby", 4);
        put("Salmon", 3);
    }}),
    LESSER_POTION_MANA("lesser-crafted-potion-mana", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-sapphire", 4);
        put("Cod", 3);
    }}),
    LESSER_POTION_SLAYING("lesser-crafted-potion-slaying", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-opal", 4);
        put("uncut-diamond", 3);
        put("Tropical", 4);
    }}),
    MINOR_POTION_HEALING("minor-crafted-potion-healing", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-ruby", 4);
        put("Salmon", 3);
    }}),
    MINOR_POTION_MANA("minor-crafted-potion-mana", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-sapphire", 4);
        put("Cod", 3);
    }}),
    MINOR_POTION_SLAYING("minor-crafted-potion-slaying", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-opal", 4);
        put("uncut-diamond", 3);
        put("Tropical", 4);
    }}),
    MINOR_POTION_LOOTING("minor-crafted-potion-looting", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("AmbrosiaRoot", 1);
        put("Pufferfish", 4);
    }}),
    MAJOR_POTION_HEALING("major-crafted-potion-healing", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-ruby", 4);
        put("Salmon", 3);
    }}),
    MAJOR_POTION_MANA("major-crafted-potion-mana", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-sapphire", 4);
        put("Cod", 3);
    }}),
    MAJOR_POTION_SLAYING("major-crafted-potion-slaying", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-opal", 4);
        put("uncut-diamond", 3);
        put("Tropical", 4);
    }}),
    MAJOR_POTION_LOOTING("major-crafted-potion-looting", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("AmbrosiaRoot", 1);
        put("Pufferfish", 4);
    }}),
    GREATER_POTION_HEALING("greater-crafted-potion-healing", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-ruby", 4);
        put("Salmon", 3);
    }}),
    GREATER_POTION_MANA("greater-crafted-potion-mana", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-sapphire", 4);
        put("Cod", 3);
    }}),
    GREATER_POTION_SLAYING("greater-crafted-potion-slaying", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("uncut-opal", 4);
        put("uncut-diamond", 3);
        put("Tropical", 4);
    }}),
    GREATER_POTION_LOOTING("greater-crafted-potion-looting", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("AmbrosiaRoot", 1);
        put("Pufferfish", 4);
    }}),
    POTION_SACRED_FIRE("crafted-potion-sacred-fire", ProfessionEnum.ALCHEMIST, new LinkedHashMap<String, Integer>() {{
        put("Bottle", 1);
        put("SacredFlame", 3);
        put("Pufferfish", 4);
    }}),

    /*
    Blacksmith
     */
    CHAIN_LINK("chain-link", ProfessionEnum.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-ore", 4);
        put("SpruceWood", 4);
    }}),
    IRON_BAR("iron-bar", ProfessionEnum.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-ore", 4);
        put("OakWood", 4);
    }}),
    GOLD_BAR("gold-bar", ProfessionEnum.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("gold-ore", 4);
        put("OakWood", 4);
    }}),
    OFFHAND_VIT_10("blacksmith-offhand-vit-10", ProfessionEnum.BLACKSMITH, new LinkedHashMap<String, Integer>() {{
        put("iron-bar", 6);
        put("OakWood", 6);
        put("crimson-core", 1);
    }});

    private final String templateId;
    private final ProfessionEnum professionEnum;
    private final RunicItem runicItem;
    private final ItemStack itemStack;
    private final LinkedHashMap<ItemStack, Integer> reagents;
    private final int requiredLevel;
    private final int experience;

    /**
     * Initialize an enumerated CraftedResource with all necessary info to handle crafting
     *
     * @param templateId     the template of the runic item
     * @param professionEnum which crafting profession the item corresponds to
     */
    CraftedResource(String templateId, ProfessionEnum professionEnum, LinkedHashMap<String, Integer> reagents) {
        this.templateId = templateId;
        this.professionEnum = professionEnum;
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

    public ProfessionEnum getProfessionEnum() {
        return professionEnum;
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
