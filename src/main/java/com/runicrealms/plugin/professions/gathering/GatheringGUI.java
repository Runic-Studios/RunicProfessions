package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.utilities.ColorUtil;
import com.runicrealms.plugin.utilities.GUIUtil;
import com.runicrealms.plugin.utilities.NumRounder;
import org.apache.commons.lang.ArrayUtils;
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

public class GatheringGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;

    public GatheringGUI(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, ColorUtil.format("&eGathering Skills"));
        this.player = player;
        openMenu();
    }

    private static String[] gatheringSkillDescription(GatherPlayer gatherPlayer, GatheringSkill gatheringSkill) {
        String[] unlockMessageArray = ProfExpUtil.nextReagentUnlockMessage(gatheringSkill,
                gatherPlayer.getGatheringLevel(gatheringSkill), true).toArray(new String[0]);
        String[] descriptionArray = new String[]{
                buildProgressBar(gatherPlayer, gatheringSkill),
                "",
                ChatColor.GRAY + "Level: " + ChatColor.WHITE + gatherPlayer.getGatheringLevel(gatheringSkill),
                ChatColor.GRAY + "Exp: " + ChatColor.WHITE + gatherPlayer.getGatheringExp(gatheringSkill),
                "",
                ChatColor.GOLD + "" + ChatColor.BOLD + "CLICK " + ChatColor.GRAY + "to view all available reagents",
                ""
        };
        return (String[]) ArrayUtils.addAll(descriptionArray, unlockMessageArray); // append formatted unlock message
    }

    private static String buildProgressBar(GatherPlayer gatherPlayer, GatheringSkill gatheringSkill) {
        String bar = "❚❚❚❚❚❚❚❚❚❚"; // 10 bars
        int currentExp = gatherPlayer.getGatheringExp(gatheringSkill);
        int currentLv = gatherPlayer.getGatheringLevel(gatheringSkill);
        int totalExpAtLevel = ProfExpUtil.calculateTotalExperience(currentLv);
        int totalExpToLevel = ProfExpUtil.calculateTotalExperience(currentLv + 1);
        double progress = (double) (currentExp - totalExpAtLevel) / (totalExpToLevel - totalExpAtLevel); // 60 - 55 = 5 / 75 - 55 = 20, 5 /20
        int progressRounded = (int) NumRounder.round(progress * 100);
        int percent = progressRounded / 10;
        return ChatColor.GREEN + bar.substring(0, percent) + ChatColor.WHITE + bar.substring(percent) +
                " (" + (currentExp - totalExpAtLevel) + "/" + (totalExpToLevel - totalExpAtLevel) + ") " +
                ChatColor.GREEN + ChatColor.BOLD + progressRounded + "% ";
    }

    private static int calculateTotalLevel(GatherPlayer gatherPlayer) {
        int totalLevel = 0;
        for (GatheringSkill gatheringSkill : GatheringSkill.values()) {
            totalLevel += gatherPlayer.getGatheringLevel(gatheringSkill);
        }
        return totalLevel;
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
        GatherPlayer gatherPlayer = RunicProfessionsAPI.getGatherPlayer(player.getUniqueId());
        this.inventory.clear();
        GUIUtil.fillInventoryBorders(this.inventory);
        this.inventory.setItem(0, GUIUtil.closeButton());
        this.inventory.setItem(4, GUIUtil.dispItem(
                Material.PAPER,
                ChatColor.YELLOW + "Skills Info",
                new String[]{
                        "",
                        ChatColor.YELLOW + "Cooking " + ChatColor.GRAY + gatherPlayer.getGatheringLevel(GatheringSkill.COOKING),
                        ChatColor.YELLOW + "Farming " + ChatColor.GRAY + gatherPlayer.getGatheringLevel(GatheringSkill.FARMING),
                        ChatColor.YELLOW + "Fishing " + ChatColor.GRAY + gatherPlayer.getGatheringLevel(GatheringSkill.FISHING),
                        ChatColor.YELLOW + "Harvesting " + ChatColor.GRAY + gatherPlayer.getGatheringLevel(GatheringSkill.HARVESTING),
                        ChatColor.YELLOW + "Mining " + ChatColor.GRAY + gatherPlayer.getGatheringLevel(GatheringSkill.MINING),
                        ChatColor.YELLOW + "Woodcutting " + ChatColor.GRAY + gatherPlayer.getGatheringLevel(GatheringSkill.WOODCUTTING),
                        "",
                        ChatColor.GRAY + "Total Level " + calculateTotalLevel(gatherPlayer)
                }
        ));
        for (GatheringSkill gatheringSkill : GatheringSkill.values()) {
            this.inventory.setItem(gatheringSkill.getMenuSlot(), gatheringItem(gatherPlayer, gatheringSkill));
        }
    }

    /**
     * Helper method to create the visual menu item for the given gathering skill
     *
     * @param gatherPlayer   the wrapper for the gathering player
     * @param gatheringSkill the gathering skill to display item for
     * @return an ItemStack that can be used for a UI menu
     */
    private ItemStack gatheringItem(GatherPlayer gatherPlayer, GatheringSkill gatheringSkill) {
        String displayName = gatheringSkill.getFormattedIdentifier();
        ItemStack menuItem = gatheringSkill.getMenuItem();
        ItemMeta meta = menuItem.getItemMeta();
        if (meta == null) return menuItem;
        meta.setDisplayName(ChatColor.YELLOW + displayName);
        meta.setLore(Arrays.asList(gatheringSkillDescription(gatherPlayer, gatheringSkill)));
        menuItem.setItemMeta(meta);
        return menuItem;
    }
}