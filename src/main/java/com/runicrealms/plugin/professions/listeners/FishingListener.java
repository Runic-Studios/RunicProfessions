package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.professions.event.GatheringEvent;
import com.runicrealms.plugin.professions.gathering.GatheringRegion;
import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringTool;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Listener for Fishing (Gathering Profession)
 * randomizes which fish they receive
 * Checks name of WG region for "pond" to perform tasks
 */
public class FishingListener implements Listener {

    @EventHandler
    public void onFishCatch(PlayerFishEvent e) {
        // disable default exp
        e.setExpToDrop(0);
        e.setCancelled(false);
        if (e.getCaught() != null) e.getCaught().remove();
        if (e.getState() != PlayerFishEvent.State.BITE) return;
        Player player = e.getPlayer();
        double chance = ThreadLocalRandom.current().nextDouble();

        // roll to see what kind of fish they will receive
        double fishRoll = ThreadLocalRandom.current().nextDouble();
        Location hookLoc = e.getHook().getLocation();

        // ensure the proper type of block is being mined
        GatheringResource gatheringResource = getFishFromRoll(fishRoll, RunicProfessionsAPI.getGatherPlayer(player.getUniqueId()).getFishingLevel());
        String templateId = gatheringResource.getTemplateId();
        String holoString = gatheringResource.getHologramDisplayString();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        // ItemStack fish = RunicItemsAPI.generateItemFromTemplate(gatheringResource.getTemplateId()).generateItem();

        // verify the player is holding a tool
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(gatheringResource.getGatheringSkill().getNoToolMessage());
            return;
        }

        // verify held tool is a fishing rod
        RunicItem runicItem = RunicItemsAPI.getRunicItemFromItemStack(heldItem);
        String templateIdHeldItem = runicItem.getTemplateId();
        Optional<GatheringTool> gatheringTool = GatheringUtil.getRods().stream().filter(tool -> tool.getRunicItemDynamic().getTemplateId().equals(templateIdHeldItem)).findFirst();
        if (!gatheringTool.isPresent()) {
            player.sendMessage(gatheringResource.getGatheringSkill().getNoToolMessage());
            return;
        }

        GatheringEvent gatheringEvent = new GatheringEvent
                (
                        player,
                        gatheringResource,
                        gatheringTool.get(),
                        heldItem,
                        templateId,
                        hookLoc,
                        null,
                        gatheringResource.getResourceBlockType(),
                        holoString,
                        chance,
                        gatheringResource.getResourceBlockType()
                );
        Bukkit.getPluginManager().callEvent(gatheringEvent);
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

    /**
     * Roll a die to determine which fish the player should receive
     * If they have not reached the necessary level for that fish,
     * they will receive the default fish instead (salmon)
     *
     * @param roll               random double between 0-1
     * @param playerFishingLevel the level of fishing
     * @return the appropriate fish to gather
     */
    private GatheringResource getFishFromRoll(double roll, int playerFishingLevel) {
        GatheringResource gatheringResource = GatheringResource.SALMON;
        if (roll < .5) {
            return gatheringResource;
        } else if (roll < .75) {
            gatheringResource = GatheringResource.COD;
            if (playerFishingLevel >= gatheringResource.getRequiredLevel())
                return GatheringResource.COD;
        } else if (roll < .95) {
            gatheringResource = GatheringResource.TROPICAL_FISH;
            if (playerFishingLevel >= gatheringResource.getRequiredLevel())
                return GatheringResource.TROPICAL_FISH;
        } else {
            gatheringResource = GatheringResource.PUFFERFISH;
            if (playerFishingLevel >= gatheringResource.getRequiredLevel())
                return GatheringResource.PUFFERFISH;
        }
        return GatheringResource.SALMON;
    }
}
