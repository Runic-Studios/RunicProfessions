package com.runicrealms.plugin.professions.crafting.enchanter;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.events.MobDamageEvent;
import com.runicrealms.plugin.events.SpellDamageEvent;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import com.runicrealms.plugin.item.GearScanner;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.utilities.DamageUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
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
import java.util.Random;
import java.util.UUID;

public class EnchantListener implements Listener {

    private static final double CRIT_MODIFIER = 1.5;
    private static final double MOVE_CONSTANT = 0.6;
    private static int WARMUP_TIME = 5;
    private HashMap<UUID, BukkitTask> currentlyUsing = new HashMap<>();

    @EventHandler
    public void onTeleportScrollUse(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return; // annoying 1.9 feature which makes the event run twice, once for each hand
        if (currentlyUsing.containsKey(e.getPlayer().getUniqueId())) return;

        Material mat = e.getPlayer().getInventory().getItemInMainHand().getType();
        if (mat != Material.PURPLE_DYE) return;
        if (AttributeUtil.getCustomString(e.getPlayer().getInventory().getItemInMainHand(),
                "scroll.location") == null) return;

        ItemStack scroll = e.getPlayer().getInventory().getItemInMainHand();

        int reqLevel = (int) AttributeUtil.getCustomDouble(scroll, "required.level");
        if (RunicCore.getCacheManager().getPlayerCache(e.getPlayer().getUniqueId()).getClassLevel() < reqLevel) {
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.0f);
            e.getPlayer().sendMessage(ChatColor.RED + "Your level is too low to use this!");
            return;
        }

        currentlyUsing.put(e.getPlayer().getUniqueId(), warmupScroll(e.getPlayer(), scroll));
    }

    @EventHandler
    public void onSpellDamage(SpellDamageEvent e) {
        // listen for crit if attacker has crit, dodge and thorns for defender
        if (applyCrit(e.getPlayer())) {
            e.setAmount((int) (e.getAmount()*CRIT_MODIFIER));
        }
        if (e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            if (applyDodge(victim)) {
                e.setCancelled(true);
                return;
            }
            if (applyThorns(victim)) {
                DamageUtil.damageEntitySpell(e.getAmount(), victim, e.getPlayer(), 100);
            }
        }
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        // listen for crit if attacker has crit, dodge and thorns for defender (if defender is player)
        if (applyCrit(e.getPlayer())) {
            e.setAmount((int) (e.getAmount()*CRIT_MODIFIER));
        }
        if (e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            if (applyDodge(victim)) {
                e.setCancelled(true);
                return;
            }
            if (applyThorns(victim)) {
                DamageUtil.damageEntitySpell(e.getAmount(), e.getPlayer(), victim, 100);
            }
        }
    }


    @EventHandler
    public void onMobDamage(MobDamageEvent e) {
        // listen for dodge and thorns for defender (the player)
        if (e.getVictim() instanceof Player) {
            Player victim = (Player) e.getVictim();
            if (applyDodge(victim)) {
                e.setCancelled(true);
                return;
            }
            if (applyThorns(victim)) {
                DamageUtil.damageEntitySpell(e.getAmount(), (LivingEntity) e.getDamager(), victim, 100);
            }
        }
    }

    private boolean applyCrit(Player pl) {
        int critChance = GearScanner.getCritEnchant(pl);
        if (critChance > 0) {
            Random rand = new Random();
            int roll = rand.nextInt(100) + 1;
            return roll <= critChance;
        } else {
            return false;
        }
    }

    private boolean applyDodge(Player pl) {
        int dodgeChance = GearScanner.getDodgeEnchant(pl);
        if (dodgeChance > 0) {
            Random rand = new Random();
            int roll = rand.nextInt(100) + 1;
            return roll <= dodgeChance;
        } else {
            return false;
        }
    }

    private boolean applyThorns(Player pl) {
        int thornsChance = GearScanner.getThornsEnchant(pl);
        if (thornsChance > 0) {
            Random rand = new Random();
            int roll = rand.nextInt(100) + 1;
            return roll <= thornsChance;
        } else {
            return false;
        }
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
                    pl.teleport(TeleportEnum.getEnum(
                            (AttributeUtil.getCustomString(scroll, "scroll.location"))).getLocation());
                    currentlyUsing.remove(pl.getUniqueId());
                    pl.playSound(pl.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 1.0f);
                    pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You've have been teleported!");
                    return;
                }

                pl.getWorld().spawnParticle(Particle.REDSTONE, pl.getLocation().add(0,1,0),
                        10, 0.5f, 0.5f, 0.5f, new Particle.DustOptions(Color.AQUA, 3));

                pl.sendMessage(ChatColor.AQUA + "Teleporting... " + ChatColor.WHITE + (WARMUP_TIME -count) + "s");
                count = count+1;

            }
        }.runTaskTimer(RunicCore.getInstance(), 0, 20);
    }
}
