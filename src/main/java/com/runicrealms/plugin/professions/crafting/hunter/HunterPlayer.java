package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class HunterPlayer {
    private final Player player;
    private int hunterPoints;
    private int hunterKills;
    private int maxHunterKills;
    private HunterTask.HunterMob task;

    public HunterPlayer(Player player) {
        this.player = player;
        this.hunterPoints = 0;
        this.hunterKills = 0;
        this.maxHunterKills = 0;
        this.task = null;
    }

    public HunterPlayer(Player player, int hunterPoints, int hunterKills, int maxHunterKills, HunterTask.HunterMob task) {
        this.player = player;
        this.hunterPoints = hunterPoints;
        this.hunterKills = hunterKills;
        this.maxHunterKills = maxHunterKills;
        this.task = task;
    }

    public void save(boolean write) {
        Plugin plugin = RunicProfessions.getInstance();
        FileConfiguration config = plugin.getConfig();

        if (this.task != null) {
            config.set(this.formatData("hunter_mob"), this.task.name());
        } else {
            config.set(this.formatData("hunter_mob"), "null");
        }

        config.set(this.formatData("hunter_points"), this.hunterPoints);
        config.set(this.formatData("hunter_kills"), this.hunterKills);
        config.set(this.formatData("hunter_kills_max"), this.maxHunterKills);

        if (write) {
            plugin.saveConfig();
        }
    }

    public void newTask() {
        if (this.task != null) {
            return;
        }

        this.getRandomMob();
    }

    public void resetTask() {
        this.hunterKills = 0;
        this.maxHunterKills = 0;
        this.task = null;
    }

    public boolean addKill() {
        if (this.task == null) {
            return false;
        }

        this.hunterKills++;

        if (this.hunterKills < this.maxHunterKills) {
            return false;
        }

        HunterTask.givePoints(this.player);
        HunterTask.giveExperience(this.player, false);
        this.player.sendMessage
                (ChatColor.GREEN + "You have completed your hunter task and receive " +
                        ChatColor.GOLD + ChatColor.BOLD + HunterTask.getEarnedPoints(this.player) + " points!" +
                        ChatColor.GREEN + " Return to a hunting board for another task.");
        this.launchFirework(this.player);
        this.save(true);
        return true;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getHunterPoints() {
        return this.hunterPoints;
    }

    public void setHunterPoints(int hunterPoints) {
        this.hunterPoints = hunterPoints;
    }

    public int getHunterKills() {
        return this.hunterKills;
    }

    public int getMaxHunterKills() {
        return this.maxHunterKills;
    }

    public HunterTask.HunterMob getTask() {
        return this.task;
    }

    public void setTask(HunterTask.HunterMob task) {
        this.task = task;
    }

    private String formatData(String field) {
        return HunterPlayer.formatData(this.player.getUniqueId(), field);
    }

    private void launchFirework(Player pl) {
        Firework firework = pl.getWorld().spawn(pl.getEyeLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(0);
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).build());
        firework.setFireworkMeta(meta);
    }

    private HunterTask.HunterMob getRandomMob() {
        List<HunterTask.HunterMob> hunterMobs = new ArrayList<>();
        List<String> names = this.getRegions();

        // filter-out mobs above the player's hunter level and region
        int playLv = RunicCoreAPI.getPlayerCache(this.player).getProfLevel();
        for (HunterTask.HunterMob mob : HunterTask.HunterMob.values()) {
            int mobLv = mob.getLevel();
            if (mobLv > playLv) {
                continue;
            }

            if (!this.containsRegion(mob, names)) {
                continue;
            }

            hunterMobs.add(mob);
        }

        Random rand = ThreadLocalRandom.current();
        int index = rand.nextInt(hunterMobs.size());
        HunterTask.HunterMob mob = hunterMobs.get(index);

        // set mob as task in config
        this.setTask(mob);
        return mob;
    }

    private List<String> getRegions() {
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(this.player.getLocation()));
        List<String> names = new ArrayList<>();

        regions.forEach(region -> names.add(region.getId()));

        return names;
    }

    private boolean containsRegion(HunterTask.HunterMob mob, List<String> names) {
        for (String name : names) {
            if (mob.getRegions().contains(name)) {
                return true;
            }
        }
        return false;
    }

    public static String formatData(UUID uuid, String field) {
        return uuid.toString() + ".info.prof." + field;
    }
}
