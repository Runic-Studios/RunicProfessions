package com.runicrealms.plugin.professions.crafting;

import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.utilities.ProfUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItem;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

/**
 * An enum of crafted resources that are used in listeners
 */
public enum ListenerResource {
    /*
    Cooking
     */
    AMBROSIA_STEW("ambrosia-stew", Profession.COOKING, new LinkedHashMap<>() {{
        put("ambrosia-root", 1);
        put("rabbit", 4);
        put("birch-wood", 2);
    }});

    private final String templateId;
    private final Profession profession;
    private final RunicItem runicItem;
    private final ItemStack itemStack;
    private final LinkedHashMap<ItemStack, Integer> reagents;
    private final int requiredLevel;
    private final int experience;

    /**
     * Initialize an enumerated ListenerResource with all necessary info to handle crafting
     *
     * @param templateId the template of the runic item
     * @param profession which crafting profession the item corresponds to
     */
    ListenerResource(String templateId, Profession profession, LinkedHashMap<String, Integer> reagents) {
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

    public int getExperience() {
        return experience;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Profession getProfession() {
        return profession;
    }

    public LinkedHashMap<ItemStack, Integer> getReagents() {
        return reagents;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public RunicItem getRunicItem() {
        return runicItem;
    }

    public String getTemplateId() {
        return templateId;
    }
}
