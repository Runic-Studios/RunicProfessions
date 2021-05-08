package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.database.PlayerMongoData;
import com.runicrealms.plugin.database.PlayerMongoDataSection;
import com.runicrealms.plugin.player.cache.PlayerCache;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.utilities.ColorUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HunterPlayer {
    private final Player player;
    private final PlayerCache playerCache;
    private int hunterPoints;
    private int hunterKills;
    private int maxHunterKills;
    private TaskMobs task;

    public HunterPlayer(Player player, int hunterPoints, int hunterKills, int maxHunterKills, TaskMobs task) {
        this.player = player;
        this.playerCache = RunicCoreAPI.getPlayerCache(player);
        this.hunterPoints = hunterPoints;
        this.hunterKills = hunterKills;
        this.maxHunterKills = maxHunterKills;
        this.task = task;
    }

    public void save(PlayerMongoData playerData) {
        int slot = this.playerCache.getCharacterSlot();
        PlayerMongoDataSection data = playerData.getCharacter(slot);

        if (this.task != null) {
            data.set(formatData(slot, "hunter_mob"), this.task.name());
        } else {
            data.set(formatData(slot, "hunter_mob"), "null");
        }

        data.set(formatData(slot, "hunter_points"), this.hunterPoints);
        data.set(formatData(slot, "hunter_kills"), this.hunterKills);
        data.set(formatData(slot, "hunter_kills_max"), this.maxHunterKills);
    }

    public void newTask() {
        if (this.task != null) {
            return;
        }

        this.chooseNewRandomMob();
    }

    public void resetTask() {
        this.hunterKills = 0;
        this.maxHunterKills = 0;
        this.task = null;
    }

    public void addKill() {
        if (this.task == null) {
            return;
        }

        this.player.sendMessage(ColorUtil.format("&r&aHunter mob slain!"));

        ProfExpUtil.giveExperience(this.player, this.task.getExperience(), true);

        this.hunterKills++;

        if (this.hunterKills < this.maxHunterKills) {
            return;
        }

        this.hunterPoints += this.task.getPoints();

        this.player.sendMessage
                (ChatColor.GREEN + "You have completed your hunter task and receive " +
                        ChatColor.GOLD + ChatColor.BOLD + this.task.getPoints() + " points!" +
                        ChatColor.GREEN + " Return to a hunting board for another task.");
        this.launchFirework(this.player);
        this.resetTask();
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerCache getPlayerCache() {
        return this.playerCache;
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

    @Nullable
    public TaskMobs getTask() {
        return this.task;
    }

    public void setTask(TaskMobs task, int hunterKills, int maxHunterKills) {
        this.task = task;
        this.hunterKills = hunterKills;
        this.maxHunterKills = maxHunterKills;
    }

    private void launchFirework(Player pl) {
        Firework firework = pl.getWorld().spawn(pl.getEyeLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(0);
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).build());
        firework.setFireworkMeta(meta);
    }

    private void chooseNewRandomMob() {
        List<TaskMobs> hunterMobs = new ArrayList<>();
        List<String> names = this.getRegions();

        // filter-out mobs above the player's hunter level and region
        int playLv = this.playerCache.getProfLevel();
        for (TaskMobs mob : TaskMobs.values()) {
            int mobLv = mob.getLevel();
            if (mobLv > playLv) {
                continue;
            }

            if (!this.containsRegion(mob, names)) {
                continue;
            }

            hunterMobs.add(mob);
        }

        if (hunterMobs.isEmpty()) {
            this.player.closeInventory();
            this.player.sendMessage(ColorUtil.format("&r&cYou are not high enough level to accept any tasks from this area!"));
            return;
        }

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int index = rand.nextInt(hunterMobs.size());
        TaskMobs mob = hunterMobs.get(index);

        this.setTask(mob, 0, rand.nextInt(15, 30));

        this.player.sendMessage(ColorUtil.format("&r&aYour target is: &r&f" + this.task.getName()));
        this.player.sendMessage(ColorUtil.format("&r&aPlease defeat it &r&f" + this.maxHunterKills + " &r&atimes"));
    }

    private List<String> getRegions() {
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(this.player.getLocation()));
        List<String> names = new ArrayList<>();

        regions.forEach(region -> names.add(region.getId()));

        return names;
    }

    private boolean containsRegion(TaskMobs mob, List<String> names) {
        for (String name : names) {
            if (mob.getRegions().contains(name)) {
                return true;
            }
        }
        return false;
    }

    public static String formatData(int slot, String field) {
        return slot + ".prof." + field;
    }
}