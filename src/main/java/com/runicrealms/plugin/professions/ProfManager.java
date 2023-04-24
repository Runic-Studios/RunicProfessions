package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.api.ProfessionsAPI;
import com.runicrealms.plugin.professions.event.ProfessionChangeEvent;
import com.runicrealms.plugin.professions.gathering.GatheringGUI;
import com.runicrealms.plugin.professions.gathering.GatheringRegion;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.gathering.GatheringSkillGUI;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.runicrestart.event.ServerShutdownEvent;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfManager implements Listener, ProfessionsAPI {
    private final HashMap<Player, Workstation> workstations;
    private final ArrayList<Player> currentCrafters;
    private final ConcurrentHashMap<Location, Material> blocksToRestore;

    public ProfManager() {
        this.workstations = new HashMap<>();
        currentCrafters = new ArrayList<>();
        blocksToRestore = new ConcurrentHashMap<>();
        this.startRegenTask();
        RunicProfessions.getInstance().getServer().getPluginManager().registerEvents(this, RunicProfessions.getInstance());
    }

    @Override
    public void changePlayerProfession(Player player, Profession profession) {
        Bukkit.getServer().getPluginManager().callEvent(new ProfessionChangeEvent(player, profession));
    }

    @Override
    public int determineCurrentGatheringLevel(UUID uuid, GatheringSkill gatheringSkill) {
        GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(uuid);
        switch (gatheringSkill) {
            case COOKING:
                return gatheringData.getCookingLevel();
            case FARMING:
                return gatheringData.getFarmingLevel();
            case FISHING:
                return gatheringData.getFishingLevel();
            case HARVESTING:
                return gatheringData.getHarvestingLevel();
            case MINING:
                return gatheringData.getMiningLevel();
            case WOODCUTTING:
                return gatheringData.getWoodcuttingLevel();
            default:
                return 0;
        }
    }

    @Override
    public ConcurrentHashMap<Location, Material> getBlocksToRestore() {
        return blocksToRestore;
    }

    @Override
    public ArrayList<Player> getCurrentCrafters() {
        return currentCrafters;
    }

    @Override
    public Profession getPlayerProfession(UUID uuid, int slot) {
        return Profession.getFromName(RunicProfessions.getDataAPI().loadCraftingData(uuid, slot).getProfName());
    }

    @Override
    public int getPlayerProfessionExp(UUID uuid, int slot) {
        return RunicProfessions.getDataAPI().loadCraftingData(uuid, slot).getProfExp();
    }

    @Override
    public int getPlayerProfessionLevel(UUID uuid, int slot) {
        return RunicProfessions.getDataAPI().loadCraftingData(uuid, slot).getProfLevel();
    }

    public Workstation getPlayerWorkstation(Player player) {
        return this.workstations.get(player);
    }

    @Override
    public boolean isInGatheringRegion(GatheringRegion gatheringRegion, Location location) {
        return RunicCore.getRegionAPI().containsRegion(location, gatheringRegion.getIdentifier());
    }

    @Override
    public void openGatheringGUI(Player player) {
        player.openInventory(new GatheringGUI(player).getInventory());
    }

    @Override
    public void openGatheringSkillGUI(Player player, GatheringSkill gatheringSkill) {
        player.openInventory(new GatheringSkillGUI(player, gatheringSkill).getInventory());
    }

    @Override
    public void setPlayerWorkstation(Player player, Workstation station) {
        this.workstations.put(player, station);
    }

    @EventHandler
    public void onShutdown(ServerShutdownEvent event) {
        regenGatheringNodes();
        RunicProfessions.getInstance().getLogger().info(" §cRunicProfessions has been disabled.");
    }

    /**
     * Grabs a list of locations and materials from memory, sets blocks to that material
     */
    @SuppressWarnings("deprecation")
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

    /**
     * Starts the repeating task to regenerate farms, ores, trees, every 30 seconds
     * Cannot set blocks async, so MUST be sync
     */
    private void startRegenTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                regenGatheringNodes();
            }
        }.runTaskTimer(RunicProfessions.getInstance(), 20, 600);
    }
}
