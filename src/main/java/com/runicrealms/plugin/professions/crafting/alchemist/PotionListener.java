package com.runicrealms.plugin.professions.crafting.alchemist;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.common.util.ColorUtil;
import com.runicrealms.plugin.events.MagicDamageEvent;
import com.runicrealms.plugin.events.PhysicalDamageEvent;
import com.runicrealms.runicitems.item.RunicItem;
import com.runicrealms.runicitems.item.event.RunicItemGenericTriggerEvent;
import com.runicrealms.runicitems.item.stats.RunicItemTag;
import com.runicrealms.runicitems.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PotionListener implements Listener {
    private static final String DATA_KEY_AMOUNT = "amount";
    private static final String DATA_KEY_DURATION = "duration";
    private static final String DATA_KEY_MULTIPLIER = "multiplier";
    private static final Set<UUID> npcClickers = new HashSet<>();
    private static final Map<UUID, Double> slayers = new HashMap<>();
    private static final Map<UUID, Pair> pyromaniacs = new HashMap<>();
    private final HashSet<String> healingPotions = new HashSet<String>() {{
        add("minor-potion-healing");
        add("major-potion-healing");
        add("greater-potion-healing");
        add("lesser-crafted-potion-healing");
        add("minor-crafted-potion-healing");
        add("major-crafted-potion-healing");
        add("greater-crafted-potion-healing");
    }};
    private final HashSet<String> craftedManaPotions = new HashSet<String>() {{
        add("minor-potion-mana");
        add("major-potion-mana");
        add("greater-potion-mana");
        add("lesser-crafted-potion-mana");
        add("minor-crafted-potion-mana");
        add("major-crafted-potion-mana");
        add("greater-crafted-potion-mana");
    }};
    private final HashSet<String> craftedHastePotions = new HashSet<String>() {{
        add("lesser-crafted-potion-haste");
        add("minor-crafted-potion-haste");
        add("major-crafted-potion-haste");
        add("greater-crafted-potion-haste");
    }};
    private final HashSet<String> craftedSlayingPotions = new HashSet<String>() {{
        add("lesser-crafted-potion-slaying");
        add("minor-crafted-potion-slaying");
        add("major-crafted-potion-slaying");
        add("greater-crafted-potion-slaying");
    }};

    /**
     * @param player    who used the potion
     * @param runicItem of the potion item
     */
    private void handlePotionHaste(Player player, RunicItem runicItem) {
        int multiplier = Integer.parseInt(runicItem.getData().get(DATA_KEY_MULTIPLIER));
        int duration = Integer.parseInt(runicItem.getData().get(DATA_KEY_DURATION));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration * 20,
                multiplier));
        int amount = multiplier > 0 ? 40 : 20;
        player.sendMessage(ColorUtil.format("&eYou've gained &f" + amount + "% " +
                "&emining speed &f" + duration + " &eseconds!"));
//        Bukkit.getScheduler().runTaskLaterAsynchronously(RunicProfessions.getInstance(), () -> {
//            slayers.remove(player.getUniqueId());
//            player.sendMessage(ChatColor.GRAY + "Your potion of slaying has expired.");
//        }, slayingDuration * 20L);
    }

    /**
     * @param player    who used the potion
     * @param runicItem of the potion item
     */
    private void handlePotionHealing(Player player, RunicItem runicItem) {
        int healAmt = Integer.parseInt(runicItem.getData().get(DATA_KEY_AMOUNT));
        RunicCore.getSpellAPI().healPlayer(player, player, healAmt);
    }

    /**
     * @param player    who used the potion
     * @param runicItem of the potion item
     */
    private void handlePotionMana(Player player, RunicItem runicItem) {
        int manaAmt = Integer.parseInt(runicItem.getData().get(DATA_KEY_AMOUNT));
        RunicCore.getRegenManager().addMana(player, manaAmt);
    }

    /**
     * @param player    who used the potion
     * @param runicItem of the potion item
     */
    private void handlePotionSacredFire(Player player, RunicItem runicItem) {
        int amount = Integer.parseInt(runicItem.getData().get(DATA_KEY_AMOUNT));
        double chance = Double.parseDouble(runicItem.getData().get("chance")) / 100.0;
        int fireDuration = Integer.parseInt(runicItem.getData().get(DATA_KEY_DURATION));
        pyromaniacs.put(player.getUniqueId(), new Pair(amount, chance));
        player.sendMessage(ColorUtil.format("&eYour spells now have a &f" + (chance * 100) + "% &echance to burn enemies &efor &f" + fireDuration + " &eminutes!"));
        Bukkit.getScheduler().runTaskLaterAsynchronously(RunicProfessions.getInstance(), () -> {
            pyromaniacs.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GRAY + "Your potion of sacred fire has expired.");
        }, fireDuration * 20L);
    }

    /**
     * @param player    who used the potion
     * @param runicItem of the potion item
     */
    private void handlePotionSlaying(Player player, RunicItem runicItem) {
        double amount = Double.parseDouble(runicItem.getData().get(DATA_KEY_AMOUNT)) / 100.0;
        int slayingDuration = Integer.parseInt(runicItem.getData().get(DATA_KEY_DURATION));
        slayers.put(player.getUniqueId(), amount);
        player.sendMessage(ColorUtil.format("&eYou've gained a &f" + (int) (amount * 100) + "% &edamage bonus vs. monsters for &f" + slayingDuration + " &eseconds!"));
        Bukkit.getScheduler().runTaskLaterAsynchronously(RunicProfessions.getInstance(), () -> {
            slayers.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GRAY + "Your potion of slaying has expired.");
        }, slayingDuration * 20L);
    }

    /**
     * Logic for slayer potions
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMeleeDamage(PhysicalDamageEvent event) {
        if (!slayers.containsKey(event.getPlayer().getUniqueId())) return;
        double percent = slayers.get(event.getPlayer().getUniqueId());
        int extraAmt = (int) (event.getAmount() * percent);
        if (extraAmt < 1) {
            extraAmt = 1;
        }
        event.setAmount(event.getAmount() + extraAmt);
    }

    /**
     * This disables potion drinking if a player is talking to an NPC
     */
    @EventHandler
    public void onNPCInteract(PlayerInteractEntityEvent event) {
        if (!event.getRightClicked().hasMetadata("NPC")) return;
        npcClickers.add(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(RunicProfessions.getInstance(),
                () -> npcClickers.remove(event.getPlayer().getUniqueId()), 20L);
    }

    /**
     * Handles custom potions
     */
    @EventHandler(priority = EventPriority.HIGHEST) // last
    public void onPotionUse(RunicItemGenericTriggerEvent event) {

        if (event.isCancelled()) return;
        if (event.getItem() == null) return;
        if (!event.getItem().getTags().contains(RunicItemTag.POTION)) return;
        if (npcClickers.contains(event.getPlayer().getUniqueId())) return; // for quest interactions

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemStack();
        ItemUtils.takeItem(player, itemStack, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.5f, 1.0f);

        if (healingPotions.contains(event.getItem().getTemplateId())) {
            handlePotionHealing(player, event.getItem());
        } else if (craftedManaPotions.contains(event.getItem().getTemplateId())) {
            handlePotionMana(player, event.getItem());
        } else if (craftedHastePotions.contains(event.getItem().getTemplateId())) {
            handlePotionHaste(player, event.getItem());
        } else if (craftedSlayingPotions.contains(event.getItem().getTemplateId())) {
            handlePotionSlaying(player, event.getItem());
        } else if (event.getItem().getTemplateId().equals("crafted-potion-sacred-fire")) {
            handlePotionSacredFire(player, event.getItem());
        }
    }

    /**
     * Logic for sacred fire and slayer potions
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpellDamage(MagicDamageEvent event) {
        if (pyromaniacs.containsKey(event.getPlayer().getUniqueId())) {
            double chance = ThreadLocalRandom.current().nextDouble();
            // 20% chance for burn
            if (chance > pyromaniacs.get(event.getPlayer().getUniqueId()).getChance()) return;
            event.setAmount(event.getAmount() + pyromaniacs.get(event.getPlayer().getUniqueId()).getAmount());
            LivingEntity victim = event.getVictim();
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.25f, 1.25f);
            victim.getWorld().spawnParticle(Particle.FLAME, victim.getEyeLocation(), 5, 0.5F, 0.5F, 0.5F, 0);
        }
        if (slayers.containsKey(event.getPlayer().getUniqueId())) {
            double percent = slayers.get(event.getPlayer().getUniqueId());
            int extraAmt = (int) (event.getAmount() * percent);
            if (extraAmt < 1) {
                extraAmt = 1;
            }
            event.setAmount(event.getAmount() + extraAmt);
        }
    }

    static class Pair {
        private final int amount;
        private final double chance;

        public Pair(int amount, double chance) {
            this.amount = amount;
            this.chance = chance;
        }

        public int getAmount() {
            return amount;
        }

        public double getChance() {
            return chance;
        }
    }
}
