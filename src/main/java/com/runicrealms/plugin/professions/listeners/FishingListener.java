package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.professions.gathering.GatheringRegion;
import com.runicrealms.plugin.professions.utilities.GatheringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Listener for Fishing (Gathering Profession)
 * randomizes which fish they receive
 * Checks name of WG region for "pond" to perform tasks
 */
@SuppressWarnings("FieldCanBeLocal")
public class FishingListener implements Listener {

    @EventHandler
    public void onFishCatch(PlayerFishEvent e) {
        // disable exp
        e.setExpToDrop(0);
        e.setCancelled(false);
        if (e.getCaught() != null) e.getCaught().remove();
        if (e.getState() != PlayerFishEvent.State.BITE) return;
        Player player = e.getPlayer();

        // roll to see if player successfully fished
        // roll to see what kind of fish they will receive
        double chance = ThreadLocalRandom.current().nextDouble();
        double fishType = ThreadLocalRandom.current().nextDouble();
        Location hookLoc = e.getHook().getLocation();
        Vector fishPath = player.getLocation().toVector().subtract
                (hookLoc.clone().add(0, 1, 0).toVector()).normalize();

        // ensure the proper type of block is being mined
//        GatheringResource gatheringResource = GatheringResource.getFromResourceBlockType(fishType);
//        String templateId = gatheringResource.getTemplateId();
//        ItemStack fish = RunicItemsAPI.generateItemFromTemplate(gatheringResource.getTemplateId()).generateItem();
//        String holoString = gatheringResource.getHologramDisplayString();
//        ItemStack heldItem = player.getInventory().getItemInMainHand();
//
//        // verify the player is holding a tool
//        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
//            player.sendMessage(ChatColor.RED + "You need a fishing rod to do that!");
//            return;
//        }
//
//        // verify held tool is a fishing rod
//        RunicItem runicItem = RunicItemsAPI.getRunicItemFromItemStack(heldItem);
//        String templateIdHeldItem = runicItem.getTemplateId();
//        Optional<GatheringTool> gatheringTool = GatheringUtil.getRods().stream().filter(tool -> tool.getRunicItemDynamic().getTemplateId().equals(templateIdHeldItem)).findFirst();
//        if (!gatheringTool.isPresent()) {
//            player.sendMessage(ChatColor.RED + "You need a fishing rod to do that!");
//            return;
//        }
//
//        GatheringEvent gatheringEvent = new GatheringEvent(player, gatheringResource, gatheringTool.get(), templateId, hookLoc, hookLoc.clone().add(0, 1.5, 0), fish.getType(), holoString, chance, fishPath);
//        Bukkit.getPluginManager().callEvent(gatheringEvent);
    }

    /**
     * Prevents players from fishing outside of ponds
     */
    @EventHandler
    public void onRodUse(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = e.getPlayer();
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        Material offHand = player.getInventory().getItemInOffHand().getType();
        if (mainHand != Material.FISHING_ROD && offHand != Material.FISHING_ROD) return;
        boolean canFish = RunicProfessionsAPI.isInGatheringRegion(GatheringRegion.POND, player.getLocation());
        if (!canFish) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't fish here.");
        }
    }

    /**
     * Prevent fish from spawning naturally.
     * We'll spawn them in ponds as NPCs, but not out in the ocean.
     */
    @EventHandler
    public void onFishSpawn(CreatureSpawnEvent e) {
        Entity spawned = e.getEntity();
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (spawned instanceof Fish) e.setCancelled(true);
    }

    /**
     * Prevents a player from consuming raw fish
     */
    @EventHandler
    public void onRawFishEat(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.COD || e.getItem().getType() == Material.SALMON) {
            e.getPlayer().sendMessage(ChatColor.RED + "I need to cook that first.");
            e.setCancelled(true);
        }
    }

    /**
     * Prevents a player from consuming puffer / tropical
     */
    @EventHandler
    public void onPufferOrTropicalFishEat(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        Material m = e.getItem().getType();
        if (m == Material.PUFFERFISH || m == Material.TROPICAL_FISH) {
            e.getPlayer().sendMessage(ChatColor.RED + "I shouldn't eat that.");
            e.setCancelled(true);
        }
    }

    /**
     * Reduces fishing time using NMS
     * Time for a 'bite' will be between 5-25 seconds
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        FishHook plHook = e.getHook();
        Random rand = new Random();
        int time = rand.nextInt(25 - 5) + 5;
        GatheringUtil.setBiteTime(plHook, time);
    }

//    /**
//     * Builds out a handy wrapper for matching the runic item template id and changing the hologram display
//     *
//     * @param fishType a double representing a random roll that corresponds to fish type
//     * @return a wrapper with a material and a string
//     */
//    private GatheringUtil.GatheringReagentWrapper buildGatheringReagentWrapper(double fishType) {
//        String templateId;
//        Material placeHolderType = Material.AIR;
//        String holoString;
//        if (fishType < .5) {
//            templateId = "Salmon";
//            holoString = "+ Salmon";
//        } else if (fishType < .75) {
//            templateId = "Cod";
//            holoString = "+ Cod";
//        } else if (fishType < .95) {
//            templateId = "Tropical";
//            holoString = "+ Tropical";
//        } else {
//            templateId = "Pufferfish";
//            holoString = "+ Pufferfish";
//        }
//        return new GatheringUtil.GatheringReagentWrapper(templateId, placeHolderType, holoString);
//    }
}
