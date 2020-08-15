package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.runicrestart.api.RunicRestartApi;
import com.runicrealms.runicrestart.api.ServerShutdownEvent;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProfManager implements Listener {

    // globals
    private final HashMap<Player, Workstation> workstations;
    private final ArrayList<Player> currentCrafters;
    private final ConcurrentHashMap<Location, Material> blocksToRestore;

    // constructor
    public ProfManager() {
        this.workstations = new HashMap<>();
        currentCrafters = new ArrayList<>();
        blocksToRestore = new ConcurrentHashMap<>();
        this.startRegenTask();
        RunicProfessions.getInstance().getServer().getPluginManager().registerEvents(this, RunicProfessions.getInstance());
    }

    @EventHandler
    public void onShutdown(ServerShutdownEvent e) {
        regenGatheringNodes();
        RunicProfessions.getInstance().getLogger().info(" §cRunicProfessions has been disabled.");
        RunicRestartApi.markPluginSaved("professions");
    }

    /*
     Starts the repeating task to regenerate farms, ores, trees, every 30 seconds
     Cannot set blocks async, so MUST be sync
     */
    private void startRegenTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                regenGatheringNodes();
            }
        }.runTaskTimer(RunicProfessions.getInstance(), 20, 600);
    }

    /*
     Grabs a list of locations and materials from memory, sets blocks to that material
     */
    private void regenGatheringNodes() {
        for (Location loc : blocksToRestore.keySet()) {
            if (loc.getChunk().isLoaded()) {
                Material type = blocksToRestore.get(loc);
                loc.getBlock().setType(type);
                if (type == Material.WHEAT || type == Material.CARROTS || type == Material.POTATOES) {
                    BlockState state = loc.getBlock().getState();
                    state.setRawData(CropState.RIPE.getData());
                    state.update();
                }
                loc.getBlock().getWorld().spawnParticle(Particle.VILLAGER_HAPPY,
                        loc.getBlock().getLocation().add(0.5, 0, 0.5), 25, 0.5, 0.5, 0.5, 0.01);
                blocksToRestore.remove(loc);
            }
        }
    }

    public ArrayList<Player> getCurrentCrafters() {
        return currentCrafters;
    }
    public ConcurrentHashMap<Location, Material> getBlocksToRestore() {
        return blocksToRestore;
    }
    public Workstation getPlayerWorkstation(Player pl) {
        return this.workstations.get(pl);
    }
    public void setPlayerWorkstation(Player pl, Workstation station) {
        this.workstations.put(pl, station);
    }
}
