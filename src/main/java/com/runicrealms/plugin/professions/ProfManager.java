package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.common.RunicCommon;
import com.runicrealms.plugin.professions.api.ProfessionsAPI;
import com.runicrealms.plugin.professions.event.ProfessionChangeEvent;
import com.runicrealms.plugin.professions.gathering.GatheringGUI;
import com.runicrealms.plugin.professions.gathering.GatheringRegion;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.gathering.GatheringSkillGUI;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.runicrestart.event.ServerShutdownEvent;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfManager implements Listener, ProfessionsAPI {
    private final HashMap<Player, Workstation> workstations;
    private final ArrayList<Player> currentCrafters;
    private final ConcurrentHashMap<Location, Material> blocksToRestore;
    private final HashMap<Location, String> storedStationLocations;

    public ProfManager() {
        this.workstations = new HashMap<>();
        currentCrafters = new ArrayList<>();
        blocksToRestore = new ConcurrentHashMap<>();
        Bukkit.getScheduler().runTaskTimer(RunicProfessions.getInstance(), this::regenGatheringNodes, 20, 600); //cannot set blocks async so must be sync

        // Load workstations
        storedStationLocations = new HashMap<>();

        // retrieve the data file
        File workstations = new File(Bukkit.getServer().getPluginManager().getPlugin("RunicProfessions").getDataFolder(),
                "workstations.yml");
        FileConfiguration stationConfig = YamlConfiguration.loadConfiguration(workstations);
        ConfigurationSection stationLocs = stationConfig.getConfigurationSection("Workstations.Locations");

        if (stationLocs == null) return;

        /*
        Iterate through all workstations and add them to memory
         */
        for (String stationID : stationLocs.getKeys(false)) {
            World savedWorld = Bukkit.getServer().getWorld(stationLocs.getString(stationID + ".world"));
            double savedX = stationLocs.getDouble(stationID + ".x");
            double savedY = stationLocs.getDouble(stationID + ".y");
            double savedZ = stationLocs.getDouble(stationID + ".z");
            Location stationLocation = new Location(savedWorld, savedX, savedY, savedZ);
            String stationType = stationLocs.getString(stationID + ".type");
            storedStationLocations.put(stationLocation, stationType);
        }
        loadWorkstationHolograms();
        // Load event listeners
        RunicProfessions.getInstance().getServer().getPluginManager().registerEvents(this, RunicProfessions.getInstance());
    }

    private void loadWorkstationHolograms() {
        for (Location location : storedStationLocations.keySet()) {
            Hologram hologram = HolographicDisplaysAPI.get(RunicProfessions.getInstance()).createHologram(location.clone().add(0.5, 2, 0.5));
            hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
            String typeName = storedStationLocations.get(location);
            WorkstationType workstationType = WorkstationType.getFromName(typeName);
            if (workstationType == null) {
                workstationType = WorkstationType.ANVIL; // Default
            }
            hologram.getLines().appendText(ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + workstationType.getName());
            hologram.getLines().appendText(ChatColor.GRAY + "Workstation");
        }
    }

    @Override
    public void changePlayerProfession(Player player, Profession profession) {
        Bukkit.getServer().getPluginManager().callEvent(new ProfessionChangeEvent(player, profession));
    }

    @Override
    public int determineCurrentGatheringLevel(UUID uuid, GatheringSkill gatheringSkill) {
        GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(uuid);
        return switch (gatheringSkill) {
            case COOKING -> gatheringData.getCookingLevel();
            case FARMING -> gatheringData.getFarmingLevel();
            case FISHING -> gatheringData.getFishingLevel();
            case HARVESTING -> gatheringData.getHarvestingLevel();
            case MINING -> gatheringData.getMiningLevel();
            case WOODCUTTING -> gatheringData.getWoodcuttingLevel();
        };
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
        RunicCommon.getQuestsAPI().triggerQuest(false, player, "gathering-menu", null);
    }

    @Override
    public void openGatheringSkillGUI(Player player, GatheringSkill gatheringSkill) {
        player.openInventory(new GatheringSkillGUI(player, gatheringSkill).getInventory());
    }

    @Override
    public void setPlayerWorkstation(Player player, Workstation station) {
        this.workstations.put(player, station);
    }

    @Override
    public Map<Location, String> getStoredStationLocations() {
        return storedStationLocations;
    }

    @Override
    public void removeWorkstation(Location location) {
        storedStationLocations.remove(location); // remove workstation from memory
        try {
            File workstations = new File(Bukkit.getServer().getPluginManager().getPlugin("RunicProfessions").getDataFolder(),
                    "workstations.yml");
            FileConfiguration stationConfig = YamlConfiguration.loadConfiguration(workstations);
            ConfigurationSection stationLocs = stationConfig.getConfigurationSection("Workstations.Locations");

            if (stationLocs == null) return;

            /*
            Iterate through all workstations and add them to memory
             */
            for (String stationID : stationLocs.getKeys(false)) {
                World savedWorld = Bukkit.getServer().getWorld(stationLocs.getString(stationID + ".world"));
                double savedX = stationLocs.getDouble(stationID + ".x");
                double savedY = stationLocs.getDouble(stationID + ".y");
                double savedZ = stationLocs.getDouble(stationID + ".z");
                Location stationLocation = new Location(savedWorld, savedX, savedY, savedZ);
                if (stationLocation.equals(location)) {
                    stationLocs.set(stationID, null);
                }
            }

            stationConfig.save(workstations);
        } catch (NullPointerException e) {
            Bukkit.getServer().getLogger().info(ChatColor.RED + "Error: there was an error removing workstation!");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onShutdown(ServerShutdownEvent event) {
        regenGatheringNodes();
        RunicProfessions.getInstance().getLogger().info(" §cRunicProfessions has been disabled.");
    }

    /**
     * Grabs a list of locations and materials from memory, sets blocks to that material
     */
    private void regenGatheringNodes() {
        for (Location loc : blocksToRestore.keySet()) {
            Material type = blocksToRestore.get(loc);
            loc.getBlock().setType(type);

            if (loc.getBlock().getBlockData() instanceof Ageable ageable) {
                ageable.setAge(ageable.getMaximumAge());
                loc.getBlock().setBlockData(ageable);
            }

            loc.getBlock().getWorld().spawnParticle(Particle.VILLAGER_HAPPY,
                    loc.getBlock().getLocation().add(0.5, 0, 0.5), 25, 0.5, 0.5, 0.5, 0.01);
            blocksToRestore.remove(loc);
        }
    }
}
