package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.professions.gathering.GatheringTool;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItemDynamic;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class GatheringUtil {

    /*
    Axes
     */
    public static final RunicItemDynamic GATHERING_AXE_TUTORIAL = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-axe-tutorial");
    public static final ItemStack GATHERING_AXE_TUTORIAL_ITEMSTACK = GATHERING_AXE_TUTORIAL.generateItem();
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
    public static final RunicItemDynamic GATHERING_HOE_ADEPT = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-hoe-adept");
    public static final RunicItemDynamic GATHERING_HOE_REFINED = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-hoe-refined");
    public static final RunicItemDynamic GATHERING_HOE_MASTER = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-hoe-master");
    public static final RunicItemDynamic GATHERING_HOE_ARTISAN = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-hoe-artisan");

    /*
    Pickaxes
     */
    public static final RunicItemDynamic GATHERING_PICKAXE_APPRENTICE = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-apprentice");
    public static final RunicItemDynamic GATHERING_PICKAXE_ADEPT = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-adept");
    public static final RunicItemDynamic GATHERING_PICKAXE_REFINED = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-refined");
    public static final RunicItemDynamic GATHERING_PICKAXE_MASTER = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-master");
    public static final RunicItemDynamic GATHERING_PICKAXE_ARTISAN = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-pickaxe-artisan");

    /*
    Rods
     */
    public static final RunicItemDynamic GATHERING_ROD_APPRENTICE = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-apprentice");
    public static final RunicItemDynamic GATHERING_ROD_ADEPT = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-adept");
    public static final RunicItemDynamic GATHERING_ROD_REFINED = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-refined");
    public static final RunicItemDynamic GATHERING_ROD_MASTER = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-master");
    public static final RunicItemDynamic GATHERING_ROD_ARTISAN = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-artisan");

    /**
     * Grabs a set of gathering axes to ensure player is holding the right item
     *
     * @return a set of pre-defined gathering axes
     */
    public static Set<GatheringTool> getAxes() {
        Set<GatheringTool> woodcuttingAxes = new HashSet<>();
        woodcuttingAxes.add(GatheringTool.GATHERING_AXE_TUTORIAL);
        woodcuttingAxes.add(GatheringTool.GATHERING_AXE_APPRENTICE);
        woodcuttingAxes.add(GatheringTool.GATHERING_AXE_ADEPT);
        woodcuttingAxes.add(GatheringTool.GATHERING_AXE_REFINED);
        woodcuttingAxes.add(GatheringTool.GATHERING_AXE_MASTER);
        woodcuttingAxes.add(GatheringTool.GATHERING_AXE_ARTISAN);
        return woodcuttingAxes;
    }

    /**
     * Grabs a set of gathering hoes to ensure player is holding the right item
     *
     * @return a set of pre-defined gathering hoes
     */
    public static Set<GatheringTool> getHoes() {
        Set<GatheringTool> farmingHoes = new HashSet<>();
        farmingHoes.add(GatheringTool.GATHERING_HOE_APPRENTICE);
        farmingHoes.add(GatheringTool.GATHERING_HOE_ADEPT);
        farmingHoes.add(GatheringTool.GATHERING_HOE_REFINED);
        farmingHoes.add(GatheringTool.GATHERING_HOE_MASTER);
        farmingHoes.add(GatheringTool.GATHERING_HOE_ARTISAN);
        return farmingHoes;
    }

    /**
     * Grabs a set of gathering pickaxes to ensure player is holding the right item
     *
     * @return a set of pre-defined gathering pickaxes
     */
    public static Set<GatheringTool> getPickaxes() {
        Set<GatheringTool> miningPickaxes = new HashSet<>();
        miningPickaxes.add(GatheringTool.GATHERING_PICKAXE_APPRENTICE);
        miningPickaxes.add(GatheringTool.GATHERING_PICKAXE_ADEPT);
        miningPickaxes.add(GatheringTool.GATHERING_PICKAXE_REFINED);
        miningPickaxes.add(GatheringTool.GATHERING_PICKAXE_MASTER);
        miningPickaxes.add(GatheringTool.GATHERING_PICKAXE_ARTISAN);
        return miningPickaxes;
    }

    /**
     * Grabs a set of gathering rods to ensure player is holding the right item
     *
     * @return a set of pre-defined gathering rods
     */
    public static Set<GatheringTool> getRods() {
        Set<GatheringTool> fishingRods = new HashSet<>();
        fishingRods.add(GatheringTool.GATHERING_ROD_APPRENTICE);
        fishingRods.add(GatheringTool.GATHERING_ROD_ADEPT);
        fishingRods.add(GatheringTool.GATHERING_ROD_REFINED);
        fishingRods.add(GatheringTool.GATHERING_ROD_MASTER);
        fishingRods.add(GatheringTool.GATHERING_ROD_ARTISAN);
        return fishingRods;
    }
}
