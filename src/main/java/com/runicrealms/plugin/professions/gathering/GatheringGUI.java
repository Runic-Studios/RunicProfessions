package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.common.util.ColorUtil;
import com.runicrealms.plugin.common.util.GUIUtil;
import com.runicrealms.plugin.professions.listeners.GatheringLevelChangeListener;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.utilities.NumRounder;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GatheringGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;

    public GatheringGUI(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, ColorUtil.format("&eGathering Skills"));
        this.player = player;
        openMenu();
    }

    private static String[] gatheringSkillDescription(GatheringData gatheringData, GatheringSkill gatheringSkill) {
        String[] unlockMessageArray;
        if (gatheringSkill == GatheringSkill.COOKING) {
            unlockMessageArray = GatheringLevelChangeListener.nextReagentUnlockMessageCooking
                    (gatheringData.getGatheringLevel(gatheringSkill), true).toArray(new String[0]);
        } else {
            unlockMessageArray = GatheringLevelChangeListener.nextReagentUnlockMessage(gatheringSkill,
                    gatheringData.getGatheringLevel(gatheringSkill), true).toArray(new String[0]);
        }
        int level = gatheringData.getGatheringLevel(gatheringSkill);
//        boolean isSpecialized = ProfessionsAPI.isSpecializedInGatheringSkill(gatheringData, gatheringSkill);
        boolean isSpecialized = false;
        boolean professionIsMaxed = (level == 60 && !isSpecialized) || (level == 100 && isSpecialized);
        String[] descriptionArray = new String[]{
                buildProgressBar(gatheringData, gatheringSkill),
                "",
                ChatColor.GRAY + "Level: " + ChatColor.WHITE + level + (professionIsMaxed ? " (Cap Reached)" : ""),
                ChatColor.GRAY + "Exp: " + ChatColor.WHITE + gatheringData.getGatheringExp(gatheringSkill),
                "",
                ChatColor.GOLD + "" + ChatColor.BOLD + "CLICK " + ChatColor.GRAY + "to view all available reagents",
                ""
        };
        return (String[]) ArrayUtils.addAll(descriptionArray, unlockMessageArray); // append formatted unlock message
    }

    private static String buildProgressBar(GatheringData gatheringData, GatheringSkill gatheringSkill) {
        String bar = "❚❚❚❚❚❚❚❚❚❚"; // 10 bars
        try {
            int currentExp = gatheringData.getGatheringExp(gatheringSkill);
            int currentLv = gatheringData.getGatheringLevel(gatheringSkill);
            int totalExpAtLevel = ProfExpUtil.calculateTotalExperience(currentLv);
            int totalExpToLevel = ProfExpUtil.calculateTotalExperience(currentLv + 1);
            double progress = (double) (currentExp - totalExpAtLevel) / (totalExpToLevel - totalExpAtLevel); // 60 - 55 = 5 / 75 - 55 = 20, 5 /20
            int progressRounded = (int) NumRounder.round(progress * 100);
            int percent = Math.min(progressRounded / 10, 10); // limit percent to a maximum of 10
            return ChatColor.GREEN + bar.substring(0, percent) + ChatColor.WHITE + bar.substring(percent) +
                    " (" + (currentExp - totalExpAtLevel) + "/" + (totalExpToLevel - totalExpAtLevel) + ") " +
                    ChatColor.GREEN + ChatColor.BOLD + progressRounded + "% ";
        } catch (Exception ex) {
            Bukkit.getLogger().warning("There was a problem creating the gathering progress bar for " + gatheringSkill.getIdentifier());
            ex.printStackTrace();
        }
        return ChatColor.WHITE + bar;
    }

    private static int calculateTotalLevel(GatheringData gatheringData) {
        int totalLevel = 0;
        for (GatheringSkill gatheringSkill : GatheringSkill.values()) {
            totalLevel += gatheringData.getGatheringLevel(gatheringSkill);
        }
        return totalLevel;
    }

    /**
     * Helper method to create the visual menu item for the given gathering skill
     *
     * @param gatheringData  the wrapper for the gathering player
     * @param gatheringSkill the gathering skill to display item for
     * @return an ItemStack that can be used for a UI menu
     */
    private ItemStack gatheringItem(GatheringData gatheringData, GatheringSkill gatheringSkill) {
        String displayName = gatheringSkill.getFormattedIdentifier();
        ItemStack menuItem = gatheringSkill.getMenuItem();
        ItemMeta meta = menuItem.getItemMeta();
        if (meta == null) return menuItem;
//        if (!ProfessionsAPI.isSpecializedInGatheringSkill(gatheringData, gatheringSkill))
        meta.setDisplayName(ChatColor.YELLOW + displayName);
//        else
//            meta.setDisplayName(ChatColor.GREEN + displayName + " - SPECIALIZED");
        meta.setLore(Arrays.asList(gatheringSkillDescription(gatheringData, gatheringSkill)));
//        if (ProfessionsAPI.isSpecializedInGatheringSkill(gatheringData, gatheringSkill))
//            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        menuItem.setItemMeta(meta);
        return menuItem;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public Player getPlayer() {
        return this.player;
    }

    /**
     * Opens the inventory associated w/ this GUI, ordering perks
     */
    private void openMenu() {
        GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(player.getUniqueId());
        this.inventory.clear();
        GUIUtil.fillInventoryBorders(this.inventory);
        this.inventory.setItem(0, GUIUtil.CLOSE_BUTTON);
        this.inventory.setItem(4, GUIUtil.dispItem(
                Material.PAPER,
                ChatColor.YELLOW + "Skills Info",
                new String[]{
                        "",
                        ChatColor.YELLOW + "Cooking " + ChatColor.GRAY + gatheringData.getGatheringLevel(GatheringSkill.COOKING),
                        ChatColor.YELLOW + "Farming " + ChatColor.GRAY + gatheringData.getGatheringLevel(GatheringSkill.FARMING),
                        ChatColor.YELLOW + "Fishing " + ChatColor.GRAY + gatheringData.getGatheringLevel(GatheringSkill.FISHING),
                        ChatColor.YELLOW + "Harvesting " + ChatColor.GRAY + gatheringData.getGatheringLevel(GatheringSkill.HARVESTING),
                        ChatColor.YELLOW + "Mining " + ChatColor.GRAY + gatheringData.getGatheringLevel(GatheringSkill.MINING),
                        ChatColor.YELLOW + "Woodcutting " + ChatColor.GRAY + gatheringData.getGatheringLevel(GatheringSkill.WOODCUTTING),
                        "",
                        ChatColor.GRAY + "Total Level " + calculateTotalLevel(gatheringData)
                }
        ));
        for (GatheringSkill gatheringSkill : GatheringSkill.values()) {
            this.inventory.setItem(gatheringSkill.getMenuSlot(), gatheringItem(gatheringData, gatheringSkill));
        }
    }
}