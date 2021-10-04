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
import org.bukkit.Bukkit;
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

/**
 * Wrapper for players with the hunter profession
 */
public class HunterPlayer {

    private final PlayerCache playerCache;
    private int hunterPoints;
    private int hunterKills;
    private int maxHunterKills;
    private TaskMobs task;

    public HunterPlayer(Player player, int hunterPoints, int hunterKills, int maxHunterKills, TaskMobs task) {
        this.playerCache = RunicCoreAPI.getPlayerCache(player);
        this.hunterPoints = hunterPoints;
        this.hunterKills = hunterKills;
        this.maxHunterKills = maxHunterKills;
        this.task = task;
    }

    /**
     * ALWAYS ONLY CALL ON CACHE SAVE EVENT
     *
     * @param playerData the player data
     */
    public void save(PlayerMongoData playerData) {
        int slot = this.playerCache.getCharacterSlot();
        PlayerMongoDataSection data = playerData.getCharacter(slot);

        if (this.task != null) {
            data.set("prof.hunter_mob", this.task.name());
        } else {
            data.set("prof.hunter_mob", "null");
        }

        data.set("prof.hunter_points", this.hunterPoints);
        data.set("prof.hunter_kills", this.hunterKills);
        data.set("prof.hunter_kills_max", this.maxHunterKills);
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

        this.getPlayer().sendMessage(ColorUtil.format("&r&aHunter mob slain!"));

        ProfExpUtil.giveCraftingExperience(this.getPlayer(), this.task.getExperience(), true);

        this.hunterKills++;

        if (this.hunterKills < this.maxHunterKills) {
            return;
        }

        this.hunterPoints += this.task.getPoints();

        this.getPlayer().sendMessage
                (ChatColor.GREEN + "You have completed your hunter task and receive " +
                        ChatColor.GOLD + ChatColor.BOLD + this.task.getPoints() + " points!" +
                        ChatColor.GREEN + " Return to a hunting board for another task.");
        this.launchFirework(this.getPlayer());
        this.resetTask();
    }

    public Player getPlayer() {
        try {
            return Bukkit.getPlayer(this.getPlayerCache().getPlayerID());
        } catch (NullPointerException e) {
            Bukkit.getLogger().info(ChatColor.DARK_RED + "Player cache for hunter player was not found!");
            return null;
        }
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
            this.getPlayer().closeInventory();
            this.getPlayer().sendMessage(ColorUtil.format("&r&cYour hunter level is too low to accept any tasks from this area!"));
            return;
        }

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int index = rand.nextInt(hunterMobs.size());
        TaskMobs mob = hunterMobs.get(index);

        this.setTask(mob, 0, rand.nextInt(15, 30 + 1)); //+1 because the method is dumb and gens an index

        this.getPlayer().sendMessage(ColorUtil.format("&r&aYour target is: &r&f" + this.task.getName()));
        this.getPlayer().sendMessage(ColorUtil.format("&r&aYou must hunt it &r&f" + this.maxHunterKills + " &r&atimes!"));
    }

    private List<String> getRegions() {
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(this.getPlayer().getLocation()));
        List<String> names = new ArrayList<>();

        regions.forEach(region -> names.add(region.getId()));

        return names;
    }

    private boolean containsRegion(TaskMobs mob, List<String> names) {
        for (String name : names) {
            if (mob.getRegion().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
