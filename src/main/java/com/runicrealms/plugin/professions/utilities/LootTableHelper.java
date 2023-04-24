package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.WeightedRandomBag;
import com.runicrealms.plugin.item.lootchests.ChestItem;
import org.bukkit.Bukkit;

/**
 * Adds profession-related items to various loot/drop tables on startup
 */
public class LootTableHelper {

    private void setupCommonLootTable() {
        WeightedRandomBag<ChestItem> lootTableTierI = RunicCore.getLootTableAPI().getLootTableTierI();

        ChestItem gatheringAxe = new ChestItem(GatheringUtil.GATHERING_AXE_APPRENTICE.getTemplateId(), 1, 1);
        ChestItem gatheringHoe = new ChestItem(GatheringUtil.GATHERING_HOE_APPRENTICE.getTemplateId(), 1, 1);
        ChestItem gatheringPick = new ChestItem(GatheringUtil.GATHERING_PICKAXE_APPRENTICE.getTemplateId(), 1, 1);
        ChestItem gatheringRod = new ChestItem(GatheringUtil.GATHERING_ROD_APPRENTICE.getTemplateId(), 1, 1);

        lootTableTierI.addEntry(gatheringAxe, 4.0);
        lootTableTierI.addEntry(gatheringHoe, 4.0);
        lootTableTierI.addEntry(gatheringPick, 4.0);
        lootTableTierI.addEntry(gatheringRod, 6.0);
    }

    private void setupEpicLootTable() {
        WeightedRandomBag<ChestItem> lootTableTierIV = RunicCore.getLootTableAPI().getLootTableTierIV();

        ChestItem gatheringAxe = new ChestItem(GatheringUtil.GATHERING_AXE_MASTER.getTemplateId(), 1, 1);
        ChestItem gatheringHoe = new ChestItem(GatheringUtil.GATHERING_HOE_MASTER.getTemplateId(), 1, 1);
        ChestItem gatheringPick = new ChestItem(GatheringUtil.GATHERING_PICKAXE_MASTER.getTemplateId(), 1, 1);
        ChestItem gatheringRod = new ChestItem(GatheringUtil.GATHERING_ROD_MASTER.getTemplateId(), 1, 1);

        lootTableTierIV.addEntry(gatheringAxe, 2.0);
        lootTableTierIV.addEntry(gatheringHoe, 2.0);
        lootTableTierIV.addEntry(gatheringPick, 2.0);
        lootTableTierIV.addEntry(gatheringRod, 4.0);
    }

    /**
     * Load profession items into loot chests on a delay to ensure items have loaded
     */
    public void setupLootTables() {
        Bukkit.getScheduler().runTaskLater(RunicProfessions.getInstance(), () -> {
            setupCommonLootTable();
            setupUncommonLootTable();
            setupRareLootTable();
            setupEpicLootTable();
        }, 10 * 20L);
    }

    private void setupRareLootTable() {
        WeightedRandomBag<ChestItem> lootTableTierIII = RunicCore.getLootTableAPI().getLootTableTierIII();

        ChestItem gatheringAxe = new ChestItem(GatheringUtil.GATHERING_AXE_REFINED.getTemplateId(), 1, 1);
        ChestItem gatheringHoe = new ChestItem(GatheringUtil.GATHERING_HOE_REFINED.getTemplateId(), 1, 1);
        ChestItem gatheringPick = new ChestItem(GatheringUtil.GATHERING_PICKAXE_REFINED.getTemplateId(), 1, 1);
        ChestItem gatheringRod = new ChestItem(GatheringUtil.GATHERING_ROD_REFINED.getTemplateId(), 1, 1);

        lootTableTierIII.addEntry(gatheringAxe, 2.0);
        lootTableTierIII.addEntry(gatheringHoe, 2.0);
        lootTableTierIII.addEntry(gatheringPick, 2.0);
        lootTableTierIII.addEntry(gatheringRod, 4.0);
    }

    private void setupUncommonLootTable() {
        WeightedRandomBag<ChestItem> lootTableTierII = RunicCore.getLootTableAPI().getLootTableTierII();

        ChestItem gatheringAxe = new ChestItem(GatheringUtil.GATHERING_AXE_ADEPT.getTemplateId(), 1, 1);
        ChestItem gatheringHoe = new ChestItem(GatheringUtil.GATHERING_HOE_ADEPT.getTemplateId(), 1, 1);
        ChestItem gatheringPick = new ChestItem(GatheringUtil.GATHERING_PICKAXE_ADEPT.getTemplateId(), 1, 1);
        ChestItem gatheringRod = new ChestItem(GatheringUtil.GATHERING_ROD_ADEPT.getTemplateId(), 1, 1);

        lootTableTierII.addEntry(gatheringAxe, 3.0);
        lootTableTierII.addEntry(gatheringHoe, 3.0);
        lootTableTierII.addEntry(gatheringPick, 3.0);
        lootTableTierII.addEntry(gatheringRod, 5.0);
    }
}
