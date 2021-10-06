package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.professions.gathering.GatheringTool;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItem;
import com.runicrealms.runicitems.item.RunicItemDynamic;
import net.minecraft.server.v1_16_R3.EntityFishingHook;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.FishHook;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
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
    public static final RunicItemDynamic GATHERING_ROD_APPRENTICE = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-apprentice");
    public static final ItemStack GATHERING_ROD_APPRENTICE_ITEMSTACK = GATHERING_ROD_APPRENTICE.generateItem();
    public static final RunicItemDynamic GATHERING_ROD_ADEPT = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-adept");
    public static final ItemStack GATHERING_ROD_ADEPT_ITEMSTACK = GATHERING_ROD_ADEPT.generateItem();
    public static final RunicItemDynamic GATHERING_ROD_REFINED = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-refined");
    public static final ItemStack GATHERING_ROD_REFINED_ITEMSTACK = GATHERING_ROD_REFINED.generateItem();
    public static final RunicItemDynamic GATHERING_ROD_MASTER = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-master");
    public static final ItemStack GATHERING_ROD_MASTER_ITEMSTACK = GATHERING_ROD_MASTER.generateItem();
    public static final RunicItemDynamic GATHERING_ROD_ARTISAN = (RunicItemDynamic) RunicItemsAPI.generateItemFromTemplate("gathering-rod-artisan");
    public static final ItemStack GATHERING_ROD_ARTISAN_ITEMSTACK = GATHERING_ROD_ARTISAN.generateItem();

    /**
     * Grabs a set of gathering axes to ensure player is holding the right item
     *
     * @return a set of pre-defined gathering axes
     */
    public static Set<GatheringTool> getAxes() {
        Set<GatheringTool> woodcuttingAxes = new HashSet<>();
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

    /**
     * A bit of nms magic to modify the rate of fishing on the server
     *
     * @param hook the player's fishing hook
     * @param time (in seconds) before a fish bites
     */
    public static void setBiteTime(FishHook hook, int time) {
        net.minecraft.server.v1_16_R3.EntityFishingHook hookCopy = (EntityFishingHook) ((CraftEntity) hook).getHandle();

        Field fishCatchTime = null;

        try {
            fishCatchTime = net.minecraft.server.v1_16_R3.EntityFishingHook.class.getDeclaredField("ah");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(fishCatchTime).setAccessible(true);

        try {
            fishCatchTime.setInt(hookCopy, time);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function for RunicItemDynamic
     *
     * @param runicItem the runic item to read
     * @param key       the key of the data field
     * @return an Integer value matching key
     */
    public static int getRunicItemDataFieldInt(RunicItem runicItem, String key) {
        return Integer.parseInt(runicItem.getData().get(key));
    }
}
