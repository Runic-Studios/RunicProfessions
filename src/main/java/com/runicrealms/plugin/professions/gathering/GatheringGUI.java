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
import org.jetbrains.annotations.NotNull;

public class GatheringGUI implements InventoryHolder {

    private static final int[] skillSlots = new int[]{20, 22, 24, 29, 31, 33};
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
        this.inventory.setItem(skillSlots[0], GUIUtil.dispItem
                (
                        Material.COOKED_MUTTON,
                        ChatColor.YELLOW + "Cooking",
                        gatheringSkillDescription(gatherPlayer, GatheringSkill.COOKING)
                ));
        this.inventory.setItem(skillSlots[1], GUIUtil.dispItem
                (
                        Material.IRON_HOE,
                        ChatColor.YELLOW + "Farming",
                        gatheringSkillDescription(gatherPlayer, GatheringSkill.FARMING),
                        3
                ));
        this.inventory.setItem(skillSlots[2], GUIUtil.dispItem
                (
                        Material.FISHING_ROD,
                        ChatColor.YELLOW + "Fishing",
                        gatheringSkillDescription(gatherPlayer, GatheringSkill.FISHING)
                ));
        this.inventory.setItem(skillSlots[3], GUIUtil.dispItem
                (
                        Material.POPPY,
                        ChatColor.YELLOW + "Harvesting",
                        gatheringSkillDescription(gatherPlayer, GatheringSkill.HARVESTING)
                ));
        this.inventory.setItem(skillSlots[4], GUIUtil.dispItem
                (
                        Material.IRON_PICKAXE,
                        ChatColor.YELLOW + "Mining",
                        gatheringSkillDescription(gatherPlayer, GatheringSkill.MINING),
                        3
                ));
        this.inventory.setItem(skillSlots[5], GUIUtil.dispItem
                (
                        Material.IRON_AXE,
                        ChatColor.YELLOW + "Woodcutting",
                        gatheringSkillDescription(gatherPlayer, GatheringSkill.WOODCUTTING),
                        3
                ));
    }
}