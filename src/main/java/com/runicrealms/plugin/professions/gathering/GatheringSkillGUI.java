package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.utilities.ColorUtil;
import com.runicrealms.plugin.utilities.GUIUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

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

//    private static String[] gatheringSkillDescription(GatherPlayer gatherPlayer, GatheringSkill gatheringSkill) {
//        String[] unlockMessageArray = ProfExpUtil.nextReagentUnlockMessage(gatheringSkill,
//                gatherPlayer.getGatheringLevel(gatheringSkill), true).toArray(new String[0]);
//        String[] descriptionArray = new String[]{
//                buildProgressBar(gatherPlayer, gatheringSkill),
//                "",
//                ChatColor.GRAY + "Level: " + ChatColor.WHITE + gatherPlayer.getGatheringLevel(gatheringSkill),
//                ChatColor.GRAY + "Exp: " + ChatColor.WHITE + gatherPlayer.getGatheringExp(gatheringSkill),
//                "",
//        };
//        return (String[]) ArrayUtils.addAll(descriptionArray, unlockMessageArray); // append formatted unlock message
//    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public GatheringSkill getGatheringSkill() {
        return gatheringSkill;
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
                }
        ));
        int i = 9;
        for (GatheringResource gatheringResource : GatheringResource.values()) {
            if (gatheringResource.getGatheringSkill() != this.gatheringSkill) continue;
            this.inventory.setItem(i, RunicItemsAPI.generateItemFromTemplate(gatheringResource.getTemplateId()).generateItem());
            i++;
        }
    }
}