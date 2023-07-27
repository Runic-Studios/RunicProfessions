package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.professions.utilities.GatheringUtil;
import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.professions.gathering.FishingSession;
import com.runicrealms.plugin.professions.gathering.GatheringRegion;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.gathering.GatheringTool;
import com.runicrealms.plugin.runicitems.RunicItemsAPI;
import com.runicrealms.plugin.runicitems.item.RunicItem;
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

import java.util.Optional;

/**
 * Listener for Fishing (Gathering Profession)
 * randomizes which fish they receive
 * Checks name of WG region for "pond" to perform tasks
 */
public class FishingListener implements Listener {

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
        Location hookLoc = event.getHook().getLocation();

        ItemStack heldItem = player.getInventory().getItemInMainHand();

        // Verify the player is holding a tool
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.sendMessage(GatheringSkill.FISHING.getNoToolMessage());
            return;
        }

        // Verify held tool is a fishing rod
        RunicItem runicItem = RunicItemsAPI.getRunicItemFromItemStack(heldItem);
        String templateIdHeldItem = runicItem.getTemplateId();
        Optional<GatheringTool> gatheringTool = GatheringUtil.getRods().stream().filter
                (
                        tool -> tool.getRunicItemDynamic().getTemplateId().equals(templateIdHeldItem)
                ).findFirst();
        if (gatheringTool.isEmpty()) {
            player.sendMessage(GatheringSkill.FISHING.getNoToolMessage());
            return;
        }

        // Remove the hook entity
        event.getHook().remove();

        // Start the fishing session
        FishingSession fishingSession = new FishingSession(player, hookLoc, gatheringTool.get(), heldItem);
        fishingSession.startSession();
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
            event.setCancelled(true);
        }
    }
}
