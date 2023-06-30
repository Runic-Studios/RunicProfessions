package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.WeightedRandomBag;
import com.runicrealms.plugin.professions.gathering.FishingSession;
import com.runicrealms.plugin.professions.gathering.GatheringRegion;
import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringTool;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.professions.utilities.GatheringUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Listener for Fishing (Gathering Profession)
 * randomizes which fish they receive
 * Checks name of WG region for "pond" to perform tasks
 */
public class FishingListener implements Listener {

    /**
     * @param hotspotResources a string array of templateIDs for fish
     * @param fishingLevel     of the player
     * @return the selected fish from the drop table
     */
    private GatheringResource determineFish(String[] hotspotResources, int fishingLevel) {
        WeightedRandomBag<GatheringResource> oreDropTable = new WeightedRandomBag<>();
        oreDropTable.addEntry(GatheringResource.SALMON, 400);
        oreDropTable.addEntry(GatheringResource.COD, 400);
        for (String string : hotspotResources) {
            GatheringResource gatheringResource = GatheringResource.getFromTemplateId(string);
            if (gatheringResource == null) continue;
            int reqLevel = gatheringResource.getRequiredLevel();
            if (fishingLevel < reqLevel) continue;
            int weight = 100 + (10 * fishingLevel);
            oreDropTable.addEntry(gatheringResource, weight);
        }
        return oreDropTable.getRandom();
    }

    /**
     * Determine the fish to give the player based on the region they are fishing in
     *
     * @param regionIds    the list of region ids the fishhook is standing in
     * @param fishingLevel of the player
     * @return the appropriate fish to gather
     */
    private GatheringResource determineFishFromRegion(List<String> regionIds, int fishingLevel) {
        try {
            Optional<String> fishingRegion = regionIds.stream().filter(region -> region.contains("pond")).findFirst();
            if (fishingRegion.isPresent()) {
                String fishingRegionName = fishingRegion.get();
                String[] availableFish = fishingRegionName.split("/");
                return determineFish(availableFish, fishingLevel);
            }
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Error: There was an error getting fish from roll!");
        }
        return GatheringResource.COD;
    }

    /**
     * Handle logic for fishing
     */
    @EventHandler
    public void onFishCatch(PlayerFishEvent event) {
        // Set the time required to catch the fish
        FishHook fishHook = event.getHook();
        fishHook.setMinWaitTime(0); // in ticks (0s)
        fishHook.setMaxWaitTime(20); // in ticks (1s)

        // Disable default exp
        event.setExpToDrop(0);
        event.setCancelled(false);
        // Prevent hooking mobs
        if (event.getCaught() instanceof LivingEntity) {
            event.getHook().remove();
            return;
        }

        if (event.getCaught() != null) event.getCaught().remove();
        if (event.getState() != PlayerFishEvent.State.BITE) return;
        Player player = event.getPlayer();
        double chance = ThreadLocalRandom.current().nextDouble();
        Location hookLoc = event.getHook().getLocation();

        // ensure the player has reached the req level to obtain the fish
        GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(player.getUniqueId());
        int fishingLevel = gatheringData.getFishingLevel();
        GatheringResource gatheringResource = determineFishFromRegion(RunicCore.getRegionAPI().getRegionIds(hookLoc), fishingLevel);
        int requiredLevel = gatheringResource.getRequiredLevel();
        if (fishingLevel < requiredLevel) {
            player.sendMessage(ChatColor.RED + "You are not skilled enough to catch this fish!");
            return;
        }

        String templateId = gatheringResource.getTemplateId();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        // verify the player is holding a tool
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(gatheringResource.getGatheringSkill().getNoToolMessage());
            return;
        }

        // verify held tool is a fishing rod
        RunicItem runicItem = RunicItemsAPI.getRunicItemFromItemStack(heldItem);
        String templateIdHeldItem = runicItem.getTemplateId();
        Optional<GatheringTool> gatheringTool = GatheringUtil.getRods().stream().filter
                (
                        tool -> tool.getRunicItemDynamic().getTemplateId().equals(templateIdHeldItem)
                ).findFirst();
        if (!gatheringTool.isPresent()) {
            player.sendMessage(gatheringResource.getGatheringSkill().getNoToolMessage());
            return;
        }

        event.getHook().remove();

        FishingSession fishingSession = new FishingSession(player, hookLoc);
        fishingSession.startSession();

//        GatheringEvent gatheringEvent = new GatheringEvent
//                (
//                        player,
//                        gatheringResource,
//                        gatheringTool.get(),
//                        heldItem,
//                        templateId,
//                        hookLoc,
//                        null,
//                        gatheringResource.getResourceBlockType(),
//                        chance,
//                        gatheringResource.getResourceBlockType()
//                );
//        Bukkit.getPluginManager().callEvent(gatheringEvent);
    }


    /**
     * Prevent fish from spawning naturally.
     * We'll spawn them in ponds as NPCs, but not out in the ocean.
     */
    @EventHandler
    public void onFishSpawn(CreatureSpawnEvent event) {
        Entity spawned = event.getEntity();
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (spawned instanceof Fish) event.setCancelled(true);
    }

    /**
     * Prevents players from fishing outside of ponds
     */
    @EventHandler
    public void onRodUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        Material offHand = player.getInventory().getItemInOffHand().getType();
        if (mainHand != Material.FISHING_ROD && offHand != Material.FISHING_ROD) return;
        boolean canFish = RunicProfessions.getAPI().isInGatheringRegion(GatheringRegion.POND, player.getLocation());
        if (!canFish) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't fish here.");
            return;
        }
        if (FishingSession.getFishers().containsKey(event.getPlayer().getUniqueId())) {
            FishingSession fishingSession = FishingSession.getFishers().get(event.getPlayer().getUniqueId());
            fishingSession.tightenLine();
        }
    }
}
