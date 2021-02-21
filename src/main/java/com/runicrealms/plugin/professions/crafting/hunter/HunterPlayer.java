package com.runicrealms.plugin.professions.crafting.hunter;

import org.bukkit.entity.Player;

public class HunterPlayer {
    private final Player player;
    private HunterTask.HunterMob task;

    public HunterPlayer(Player player) {
        this.player = player;
        this.task = null;
    }

    public void save() {
        //save all data in config
    }

    public Player getPlayer() {
        return this.player;
    }

    public HunterTask.HunterMob getTask() {
        return this.task;
    }

    public void setTask(HunterTask.HunterMob task) {
        this.task = task;
    }
}
