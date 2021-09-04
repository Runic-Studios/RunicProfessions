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

import java.util.HashSet;
import java.util.Set;

public class GatheringUtil {

    /*
    Axes
     */
    public static final RunicItemDynamic GATHERING_AXE_APPRENTICE = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-apprentice");
    public static final ItemStack GATHERING_AXE_APPRENTICE_ITEMSTACK = GATHERING_AXE_APPRENTICE.generateItem();
    public static final RunicItemDynamic GATHERING_AXE_ADEPT = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-adept");
    public static final ItemStack GATHERING_AXE_ADEPT_ITEMSTACK = GATHERING_AXE_ADEPT.generateItem();
    public static final RunicItemDynamic GATHERING_AXE_REFINED = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-refined");
    public static final ItemStack GATHERING_AXE_REFINED_ITEMSTACK = GATHERING_AXE_REFINED.generateItem();
    public static final RunicItemDynamic GATHERING_AXE_MASTER = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-master");
    public static final ItemStack GATHERING_AXE_MASTER_ITEMSTACK = GATHERING_AXE_MASTER.generateItem();
    public static final RunicItemDynamic GATHERING_AXE_ARTISAN = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-artisan");
    public static final ItemStack GATHERING_AXE_ARTISAN_ITEMSTACK = GATHERING_AXE_ARTISAN.generateItem();

    /*
    Hoes
     */
    public static final RunicItemDynamic GATHERING_HOE_APPRENTICE = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-hoe-apprentice");
    public static final ItemStack GATHERING_HOE_APPRENTICE_ITEMSTACK = GATHERING_HOE_APPRENTICE.generateItem();
    public static final RunicItemDynamic GATHERING_HOE_ADEPT = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-hoe-adept");
    public static final ItemStack GATHERING_HOE_ADEPT_ITEMSTACK = GATHERING_HOE_ADEPT.generateItem();
    public static final RunicItemDynamic GATHERING_HOE_REFINED = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-hoe-refined");
    public static final ItemStack GATHERING_HOE_REFINED_ITEMSTACK = GATHERING_HOE_REFINED.generateItem();
    public static final RunicItemDynamic GATHERING_HOE_MASTER = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-hoe-master");
    public static final ItemStack GATHERING_HOE_MASTER_ITEMSTACK = GATHERING_HOE_MASTER.generateItem();
    public static final RunicItemDynamic GATHERING_HOE_ARTISAN = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-hoe-artisan");
    public static final ItemStack GATHERING_HOE_ARTISAN_ITEMSTACK = GATHERING_HOE_ARTISAN.generateItem();

    /*
    Pickaxes
     */
    public static final RunicItemDynamic GATHERING_PICKAXE_APPRENTICE = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-apprentice");
    public static final ItemStack GATHERING_PICKAXE_APPRENTICE_ITEMSTACK = GATHERING_PICKAXE_APPRENTICE.generateItem();
    public static final RunicItemDynamic GATHERING_PICKAXE_ADEPT = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-adept");
    public static final ItemStack GATHERING_PICKAXE_ADEPT_ITEMSTACK = GATHERING_PICKAXE_ADEPT.generateItem();
    public static final RunicItemDynamic GATHERING_PICKAXE_REFINED = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-refined");
    public static final ItemStack GATHERING_PICKAXE_REFINED_ITEMSTACK = GATHERING_PICKAXE_REFINED.generateItem();
    public static final RunicItemDynamic GATHERING_PICKAXE_MASTER = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-master");
    public static final ItemStack GATHERING_PICKAXE_MASTER_ITEMSTACK = GATHERING_PICKAXE_MASTER.generateItem();
    public static final RunicItemDynamic GATHERING_PICKAXE_ARTISAN = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-artisan");
    public static final ItemStack GATHERING_PICKAXE_ARTISAN_ITEMSTACK = GATHERING_PICKAXE_ARTISAN.generateItem();

    /*
    Rods
     */
//    public static final RunicItemDynamic GATHERING_ROD_APPRENTICE = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-apprentice");
//    public static final ItemStack GATHERING_ROD_APPRENTICE_ITEMSTACK = GATHERING_ROD_APPRENTICE.generateItem();
//    public static final RunicItemDynamic GATHERING_ROD_ADEPT = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-adept");
//    public static final ItemStack GATHERING_ROD_ADEPT_ITEMSTACK = GATHERING_ROD_ADEPT.generateItem();
//    public static final RunicItemDynamic GATHERING_ROD_REFINED = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-refined");
//    public static final ItemStack GATHERING_ROD_REFINED_ITEMSTACK = GATHERING_ROD_REFINED.generateItem();
//    public static final RunicItemDynamic GATHERING_ROD_MASTER = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-master");
//    public static final ItemStack GATHERING_ROD_MASTER_ITEMSTACK = GATHERING_ROD_MASTER.generateItem();
//    public static final RunicItemDynamic GATHERING_ROD_ARTISAN = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-artisan");
//    public static final ItemStack GATHERING_ROD_ARTISAN_ITEMSTACK = GATHERING_ROD_ARTISAN.generateItem();

    /**
     * General function handle gathering.
     *
     * @param player              who gathered material
     * @param gatheringTool       the tool used
     * @param templateId          the templateId of the gathered material (iron-ore)
     * @param location            the location of the block to replace
     * @param block               the block itself to replace
     * @param placeholderMaterial the material to set while the block is regenerating (cobblestone)
     * @param hologramItemName    the hologram to display upon successful gathering
     * @param chance              the chance to gather the material
     */
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

        // todo: handle overflow
        RunicItemsAPI.addItem(player.getInventory(), RunicItemsAPI.generateItemFromTemplate(templateId).generateItem());

        // give the player a coin
        if (chance >= (.95)) {
            block.getWorld().playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);
            HologramUtil.createStaticHologram(player, location, ChatColor.GOLD + "" + ChatColor.BOLD + "+ Coin", 0, 1.25, 0);
            // todo: handle overflow
            RunicItemsAPI.addItem(player.getInventory(), CurrencyUtil.goldCoin());
        }
    }

    /**
     * Reduces the durability of a RunicItemDynamic after gathering a material
     *
     * @param player        who gathered material
     * @param gatheringTool the item to reduce the durability of
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
     * Grabs a set of gathering axes to ensure player is holding the right item
     *
     * @return a set of pre-defined gathering axes
     */
    public static Set<RunicItemDynamic> getAxes() {
        Set<RunicItemDynamic> woodcuttingAxes = new HashSet<>();
        woodcuttingAxes.add(GATHERING_AXE_APPRENTICE);
        woodcuttingAxes.add(GATHERING_AXE_ADEPT);
        woodcuttingAxes.add(GATHERING_AXE_REFINED);
        woodcuttingAxes.add(GATHERING_AXE_MASTER);
        woodcuttingAxes.add(GATHERING_AXE_ARTISAN);
        return woodcuttingAxes;
    }

    /**
     * Grabs a set of gathering hoes to ensure player is holding the right item
     *
     * @return a set of pre-defined gathering hoes
     */
    public static Set<RunicItemDynamic> getHoes() {
        Set<RunicItemDynamic> farmingHoes = new HashSet<>();
        farmingHoes.add(GATHERING_HOE_APPRENTICE);
        farmingHoes.add(GATHERING_HOE_ADEPT);
        farmingHoes.add(GATHERING_HOE_REFINED);
        farmingHoes.add(GATHERING_HOE_MASTER);
        farmingHoes.add(GATHERING_HOE_ARTISAN);
        return farmingHoes;
    }

    /**
     * Grabs a set of gathering pickaxes to ensure player is holding the right item
     *
     * @return a set of pre-defined gathering pickaxes
     */
    public static Set<RunicItemDynamic> getPickaxes() {
        Set<RunicItemDynamic> miningPickaxes = new HashSet<>();
        miningPickaxes.add(GATHERING_PICKAXE_APPRENTICE);
        miningPickaxes.add(GATHERING_PICKAXE_ADEPT);
        miningPickaxes.add(GATHERING_PICKAXE_REFINED);
        miningPickaxes.add(GATHERING_PICKAXE_MASTER);
        miningPickaxes.add(GATHERING_PICKAXE_ARTISAN);
        return miningPickaxes;
    }

    /**
     * Grabs a set of gathering rods to ensure player is holding the right item
     *
     * @return a set of pre-defined gathering rods
     */
//    public static Set<RunicItemDynamic> getRods() {
//        Set<RunicItemDynamic> fishingRods = new HashSet<>();
//        fishingRods.add(GATHERING_ROD_APPRENTICE);
//        fishingRods.add(GATHERING_ROD_ADEPT);
//        fishingRods.add(GATHERING_ROD_REFINED);
//        fishingRods.add(GATHERING_ROD_MASTER);
//        fishingRods.add(GATHERING_ROD_ARTISAN);
//        return fishingRods;
//    }

    /**
     * A handy class that bundles some key components used in gathering
     */
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
