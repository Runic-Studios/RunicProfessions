package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.utilities.ColorUtil;
import com.runicrealms.plugin.utilities.GUIUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class GatheringGUI implements InventoryHolder {

    private static final int[] skillSlots = new int[]{21, 22, 23, 30, 31, 32};
    private final Inventory inventory;
    private final Player player;

    public GatheringGUI(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, ColorUtil.format("&eGathering Skills"));
        this.player = player;
        openMenu();
    }

    private static String[] gatheringSkillDescription(GatherPlayer gatherPlayer, GatheringSkill gatheringSkill) {
        return new String[]{
                // todo: exp bar here
                "",
                ChatColor.GRAY + "Level: " + ChatColor.WHITE + gatherPlayer.getGatheringLevel(gatheringSkill),
                ChatColor.GRAY + "Exp: " + ChatColor.WHITE + gatherPlayer.getGatheringExp(gatheringSkill),
                ""
        };
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
        // todo: set position to 4 as an info item
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