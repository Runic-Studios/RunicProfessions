package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicProfessions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

        //gen new task
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

        this.hunterKills++;

        //add logic to check if its enough to complete task
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
        return this.player.getUniqueId().toString() + ".info." + field;
    }
}
