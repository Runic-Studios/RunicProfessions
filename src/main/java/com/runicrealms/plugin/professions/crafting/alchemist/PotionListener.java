package com.runicrealms.plugin.professions.crafting.alchemist;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.events.LootEvent;
import com.runicrealms.plugin.events.SpellDamageEvent;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import com.runicrealms.plugin.spellapi.spellutil.HealUtil;
import com.runicrealms.plugin.utilities.ColorUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PotionListener implements Listener {

    private static List<UUID> slayers = new ArrayList<>();
    private static List<UUID> looters = new ArrayList<>();
    private static List<UUID> pyromaniacs = new ArrayList<>();
    private static final int FIRE_AMT = 20;

    /**
     * Handles custom potions
     */
    @EventHandler
    public void onPotionUse(PlayerItemConsumeEvent e) {

        if (e.getItem().getType() == Material.POTION) {

            Player pl = e.getPlayer();
            int healAmt = (int) AttributeUtil.getCustomDouble(e.getItem(), "potion.healing");
            int manaAmt = (int) AttributeUtil.getCustomDouble(e.getItem(), "potion.mana");
            int slayingDuration = (int) AttributeUtil.getCustomDouble(e.getItem(), "potion.slaying");
            int lootingDuration = (int) AttributeUtil.getCustomDouble(e.getItem(), "potion.looting");
            int fireDuration = (int) AttributeUtil.getCustomDouble(e.getItem(), "potion.fire");

            // remove glass bottle from inventory, main hand or offhand
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (pl.getInventory().getItemInOffHand().getType() == Material.GLASS_BOTTLE) {
                        pl.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                    } else {
                        pl.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }
                }
            }.runTaskLaterAsynchronously(RunicProfessions.getInstance(), 1L);

            if (healAmt > 0) {
                HealUtil.healPlayer(healAmt, pl, pl, false, false, false);
            }

            if (manaAmt > 0) {
                RunicCore.getManaManager().addMana(pl, manaAmt, false);
            }

            if (slayingDuration > 0) {
                slayers.add(pl.getUniqueId());
                pl.sendMessage(ColorUtil.format("&eYou've gained a &f20% &edamage bonus vs. monsters for &f" + slayingDuration + " &eminutes!"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        slayers.remove(pl.getUniqueId());
                        pl.sendMessage(ChatColor.GRAY + "Your potion of slaying has expired.");
                    }
                }.runTaskLaterAsynchronously(RunicProfessions.getInstance(), slayingDuration*60*20L);
            }

            if (lootingDuration > 0) {
                looters.add(pl.getUniqueId());
                pl.sendMessage(ColorUtil.format("&eYou've gained a &f20% &echance of &ndouble loot&r &efor &f" + lootingDuration + " &eminutes!"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        looters.remove(pl.getUniqueId());
                        pl.sendMessage(ChatColor.GRAY + "Your potion of looting has expired.");
                    }
                }.runTaskLaterAsynchronously(RunicProfessions.getInstance(), lootingDuration*60*20L);
            }

            if (fireDuration > 0) {
                pyromaniacs.add(pl.getUniqueId());
                pl.sendMessage(ColorUtil.format("&eYour spells now have a &f20% &echance to burn enemies &efor &f" + fireDuration + " &eminutes!"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        pyromaniacs.remove(pl.getUniqueId());
                        pl.sendMessage(ChatColor.GRAY + "Your potion of sacred fire has expired.");
                    }
                }.runTaskLaterAsynchronously(RunicProfessions.getInstance(), fireDuration*60*20L);
            }
        }
    }

    /**
     * Low prio so its calculated last
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMeleeDamage(WeaponDamageEvent e) {

        if (!slayers.contains(e.getPlayer().getUniqueId())) return;

        double percent = 0.2;
        int extraAmt = (int) (e.getAmount() * percent);
        if (extraAmt < 1) {
            extraAmt = 1;
        }
        e.setAmount(e.getAmount() + extraAmt);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpellDamage(SpellDamageEvent e) {

        if (pyromaniacs.contains(e.getPlayer().getUniqueId())) {
            double chance = ThreadLocalRandom.current().nextDouble(0, 100);

            // 20% chance for burn
            if (chance > 80) {
                e.setAmount(e.getAmount() + FIRE_AMT);
                LivingEntity victim = (LivingEntity) e.getEntity();
                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.25f, 1.25f);
                victim.getWorld().spawnParticle(Particle.FLAME, victim.getEyeLocation(), 5, 0.5F, 0.5F, 0.5F, 0);
            }
        }

        if (slayers.contains(e.getPlayer().getUniqueId())) {

            double percent = 0.2;
            int extraAmt = (int) (e.getAmount() * percent);
            if (extraAmt < 1) {
                extraAmt = 1;
            }
            e.setAmount(e.getAmount() + extraAmt);
        }
    }

    @EventHandler
    public void onLoot(LootEvent e) {

        if (!looters.contains(e.getPlayer().getUniqueId())) return;
        Player pl = e.getPlayer();

        double chance = ThreadLocalRandom.current().nextDouble(0, 100);

        // 20% chance for item
        if (chance > 80) {
            pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
            pl.sendMessage(ChatColor.GREEN + "You've received double loot from your potion of looting!");
            ItemStack item = e.getItemStack();

            if (pl.getInventory().firstEmpty() != -1) {
                int firstEmpty = pl.getInventory().firstEmpty();
                pl.getInventory().setItem(firstEmpty, item);
            } else {
                pl.getWorld().dropItem(pl.getLocation(), item);
            }
        }
    }
}
