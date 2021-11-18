package com.runicrealms.plugin.professions.crafting.alchemist;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.events.LootEvent;
import com.runicrealms.plugin.events.SpellCastEvent;
import com.runicrealms.plugin.events.SpellDamageEvent;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.plugin.spellapi.spellutil.HealUtil;
import com.runicrealms.plugin.utilities.ColorUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.item.RunicItem;
import com.runicrealms.runicitems.item.event.RunicItemGenericTriggerEvent;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PotionListener implements Listener {

    private static final String DATA_KEY_AMOUNT = "amount";
    private static final String DATA_KEY_DURATION = "duration";
    private static final Set<UUID> npcClickers = new HashSet<>();
    private static final Map<UUID, Double> looters = new HashMap<>();
    private static final Map<UUID, Double> slayers = new HashMap<>();
    private static final Map<UUID, Pair> pyromaniacs = new HashMap<>();
    private final HashSet<String> healingPotions = new HashSet<String>() {{
        add("minor-potion-healing");
        add("major-potion-healing");
        add("greater-potion-healing");
        add(CraftedResource.LESSER_POTION_HEALING.getTemplateId());
        add(CraftedResource.MINOR_POTION_HEALING.getTemplateId());
        add(CraftedResource.MAJOR_POTION_HEALING.getTemplateId());
        add(CraftedResource.GREATER_POTION_HEALING.getTemplateId());
    }};
    private final HashSet<String> craftedManaPotions = new HashSet<String>() {{
        add("minor-potion-mana");
        add("major-potion-mana");
        add("greater-potion-mana");
        add(CraftedResource.LESSER_POTION_MANA.getTemplateId());
        add(CraftedResource.MINOR_POTION_MANA.getTemplateId());
        add(CraftedResource.MAJOR_POTION_MANA.getTemplateId());
        add(CraftedResource.GREATER_POTION_MANA.getTemplateId());
    }};
    private final HashSet<String> craftedSlayingPotions = new HashSet<String>() {{
        add(CraftedResource.LESSER_POTION_SLAYING.getTemplateId());
        add(CraftedResource.MINOR_POTION_SLAYING.getTemplateId());
        add(CraftedResource.MAJOR_POTION_SLAYING.getTemplateId());
        add(CraftedResource.GREATER_POTION_SLAYING.getTemplateId());
    }};
    private final HashSet<String> craftedLootingPotions = new HashSet<String>() {{
        add(CraftedResource.MINOR_POTION_LOOTING.getTemplateId());
        add(CraftedResource.GREATER_POTION_LOOTING.getTemplateId());
    }};

    /**
     * This disables potion drinking if a player is talking to an NPC
     */
    @EventHandler
    public void onNPCInteract(PlayerInteractEntityEvent e) {
        if (!e.getRightClicked().hasMetadata("NPC")) return;
        npcClickers.add(e.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(RunicProfessions.getInstance(),
                () -> npcClickers.remove(e.getPlayer().getUniqueId()), 20L);
    }

    /**
     * Handles custom potions
     */
    @EventHandler(priority = EventPriority.HIGHEST) // last
    public void onPotionUse(RunicItemGenericTriggerEvent e) {

        if (e.isCancelled()) return;
        if (e.getItem() == null) return;
        if (e.getItem().getDisplayableItem().getMaterial() != Material.POTION) return;
        if (npcClickers.contains(e.getPlayer().getUniqueId())) return; // for quest interactions

        Player player = e.getPlayer();
        ItemStack itemStack = e.getItemStack();
        ItemRemover.takeItem(player, itemStack, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.5f, 1.0f);

        if (healingPotions.contains(e.getItem().getTemplateId())) {
            handlePotionHealing(player, e.getItem());
        } else if (craftedManaPotions.contains(e.getItem().getTemplateId())) {
            handlePotionMana(player, e.getItem());
        } else if (craftedSlayingPotions.contains(e.getItem().getTemplateId())) {
            handlePotionSlaying(player, e.getItem());
        } else if (craftedLootingPotions.contains(e.getItem().getTemplateId())) {
            handlePotionLooting(player, e.getItem());
        } else if (e.getItem().getTemplateId().equals("crafted-potion-sacred-fire")) {
            handlePotionSacredFire(player, e.getItem());
        }
    }

    private void applyPotionCooldown(Player player) {
        SpellCastEvent event = new SpellCastEvent(player, RunicCoreAPI.getSpell("Potion"));
        Bukkit.getPluginManager().callEvent(event);
    }

    private void handlePotionHealing(Player player, RunicItem runicItem) {
        int healAmt = Integer.parseInt(runicItem.getData().get(DATA_KEY_AMOUNT));
        HealUtil.healPlayer(healAmt, player, player, false);
        applyPotionCooldown(player);
    }

    private void handlePotionMana(Player player, RunicItem runicItem) {
        int manaAmt = Integer.parseInt(runicItem.getData().get(DATA_KEY_AMOUNT));
        RunicCore.getRegenManager().addMana(player, manaAmt);
        applyPotionCooldown(player);
    }

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

    private void handlePotionLooting(Player player, RunicItem runicItem) {
        double amount = Double.parseDouble(runicItem.getData().get(DATA_KEY_AMOUNT)) / 100.0;
        int lootingDuration = Integer.parseInt(runicItem.getData().get(DATA_KEY_DURATION));
        looters.put(player.getUniqueId(), amount);
        player.sendMessage(ColorUtil.format("&eYou've gained a &f" + (int) (amount * 100) + "% &echance of &ndouble loot&r &efor &f" + lootingDuration + " &eseconds!"));
        Bukkit.getScheduler().runTaskLaterAsynchronously(RunicProfessions.getInstance(), () -> {
            looters.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GRAY + "Your potion of looting has expired.");
        }, lootingDuration * 20L);
    }

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
     * Logic for slayer potions
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMeleeDamage(WeaponDamageEvent e) {
        if (!slayers.containsKey(e.getPlayer().getUniqueId())) return;
        double percent = slayers.get(e.getPlayer().getUniqueId());
        int extraAmt = (int) (e.getAmount() * percent);
        if (extraAmt < 1) {
            extraAmt = 1;
        }
        e.setAmount(e.getAmount() + extraAmt);
    }

    /**
     * Logic for sacred fire and slayer potions
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpellDamage(SpellDamageEvent e) {
        if (pyromaniacs.containsKey(e.getPlayer().getUniqueId())) {
            double chance = ThreadLocalRandom.current().nextDouble();
            // 20% chance for burn
            if (chance > pyromaniacs.get(e.getPlayer().getUniqueId()).getChance()) return;
            e.setAmount(e.getAmount() + pyromaniacs.get(e.getPlayer().getUniqueId()).getAmount());
            LivingEntity victim = e.getVictim();
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.25f, 1.25f);
            victim.getWorld().spawnParticle(Particle.FLAME, victim.getEyeLocation(), 5, 0.5F, 0.5F, 0.5F, 0);
        }
        if (slayers.containsKey(e.getPlayer().getUniqueId())) {
            double percent = slayers.get(e.getPlayer().getUniqueId());
            int extraAmt = (int) (e.getAmount() * percent);
            if (extraAmt < 1) {
                extraAmt = 1;
            }
            e.setAmount(e.getAmount() + extraAmt);
        }
    }

    /**
     * Logic for looting potions
     */
    @EventHandler
    public void onLoot(LootEvent e) {
        if (!looters.containsKey(e.getPlayer().getUniqueId())) return;
        Player player = e.getPlayer();
        double chance = ThreadLocalRandom.current().nextDouble();
        // 20% chance for item
        if (chance > looters.get(e.getPlayer().getUniqueId())) return;
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
        player.sendMessage(ChatColor.GREEN + "You've received double loot from your potion of looting!");
        ItemStack item = e.getItemStack();
        RunicItemsAPI.addItem(player.getInventory(), item, true);
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
