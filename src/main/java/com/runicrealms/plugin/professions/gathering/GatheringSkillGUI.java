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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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

    private static ItemStack reagentWithLore(ItemStack itemStack, GatheringResource gatheringResource, boolean isUnlocked) {
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
                                ChatColor.YELLOW + gatheringResource.getGatheringSkill().getFormattedIdentifier() + " " +
                                ChatColor.WHITE + gatheringResource.getRequiredLevel());
        lore.add("");
        lore.add
                (
                        ChatColor.GRAY + "" + ChatColor.ITALIC + "Rewards " +
                                ChatColor.WHITE + ChatColor.ITALIC + gatheringResource.getExperience() +
                                ChatColor.GRAY + ChatColor.ITALIC + " experience");
        meta.setLore(lore);
        reagentWithLore.setItemMeta(meta);
        return reagentWithLore;
    }

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
        GUIUtil.fillInventoryBorders(this.inventory);
        this.inventory.setItem(0, GUIUtil.backButton());
        this.inventory.setItem(4, GUIUtil.dispItem(
                Material.PAPER,
                ChatColor.YELLOW + gatheringSkill.getFormattedIdentifier() +
                        ChatColor.GRAY + " Level " + gatherPlayer.getGatheringLevel(gatheringSkill),
                new String[]{}
        ));
        GatheringResource[] sorted = GatheringResource.values();
        Arrays.sort(sorted, Comparator.comparing(GatheringResource::getRequiredLevel)); // sort in ascending order of level
        for (GatheringResource gatheringResource : sorted) {
            if (gatheringResource.getGatheringSkill() != this.gatheringSkill) continue;
            ItemStack itemStack = RunicItemsAPI.generateItemFromTemplate(gatheringResource.getTemplateId()).generateItem();
            this.inventory.setItem(this.inventory.firstEmpty(), reagentWithLore
                    (
                            itemStack,
                            gatheringResource,
                            (gatherPlayer.getGatheringLevel(gatheringResource.getGatheringSkill()) >= gatheringResource.getRequiredLevel())
                    ));
        }
    }
}