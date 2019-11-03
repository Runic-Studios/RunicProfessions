package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.RunicProfessions;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ProfManager {

    // globals
    private RunicProfessions plugin = RunicProfessions.getInstance();
    private HashMap<Player, Workstation> workstations;
    private ArrayList<Player> currentCrafters;

    // constructor
    public ProfManager() {
        this.workstations = new HashMap<>();
        currentCrafters = new ArrayList<>();
        this.startRegenTask();
    }

    // starts the repeating task to regenerate farms, ores, trees, every 30 seconds
    // cannot set blocks async, so MUST be sync
    private void startRegenTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                regenFarms();
                regenLogs();
                regenOres();
            }
        }.runTaskTimer(this.plugin, 20, 600);
    }

    // grabs a list of locations and materials from file, sets blocks to that material
    private void regenFarms() {

        File regenBlocks = new File(Bukkit.getServer().getPluginManager().getPlugin("RunicProfessions").getDataFolder(),
                "regen_blocks.yml");
        FileConfiguration blockLocations = YamlConfiguration.loadConfiguration(regenBlocks);
        ConfigurationSection logs = blockLocations.getConfigurationSection("Alterra.FARMS");

        if (regenBlock(logs)) return;

        // clear the data, update the file
        blockLocations.set("Alterra.NEXT_ID_FARMS", 0);
        blockLocations.set("Alterra.FARMS", null);
        try {
            blockLocations.save(regenBlocks);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void regenLogs() {

        File regenBlocks = new File(Bukkit.getServer().getPluginManager().getPlugin("RunicProfessions").getDataFolder(),
                "regen_blocks.yml");
        FileConfiguration blockLocations = YamlConfiguration.loadConfiguration(regenBlocks);
        ConfigurationSection logs = blockLocations.getConfigurationSection("Alterra.LOGS");

        if (regenBlock(logs)) return;

        // clear the data, update the file
        blockLocations.set("Alterra.NEXT_ID_LOGS", 0);
        blockLocations.set("Alterra.LOGS", null);
        try {
            blockLocations.save(regenBlocks);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void regenOres() {

        File regenBlocks = new File(Bukkit.getServer().getPluginManager().getPlugin("RunicProfessions").getDataFolder(),
                "regen_blocks.yml");
        FileConfiguration blockLocations = YamlConfiguration.loadConfiguration(regenBlocks);
        ConfigurationSection ores = blockLocations.getConfigurationSection("Alterra.ORES");

        if (regenBlock(ores)) return;

        // clear the data, update the file
        blockLocations.set("Alterra.NEXT_ID_ORES", 0);
        blockLocations.set("Alterra.ORES", null);
        try {
            blockLocations.save(regenBlocks);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean regenBlock(ConfigurationSection configSection) {
        if (configSection == null) return true;

        for (String id : configSection.getKeys(false)) {
            Material material = Material.getMaterial(configSection.get(id + ".type").toString());
            double x = configSection.getDouble(id + ".x");
            double y = configSection.getDouble(id + ".y");
            double z = configSection.getDouble(id + ".z");
            Location loc = new Location(Bukkit.getWorld("Alterra"), x, y, z);
            loc.getBlock().setType(material);
            Material type = loc.getBlock().getType();
            // make the crops fully grown
            if (type == Material.WHEAT || type == Material.CARROTS || type == Material.POTATOES) {
                BlockState state = loc.getBlock().getState();
                state.setRawData(CropState.RIPE.getData());
                state.update();
            }
            loc.getBlock().getWorld().spawnParticle(Particle.VILLAGER_HAPPY,
                    loc.getBlock().getLocation().add(0.5, 0, 0.5), 25, 0.5, 0.5, 0.5, 0.01);
        }
        return false;
    }

    public ArrayList<Player> getCurrentCrafters() {
        return currentCrafters;
    }
    public Workstation getPlayerWorkstation(Player pl) {
        return this.workstations.get(pl);
    }
    public void setPlayerWorkstation(Player pl, Workstation station) {
        this.workstations.put(pl, station);
    }
}
