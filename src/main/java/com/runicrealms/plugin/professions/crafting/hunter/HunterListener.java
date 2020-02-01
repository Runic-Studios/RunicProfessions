package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicCore;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.UUID;

public class HunterListener implements Listener {

    /**
     * When plugin is loaded, add hunter items to hash set for use later
     */
    public HunterListener() {
        HunterShop.initializeHunterItems();
    }

    @EventHandler
    public void onHunterMobDeath(MythicMobDeathEvent e) {

        // verify that a hunter is on-task
        if (!(e.getKiller() instanceof Player)) return;
        Player pl = (Player) e.getKiller();
        String mobInternal = e.getMobType().getInternalName();
        String playerTask = RunicCore.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob");
        if (!mobInternal.equals(playerTask)) return;

        int totalKills = HunterTask.getCurrentKills(pl);
        boolean sendMsg = true;

        if (totalKills+1 < HunterTask.getMobAmount()) {
            RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_kills", totalKills+1);
        } else {
            sendMsg = false;
            HunterTask.givePoints(pl);
            pl.sendMessage
                    (ChatColor.GREEN + "You have completed your hunter task and receive " +
                            ChatColor.GOLD + ChatColor.BOLD + HunterTask.getEarnedPoints(pl) + " points!" +
                            ChatColor.GREEN + " Return to a hunting board for another task.");
            launchFirework(pl);
            RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_mob", null);
            RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_kills", null);
            RunicCore.getInstance().saveConfig();
            RunicCore.getInstance().reloadConfig();
        }

        // give experience
        HunterTask.giveExperience(pl, sendMsg);
    }

    private void launchFirework(Player pl) {
        Firework firework = pl.getWorld().spawn(pl.getEyeLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(0);
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).build());
        firework.setFireworkMeta(meta);
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

        if (pl.getInventory().getItemInMainHand().equals(HunterShop.scryingOrb())) {
            Bukkit.broadcastMessage("scrying orb");
            // todo: add to list, listen to chat message, scan
        } else if (pl.getInventory().getItemInMainHand().equals(HunterShop.trackingCompass())) {
            Bukkit.broadcastMessage("tracking compass");
            // todo: add to list, listen to chat message, track
        } else if (pl.getInventory().getItemInMainHand().equals(HunterShop.enchantScroll())) {
            Bukkit.broadcastMessage("enchant scroll");
            // todo: add 'ItemEnchanter' class
        } else if (pl.getInventory().getItemInMainHand().equals(HunterShop.trackingCompass())) {
            Bukkit.broadcastMessage("tracking compass");
            // todo: add to list, listen to chat message, track
        }
    }

    // todo: add item consume event for shadowmeld potion
}
