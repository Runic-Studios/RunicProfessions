package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.utilities.ActionBarUtil;
import com.runicrealms.plugin.utilities.CurrencyUtil;
import com.runicrealms.plugin.utilities.HologramUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItemDynamic;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GatheringUtil {

    public static final RunicItemDynamic GATHERING_AXE_APPRENTICE = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-apprentice");
    // todo: update with runic items
    public static final ItemStack GATHERING_AXE_APPRENTICE_ITEMSTACK = GATHERING_AXE_APPRENTICE.generateItem();
//    public static final RunicItemDynamic GATHERING_AXE_ADEPT = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-adept");
//    public static final RunicItemDynamic GATHERING_AXE_REFINED = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-refined");
//    public static final RunicItemDynamic GATHERING_AXE_MASTER = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-master");
//    public static final RunicItemDynamic GATHERING_AXE_ARTISAN = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-artisan");

    public static void gatherMaterial(Player player, RunicItemDynamic gatheringTool, String templateId, Location location,
                                      Block block, Material placeholderMaterial, String hologramItemName, double chance) {

        block.setType(placeholderMaterial);

        double successRate = Double.parseDouble(gatheringTool.getData().get("rate"));

        if (chance < (1 - successRate)) {
            ActionBarUtil.sendTimedMessage(player, "&cYou fail to gather any resources.", 3);
            return;
        }

        if (location.clone().add(0, 1.5, 0).getBlock().getType() == Material.AIR) {
            HologramUtil.createStaticHologram(player, location, ChatColor.GREEN + "" + ChatColor.BOLD + hologramItemName, 0, 2, 0);
        }

        HashMap<Integer, ItemStack> gatheredItemsToGive = player.getInventory().addItem(RunicItemsAPI.generateItemFromTemplate(templateId).generateItem());
        for (ItemStack is : gatheredItemsToGive.values()) {
            player.getWorld().dropItem(player.getLocation(), is);
        }

        // give the player a coin
        if (chance >= (95)) {
            block.getWorld().playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);
            HologramUtil.createStaticHologram(player, location, ChatColor.GOLD + "" + ChatColor.BOLD + "+ Coin", 0, 1.25, 0);
            HashMap<Integer, ItemStack> coinToGive = player.getInventory().addItem(CurrencyUtil.goldCoin());
            for (ItemStack is : coinToGive.values()) {
                player.getWorld().dropItem(player.getLocation(), is);
            }
        }
    }

    /**
     * @param player
     * @param gatheringTool
     */
    public static void reduceGatheringToolDurability(Player player, RunicItemDynamic gatheringTool) {
        int durability = gatheringTool.getDynamicField();
        int newDurability = durability - 1;
        gatheringTool.setDynamicField(newDurability);
        ItemStack newGatheringTool = gatheringTool.generateItem();
        if (newDurability <= 0) {
            player.getInventory().setItemInMainHand(null);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
            player.sendMessage(ChatColor.RED + "Your gathering tool broke!");
        } else {
            player.getInventory().setItemInMainHand(newGatheringTool);
        }
    }

    /**
     * @return
     */
    public static Set<RunicItemDynamic> getWoodcuttingAxes() {
        Set<RunicItemDynamic> woodcuttingAxes = new HashSet<>();
        woodcuttingAxes.add(GATHERING_AXE_APPRENTICE);
        return woodcuttingAxes;
    }

    public static class GatheringReagentWrapper {

        private final String templateId;
        private final Material blockPlaceholderType;
        private final String hologramDisplayString;

        public GatheringReagentWrapper(String templateId, Material blockPlaceholderType, String hologramDisplayString) {
            this.templateId = templateId;
            this.blockPlaceholderType = blockPlaceholderType;
            this.hologramDisplayString = hologramDisplayString;
        }

        public String getTemplateId() {
            return templateId;
        }

        public Material getBlockPlaceholderType() {
            return blockPlaceholderType;
        }

        public String getHologramDisplayString() {
            return hologramDisplayString;
        }
    }
}
