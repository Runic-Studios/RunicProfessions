package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.common.util.ColorUtil;
import com.runicrealms.plugin.common.util.GUIUtil;
import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.WorkstationType;
import com.runicrealms.plugin.professions.config.WorkstationLoader;
import com.runicrealms.plugin.runicitems.RunicItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class GatheringSkillGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;
    private final GatheringSkill gatheringSkill;

    public GatheringSkillGUI(Player player, GatheringSkill gatheringSkill) {
        this.inventory = Bukkit.createInventory(this, 54, ColorUtil.format("&e" + gatheringSkill.getFormattedIdentifier()));
        this.player = player;
        this.gatheringSkill = gatheringSkill;
        openMenu();
    }

    /**
     * @param itemStack       the ui item
     * @param skillIdentifier the string to identify the skill "fishing"
     * @param requiredLevel   the required level to gather the material
     * @param experience      the experience given by the material
     * @param isUnlocked      true if player has unlocked material
     * @return an item for display
     */
    private static ItemStack reagentWithLore(ItemStack itemStack, String skillIdentifier, int requiredLevel, int experience, boolean isUnlocked) {
        ItemStack reagentWithLore = itemStack.clone();
        ItemMeta meta = reagentWithLore.getItemMeta();
        assert meta != null;
        if (isUnlocked)
            meta.setDisplayName(ChatColor.GREEN + ChatColor.stripColor(meta.getDisplayName()) + " - UNLOCKED");
        else
            meta.setDisplayName(ChatColor.RED + ChatColor.stripColor(meta.getDisplayName()) + " - LOCKED");
        List<String> lore = meta.getLore();
        assert lore != null;
        lore.add("");
        lore.add
                (
                        ChatColor.GRAY + "Requires " +
                                ChatColor.YELLOW + skillIdentifier + " " +
                                ChatColor.WHITE + requiredLevel);
        lore.add("");
        lore.add
                (
                        ChatColor.GRAY + String.valueOf(ChatColor.ITALIC) + "Rewards " +
                                ChatColor.WHITE + ChatColor.ITALIC + experience +
                                ChatColor.GRAY + ChatColor.ITALIC + " experience");
        meta.setLore(lore);
        reagentWithLore.setItemMeta(meta);
        return reagentWithLore;
    }

    private void addCookingResources(GatheringData gatheringData) {
        // Get a collection of crafted resources associated with this ui menu
        Collection<CraftedResource> craftedResources = WorkstationLoader.getCraftedResources().get(WorkstationType.COOKING_FIRE);
//        Arrays.sort(craftedResources, Comparator.comparing(ListenerResource::getRequiredLevel)); // sort in ascending order of level
        for (CraftedResource craftedResource : craftedResources) {
            if (craftedResource.getProfession() != Profession.COOKING) continue;
            ItemStack itemStack = RunicItemsAPI.generateItemFromTemplate(craftedResource.getTemplateId()).generateGUIItem();
            this.inventory.setItem(this.inventory.firstEmpty(), reagentWithLore
                    (
                            itemStack,
                            GatheringSkill.COOKING.getFormattedIdentifier(),
                            craftedResource.getRequiredLevel(),
                            craftedResource.getExperience(),
                            (gatheringData.getGatheringLevel(GatheringSkill.COOKING) >= craftedResource.getRequiredLevel())
                    ));
        }
    }

    private void addGatheringResources(GatheringData gatheringData) {
        GatheringResource[] gatheringResources = GatheringResource.values();
        Arrays.sort(gatheringResources, Comparator.comparing(GatheringResource::getRequiredLevel)); // sort in ascending order of level
        for (GatheringResource gatheringResource : gatheringResources) {
            if (gatheringResource.getGatheringSkill() != this.gatheringSkill) continue;
            ItemStack itemStack = RunicItemsAPI.generateItemFromTemplate(gatheringResource.getTemplateId()).generateGUIItem();
            this.inventory.setItem(this.inventory.firstEmpty(), reagentWithLore
                    (
                            itemStack,
                            gatheringResource.getGatheringSkill().getFormattedIdentifier(),
                            gatheringResource.getRequiredLevel(),
                            gatheringResource.getExperience(),
                            (gatheringData.getGatheringLevel(gatheringResource.getGatheringSkill()) >= gatheringResource.getRequiredLevel())
                    ));
        }
    }

    public GatheringSkill getGatheringSkill() {
        return gatheringSkill;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Opens the inventory associated w/ this GUI, ordering perks
     */
    private void openMenu() {
        GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(player.getUniqueId());
        this.inventory.clear();
        GUIUtil.fillInventoryBorders(this.inventory);
        this.inventory.setItem(0, GUIUtil.BACK_BUTTON);
        this.inventory.setItem(4, GUIUtil.dispItem(
                Material.PAPER,
                ChatColor.YELLOW + gatheringSkill.getFormattedIdentifier() +
                        ChatColor.GRAY + " Level " + gatheringData.getGatheringLevel(gatheringSkill),
                new String[]{}
        ));
        if (gatheringSkill == GatheringSkill.COOKING) {
            addCookingResources(gatheringData);
        } else {
            addGatheringResources(gatheringData);
        }
    }
}