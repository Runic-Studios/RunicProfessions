package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.api.event.ChatChannelMessageEvent;
import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.character.api.CharacterLoadEvent;
import com.runicrealms.plugin.database.PlayerMongoData;
import com.runicrealms.plugin.database.PlayerMongoDataSection;
import com.runicrealms.plugin.database.event.CacheSaveEvent;
import com.runicrealms.plugin.database.event.CacheSaveReason;
import com.runicrealms.plugin.events.MobDamageEvent;
import com.runicrealms.plugin.events.SpellDamageEvent;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.player.cache.PlayerCache;
import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.event.ProfessionChangeEvent;
import com.runicrealms.runicitems.item.event.RunicItemGenericTriggerEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class HunterListener implements Listener {

    private static final double MOVE_CONSTANT = 0.6;
    private final HashSet<UUID> cloakers; // for shadowmeld potion
    private final HashSet<UUID> hasDealtDamage; // for shadowmeld potion
    private final HashMap<UUID, ItemStack> chatters; // for listening to player chat

    /**
     * When plugin is loaded, add hunter items to hash set for use later
     */
    public HunterListener() {
        cloakers = new HashSet<>();
        hasDealtDamage = new HashSet<>();
        chatters = new HashMap<>();
    }

    @EventHandler
    public void onCharacterLoad(CharacterLoadEvent event) {
        if (!event.getPlayerCache().getProfName().equals("Hunter")) return;
        this.registerHunter(event.getPlayer());
    }

    @EventHandler
    public void onCacheSave(CacheSaveEvent event) {
        Map<UUID, HunterPlayer> hunters = RunicProfessions.getHunterCache().getPlayers();
        UUID uuid = event.getPlayer().getUniqueId();
        if (!hunters.containsKey(uuid)) return;
        hunters.get(uuid).save(event.getMongoData());
        if (event.cacheSaveReason() == CacheSaveReason.LOGOUT)
            hunters.remove(uuid);
    }

    @EventHandler
    public void onProfessionChange(ProfessionChangeEvent event) {
        if (event.getProfession() == Profession.HUNTER)
            this.registerHunter(event.getPlayer());
    }

    @EventHandler
    public void onHunterMobDeath(MythicMobDeathEvent e) {
        // verify that a hunter is on-task
        if (!(e.getKiller() instanceof Player)) return;
        Player player = (Player) e.getKiller();
        if (!RunicProfessions.getHunterCache().getPlayers().containsKey(player.getUniqueId())) return;
        String mobInternal = e.getMobType().getInternalName();
        HunterPlayer hunterPlayer = RunicProfessions.getHunterCache().getPlayers().get(player.getUniqueId());
        if (hunterPlayer.getTask() == null) return;
        if (!mobInternal.equals(hunterPlayer.getTask().getInternalName())) return;
        hunterPlayer.addKill();
    }

    /*
    Teleport scrolls are handled in core.
     */
    @EventHandler
    public void onHunterItemUse(RunicItemGenericTriggerEvent e) {
        if (!isHunterItem(e.getItem().getTemplateId())) return;
        String templateID = e.getItem().getTemplateId();
        Player player = e.getPlayer();
        if (RunicCoreAPI.isInCombat(player)) {
            player.sendMessage(ChatColor.RED + "You can't use that in combat!");
            return;
        }
        if (templateID.equals(HunterItems.SCRYING_ORB.getTemplateId())) {
            ItemRemover.takeItem(player, e.getItemStack(), 1);
            player.sendMessage(ChatColor.YELLOW + "Enter a player name in the chat.");
            chatters.put(player.getUniqueId(), HunterItems.SCRYING_ORB_ITEMSTACK);
        } else if (templateID.equals(HunterItems.SHADOWMELD_POTION.getTemplateId())) {
            ItemRemover.takeItem(player, e.getItemStack(), 1);
            shadowmeld(player);
        } else if (templateID.equals(HunterItems.TRACKING_SCROLL.getTemplateId())) {
            ItemRemover.takeItem(player, e.getItemStack(), 1);
            player.sendMessage(ChatColor.YELLOW + "Enter a player name in the chat.");
            chatters.put(player.getUniqueId(), HunterItems.TRACKING_SCROLL_ITEMSTACK);
        } else if (templateID.equals(HunterItems.TRACKING_COMPASS.getTemplateId())) {
            player.sendMessage(ChatColor.YELLOW + "Enter a player name in the chat.");
            chatters.put(player.getUniqueId(), HunterItems.TRACKING_COMPASS_ITEMSTACK);
        }
    }

    private boolean isHunterItem(String templateID) {
        return templateID.equals(HunterItems.SCRYING_ORB.getTemplateId())
                || templateID.equals(HunterItems.SHADOWMELD_POTION.getTemplateId())
                || templateID.equals(HunterItems.TELEPORT_OUTLAW_GUILD.getTemplateId())
                || templateID.equals(HunterItems.TRACKING_SCROLL.getTemplateId())
                || templateID.equals(HunterItems.TRACKING_COMPASS.getTemplateId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(ChatChannelMessageEvent e) {
        if (!chatters.containsKey(e.getMessageSender().getUniqueId())) return;
        e.setCancelled(true);
        Player player = e.getMessageSender();
        Player toLookup;

        if (Bukkit.getPlayer(e.getChatMessage()) == null) {
            player.sendMessage(ChatColor.RED + "You must enter a valid player.");
            return;
        } else {
            toLookup = Bukkit.getPlayer(e.getChatMessage());
        }

        if (toLookup == null) {
            player.sendMessage(ChatColor.RED + "Error: player not found");
            return;
        }

        if (chatters.get(player.getUniqueId()).isSimilar(HunterItems.SCRYING_ORB_ITEMSTACK)) {
            lookupStats(player, toLookup);
        } else if (chatters.get(player.getUniqueId()).isSimilar(HunterItems.TRACKING_SCROLL_ITEMSTACK)) {
            lookupLocation(player, toLookup);
        } else if (chatters.get(player.getUniqueId()).isSimilar(HunterItems.TRACKING_COMPASS_ITEMSTACK)) {
            lookupLocation(player, toLookup);
        }

        chatters.remove(player.getUniqueId());
    }

    private void lookupStats(Player player, Player toLookup) {
        int maxHealth = (int) toLookup.getMaxHealth();
        int dexterity = RunicCoreAPI.getPlayerDexterity(player.getUniqueId());
        int intelligence = RunicCoreAPI.getPlayerIntelligence(player.getUniqueId());
        int strength = RunicCoreAPI.getPlayerStrength(player.getUniqueId());
        int vitality = RunicCoreAPI.getPlayerVitality(player.getUniqueId());
        int wisdom = RunicCoreAPI.getPlayerWisdom(player.getUniqueId());
        player.sendMessage
                (ChatColor.translateAlternateColorCodes('&', "&e" + toLookup.getName() + "'s Character Stats:" +
                        "\n&c❤ (Health): " + maxHealth +
                        "\n&e✦ (DEX): " + dexterity +
                        "\n&3ʔ (INT): " + intelligence +
                        "\n&c⚔ (STR): " + strength +
                        "\n&f■ (VIT): " + vitality +
                        "\n&a✸ (WIS): " + wisdom));
    }

    private void lookupLocation(Player pl, Player toLookup) {
        String name = toLookup.getName();
        Location loc = toLookup.getLocation();
        pl.sendMessage(ChatColor.YELLOW + name + " is in world - " + loc.getWorld().getName());
        pl.sendMessage(ChatColor.YELLOW + name + " can be found at: " + (int) loc.getX() + "x, " + (int) loc.getY() + "y, " + (int) loc.getZ() + "z");
    }

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
                    // hide the player, prevent them from disappearing in tab
                    PacketPlayOutPlayerInfo packet =
                            new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                                    ((CraftPlayer) pl).getHandle());
                    for (Player ps : RunicCore.getCacheManager().getLoadedPlayers()) {
                        ps.hidePlayer(RunicProfessions.getInstance(), pl);
                        ((CraftPlayer) ps).getHandle().playerConnection.sendPacket(packet);
                    }
                    cloakers.add(pl.getUniqueId());
                    pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5f, 0.5f);
                    pl.getWorld().spawnParticle(Particle.REDSTONE, pl.getEyeLocation(), 25, 0.5f, 0.5f, 0.5f,
                            new Particle.DustOptions(Color.BLACK, 3));
                    pl.sendMessage(ChatColor.GRAY + "You vanished!");
                    return;
                }

                pl.getWorld().spawnParticle(Particle.REDSTONE, pl.getLocation().add(0, 1, 0),
                        10, 0.5f, 0.5f, 0.5f, new Particle.DustOptions(Color.GRAY, 1));

                pl.sendMessage(ChatColor.GRAY + "Fading into shadow... "
                        + ChatColor.WHITE + ChatColor.BOLD + (5 - count) + "s");
                count = count + 1;

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
                        ps.showPlayer(RunicProfessions.getInstance(), pl);
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
    public void onSpellDamage(SpellDamageEvent e) {
        if (!(cloakers.contains(e.getPlayer().getUniqueId())
                || cloakers.contains(e.getVictim().getUniqueId()))) return;
        if (cloakers.contains(e.getPlayer().getUniqueId()))
            hasDealtDamage.add(e.getPlayer().getUniqueId());
        else
            hasDealtDamage.add(e.getVictim().getUniqueId());
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        if (!(cloakers.contains(e.getPlayer().getUniqueId())
                || cloakers.contains(e.getVictim().getUniqueId()))) return;
        if (cloakers.contains(e.getPlayer().getUniqueId()))
            hasDealtDamage.add(e.getPlayer().getUniqueId());
        else
            hasDealtDamage.add(e.getVictim().getUniqueId());
    }

    /**
     * A method used to register a player into the hunter cache
     *
     * @param player the player about to be registered
     */
    private void registerHunter(Player player) {
        PlayerCache playerCache = RunicCoreAPI.getPlayerCache(player);
        int slot = playerCache.getCharacterSlot();
        PlayerMongoData playerData = (PlayerMongoData) playerCache.getMongoData();
        PlayerMongoDataSection data = playerData.getCharacter(slot);
        UUID uuid = player.getUniqueId();

        int hunterPoints;
        int hunterKills;
        int maxHunterKills;
        TaskMobs mob;

        String hunterPointsKey = "prof.hunter_points";
        if (data.has(hunterPointsKey)) {
            hunterPoints = data.get(hunterPointsKey, Integer.class);
        } else {
            hunterPoints = 0;
        }

        String hunterKillsKey = "prof.hunter_kills";
        if (data.has(hunterKillsKey)) {
            hunterKills = data.get(hunterKillsKey, Integer.class);
        } else {
            hunterKills = 0;
        }


        String hunterMaxKillsKey = "prof.hunter_kills_max";
        if (data.has(hunterMaxKillsKey)) {
            maxHunterKills = data.get(hunterMaxKillsKey, Integer.class);
        } else {
            maxHunterKills = 0;
        }

        String hunterMobKey = "prof.hunter_mob";
        if (data.has(hunterMobKey)) {
            String mobName = data.get(hunterMobKey, String.class);
            if (mobName != null) {
                mob = (!mobName.equals("null")) ? TaskMobs.valueOf(mobName) : null;
            } else {
                mob = null;
            }
        } else {
            mob = null;
        }

        RunicProfessions.getHunterCache().getPlayers().put(uuid, new HunterPlayer(player, hunterPoints, hunterKills, maxHunterKills, mob));
    }
}
