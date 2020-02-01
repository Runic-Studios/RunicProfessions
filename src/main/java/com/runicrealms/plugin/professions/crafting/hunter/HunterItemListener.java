package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicCore;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class HunterItemListener implements Listener {

    /**
     * When plugin is loaded, add hunter items to hash set for use later
     */
    public HunterItemListener() {
        HunterShop.initializeHunterItems();
    }

    @EventHandler
    public void onHunterItemUse(PlayerInteractEvent e) {

        Player pl = e.getPlayer();
        UUID uuid = pl.getUniqueId();

        if (pl.getInventory().getItemInMainHand().getType() == Material.AIR) return;
        if (pl.getGameMode() == GameMode.CREATIVE) return;

        // annoying 1.9 feature which makes the event run twice, once for each hand
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // check for hunter item
        if (!HunterShop.getHunterItems().contains(pl.getInventory().getItemInMainHand())) return;

        // prevent player's from using a hunter item in combat
        if (RunicCore.getCombatManager().getPlayersInCombat().containsKey(uuid)) {
            pl.sendMessage(ChatColor.RED + "You can't use that in combat!");
            return;
        }

        if (pl.getInventory().getItemInMainHand().equals(HunterShop.shadowmeldPotion())) {
            Bukkit.broadcastMessage("shadowmeld potion");
        } else if (pl.getInventory().getItemInMainHand().getType().equals(Material.OAK_BOAT)) {
            Bukkit.broadcastMessage("oak boat");
        } else if (pl.getInventory().getItemInMainHand().equals(HunterShop.scryingOrb())) {
            Bukkit.broadcastMessage("scrying orb");
        } else if (pl.getInventory().getItemInMainHand().equals(HunterShop.trackingCompass())) {
            Bukkit.broadcastMessage("tracking compass");
        } else if (pl.getInventory().getItemInMainHand().equals(HunterShop.enchantScroll())) {
            Bukkit.broadcastMessage("enchant scroll");
        } else if (pl.getInventory().getItemInMainHand().equals(HunterShop.trackingCompass())) {
            Bukkit.broadcastMessage("tracking compass");
        }
    }
}
