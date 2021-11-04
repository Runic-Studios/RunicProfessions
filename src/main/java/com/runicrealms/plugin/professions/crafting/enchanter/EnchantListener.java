package com.runicrealms.plugin.professions.crafting.enchanter;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.util.ItemRemover;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class EnchantListener implements Listener {

    private static final double MOVE_CONSTANT = 0.6;
    private static final int WARMUP_TIME = 5;
    private final HashMap<UUID, BukkitTask> currentlyUsing = new HashMap<>();

    @EventHandler
    public void onTeleportScrollUse(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND)
            return; // annoying 1.9 feature which makes the event run twice, once for each hand
        if (currentlyUsing.containsKey(e.getPlayer().getUniqueId())) return;

        Material mat = e.getPlayer().getInventory().getItemInMainHand().getType();
        if (mat != Material.PURPLE_DYE) return;

        // summoning scroll
        if (e.getPlayer().getInventory().getItemInMainHand().equals(EnchantingTableMenu.partySummonScroll())) {
            if (RunicCore.getPartyManager().getPlayerParty(e.getPlayer()) == null) {
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                e.getPlayer().sendMessage(ChatColor.RED + "You must be in a party to use this scroll!");
            } else {
                currentlyUsing.put(e.getPlayer().getUniqueId(), warmupScroll(e.getPlayer(), e.getPlayer().getInventory().getItemInMainHand()));
            }
            return;
        }

        if (AttributeUtil.getCustomString(e.getPlayer().getInventory().getItemInMainHand(),
                "scroll.location").equals("")) return;

        ItemStack scroll = e.getPlayer().getInventory().getItemInMainHand();

        int reqLevel = (int) AttributeUtil.getCustomDouble(scroll, "required.level");
        if (RunicCoreAPI.getPlayerCache(e.getPlayer()).getClassLevel() < reqLevel) {
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.0f);
            e.getPlayer().sendMessage(ChatColor.RED + "Your level is too low to use this!");
            return;
        }

        currentlyUsing.put(e.getPlayer().getUniqueId(), warmupScroll(e.getPlayer(), scroll));
    }

    private BukkitTask warmupScroll(Player pl, ItemStack scroll) {

        double timer_initX = Math.round(pl.getLocation().getX() * MOVE_CONSTANT);
        double timer_initY = Math.round(pl.getLocation().getY() * MOVE_CONSTANT);
        double timer_initZ = Math.round(pl.getLocation().getZ() * MOVE_CONSTANT);

        return new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {

                final Location currLocation = pl.getLocation();
                if ((Math.round(currLocation.getX() * MOVE_CONSTANT) != timer_initX
                        || Math.round(currLocation.getY() * MOVE_CONSTANT) != timer_initY
                        || Math.round(currLocation.getZ() * MOVE_CONSTANT) != timer_initZ)) {
                    this.cancel();
                    currentlyUsing.remove(pl.getUniqueId());
                    pl.sendMessage(ChatColor.RED + "Teleport scroll cancelled due to movement!");
                    return;
                }

                if (RunicCore.getCombatManager().getPlayersInCombat().containsKey(pl.getUniqueId())) {
                    this.cancel();
                    currentlyUsing.remove(pl.getUniqueId());
                    pl.sendMessage(ChatColor.RED + "Teleport scroll cancelled due to combat!");
                    return;
                }

                if (count >= WARMUP_TIME) {
                    this.cancel();
                    ItemRemover.takeItem(pl, scroll, 1);
                    currentlyUsing.remove(pl.getUniqueId());
                    if (scroll.equals(EnchantingTableMenu.partySummonScroll())
                            && RunicCore.getPartyManager().getPlayerParty(pl) != null) {

                        for (Player mem : RunicCore.getPartyManager().getPlayerParty(pl).getMembersWithLeader()) {
                            mem.teleport(currLocation);
                            mem.playSound(pl.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 1.0f);
                            mem.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You've been party summoned!");
                            return;
                        }

                    } else {
                        pl.teleport(TeleportEnum.getEnum(
                                (AttributeUtil.getCustomString(scroll, "scroll.location"))).getLocation());
                        pl.playSound(pl.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 1.0f);
                        pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You've have been teleported!");
                        return;
                    }
                }

                pl.getWorld().spawnParticle(Particle.REDSTONE, pl.getLocation().add(0, 1, 0),
                        10, 0.5f, 0.5f, 0.5f, new Particle.DustOptions(Color.AQUA, 3));

                pl.sendMessage(ChatColor.AQUA + "Teleporting... " + ChatColor.WHITE + (WARMUP_TIME - count) + "s");
                count = count + 1;

            }
        }.runTaskTimer(RunicCore.getInstance(), 0, 20);
    }
}
