package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.events.MobDamageEvent;
import com.runicrealms.plugin.item.GearScanner;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class HunterListener implements Listener {

    private HashSet<UUID> cloakers; // for shadowmeld potion
    private HashSet<UUID> hasDealtDamage; // for shadowmeld potion
    private HashMap<UUID, ItemStack> chatters; // for listening to player chat

    /**
     * When plugin is loaded, add hunter items to hash set for use later
     */
    public HunterListener() {
        //HunterShop.initializeHunterItems();
        cloakers = new HashSet<>();
        hasDealtDamage = new HashSet<>();
        chatters = new HashMap<>();
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

        // prevent player's from using a hunter item in combat
        if (RunicCore.getCombatManager().getPlayersInCombat().containsKey(uuid)) {
            pl.sendMessage(ChatColor.RED + "You can't use that in combat!");
            return;
        }

        if (pl.getInventory().getItemInMainHand().isSimilar(HunterShop.scryingOrb())) {
            pl.sendMessage(ChatColor.YELLOW + "Enter a player name in the chat.");
            chatters.put(pl.getUniqueId(), HunterShop.scryingOrb());
            // remove item
        } else if (pl.getInventory().getItemInMainHand().isSimilar(HunterShop.trackingScroll())) {
            pl.sendMessage(ChatColor.YELLOW + "Enter a player name in the chat.");
            chatters.put(pl.getUniqueId(), HunterShop.trackingScroll());
            // remove item
        } else if (pl.getInventory().getItemInMainHand().isSimilar(HunterShop.trackingCompass())) {
            pl.sendMessage(ChatColor.YELLOW + "Enter a player name in the chat.");
            chatters.put(pl.getUniqueId(), HunterShop.trackingCompass());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (!chatters.containsKey(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
        Player pl = e.getPlayer();

        Player toLookup;
        if (Bukkit.getPlayer(e.getMessage()) == null) {
            pl.sendMessage(ChatColor.RED + "You must enter a valid player.");
            return;
        } else {
            toLookup = Bukkit.getPlayer(e.getMessage());
        }

        if (chatters.get(pl.getUniqueId()).isSimilar(HunterShop.scryingOrb())) {
            lookupStats(pl, toLookup);
        } else if (chatters.get(pl.getUniqueId()).isSimilar(HunterShop.trackingScroll())) {
            lookupLocation(pl, toLookup);
        } else if (chatters.get(pl.getUniqueId()).isSimilar(HunterShop.trackingCompass())) {
            lookupLocation(pl, toLookup);
        }

        chatters.remove(pl.getUniqueId());
    }

    private void lookupStats(Player pl, Player toLookup) {
        int maxHealth = (int) toLookup.getMaxHealth();
        int maxMana = RunicCore.getInstance().getConfig().getInt(toLookup.getUniqueId() + ".info.maxMana");
        int minDamage = GearScanner.getMinDamage(toLookup);
        int maxDamage = GearScanner.getMaxDamage(toLookup);
        int healingBonus = GearScanner.getHealingBoost(toLookup);
        int magicBonus = GearScanner.getMagicBoost(toLookup);
        int shield = GearScanner.getShieldAmt(toLookup);
        pl.sendMessage
                (ChatColor.translateAlternateColorCodes('&', "&e" + toLookup.getName() + "'s Character Stats:" +
                        "\n&c❤ (Health) &etotal: " + maxHealth +
                        "\n&3✸ (Mana) &etotal: " + maxMana +
                        "\n&c⚔ (DMG) &ebonus: " + minDamage + "-" + maxDamage +
                        "\n&a✦ (Heal) &ebonus: " + healingBonus +
                        "\n&3ʔ (Magic) &ebonus: " + magicBonus +
                        "\n&f■ (Shield) &ebonus: " + shield));
    }

    private void lookupLocation(Player pl, Player toLookup) {
        String name = toLookup.getName();
        Location loc = toLookup.getLocation();
        pl.sendMessage(ChatColor.YELLOW + name + " is in world - " + loc.getWorld().getName());
        pl.sendMessage(ChatColor.YELLOW + name + " can be found at: " + (int) loc.getX() + "x, " + (int) loc.getY() + "y, " + (int) loc.getZ() + "z");
    }

    /**
     * For shadowmeld potion
     */
    @EventHandler
    public void onPotionUse(PlayerItemConsumeEvent e) {

        if (e.getItem().getType() == Material.POTION) {

            Player pl = e.getPlayer();
            boolean isShadowmeld = AttributeUtil.getCustomString(e.getItem(), "potion.shadowmeld").equals("true");

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

            if (isShadowmeld) {
                shadowmeld(pl);
            }
        }
    }

    private static final double MOVE_CONSTANT = 0.6;
    private void shadowmeld(Player pl) {
        double timer_initX = Math.round(pl.getLocation().getX() * MOVE_CONSTANT);
        double timer_initY = Math.round(pl.getLocation().getY() * MOVE_CONSTANT);
        double timer_initZ = Math.round(pl.getLocation().getZ() * MOVE_CONSTANT);

        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {

                final Location currLocation = pl.getLocation();
                if ((Math.round(currLocation.getX() * MOVE_CONSTANT) != timer_initX
                        || Math.round(currLocation.getY() * MOVE_CONSTANT) != timer_initY
                        || Math.round(currLocation.getZ() * MOVE_CONSTANT) != timer_initZ)) {
                    this.cancel();
                    pl.sendMessage(ChatColor.RED + "Teleportation cancelled due to movement!");
                    return;
                }

                if (RunicCore.getCombatManager().getPlayersInCombat().containsKey(pl.getUniqueId())) {
                    this.cancel();
                    pl.sendMessage(ChatColor.RED + "Teleportation cancelled due to combat!");
                    return;
                }

                if (count >= 5) {
                    this.cancel();
                    // poof!
                    cloakers.add(pl.getUniqueId());
                    pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5f, 0.5f);
                    pl.getWorld().spawnParticle(Particle.REDSTONE, pl.getEyeLocation(), 25, 0.5f, 0.5f, 0.5f,
                            new Particle.DustOptions(Color.BLACK, 3));
                    pl.sendMessage(ChatColor.GRAY + "You vanished!");
                    return;
                }

                pl.getWorld().spawnParticle(Particle.REDSTONE, pl.getLocation().add(0,1,0),
                        10, 0.5f, 0.5f, 0.5f, new Particle.DustOptions(Color.GRAY, 1));

                pl.sendMessage(ChatColor.GRAY + "Fading into shadow... "
                        + ChatColor.WHITE + ChatColor.BOLD + (5-count) + "s");
                count = count+1;

            }
        }.runTaskTimer(RunicProfessions.getInstance(), 0, 20);

        // reappear after duration or upon dealing damage. can't be tracked async :(
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 30 || hasDealtDamage.contains(pl.getUniqueId())) {
                    this.cancel();
                    cloakers.remove(pl.getUniqueId());
                    for (Player ps : Bukkit.getOnlinePlayers()) {
                        ps.showPlayer(RunicCore.getInstance(), pl);
                    }
                    pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5f, 0.5f);
                    pl.getWorld().spawnParticle(Particle.REDSTONE, pl.getEyeLocation(), 25, 0.5f, 0.5f, 0.5f,
                            new Particle.DustOptions(Color.BLACK, 3));
                    pl.sendMessage(ChatColor.GRAY + "You reappeared!");
                    hasDealtDamage.remove(pl.getUniqueId());
                } else {
                    count++;
                }
            }
        }.runTaskTimer(RunicProfessions.getInstance(), 0, 20);
    }

    /**
     * Player is immune to mob attacks
     */
    @EventHandler
    public void onDamage(MobDamageEvent e) {
        if (!(e.getVictim() instanceof Player)) return;
        Player pl = (Player) e.getVictim();
        if (cloakers.contains(pl.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    /**
     * Reveal the player after dealing damage
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player pl = (Player) e.getDamager();
        if (!cloakers.contains(pl.getUniqueId())) return;
        if (hasDealtDamage.contains(pl.getUniqueId())) return;
        hasDealtDamage.add(pl.getUniqueId());
    }

//    public boolean isSimilar(Player pl, ItemStack item1, ItemStack item2) {
//        ItemStack newItem1 = new ItemStack(item1);
//        newItem1.setAmount(1);
//        Bukkit.broadcastMessage(item1.getAmount() + "");
//        Bukkit.broadcastMessage(item2.getAmount() + "");
//        return newItem1.isSimilar(item2);
//    }
}
