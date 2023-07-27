package com.runicrealms.plugin.professions.crafting;

import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.utilities.ProfUtil;
import com.runicrealms.plugin.runicitems.RunicItemsAPI;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class CraftedResource {
    private final String templateId;
    private final Profession profession;
    private final int page;
    private final int slot;
    private final LinkedHashMap<ItemStack, Integer> reagents;
    private final int requiredLevel;
    private final int experience;

    /**
     * Initialize an enumerated ListenerResource with all necessary info to handle crafting
     *
     * @param templateId the template of the runic item
     * @param profession which crafting profession the item corresponds to
     */
    public CraftedResource(String templateId, Profession profession, int page, int slot, LinkedHashMap<String, Integer> reagents) {
        this.templateId = templateId;
        this.profession = profession;
        this.page = page;
        this.slot = slot;
        this.reagents = new LinkedHashMap<>();
        for (String key : reagents.keySet())
            this.reagents.put(RunicItemsAPI.generateItemFromTemplate(key).generateItem(), reagents.get(key));
        this.requiredLevel = ProfUtil.getRunicItemDataFieldInt(RunicItemsAPI.generateItemFromTemplate(templateId), ProfUtil.REQUIRED_LEVEL_KEY);
        this.experience = ProfUtil.getRunicItemDataFieldInt(RunicItemsAPI.generateItemFromTemplate(templateId), ProfUtil.EXPERIENCE_KEY);
    }

    public int getExperience() {
        return experience;
    }

    public int getPage() {
        return page;
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

    public int getSlot() {
        return slot;
    }

    public String getTemplateId() {
        return templateId;
    }
}
