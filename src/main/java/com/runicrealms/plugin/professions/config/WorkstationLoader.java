package com.runicrealms.plugin.professions.config;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.WorkstationType;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.plugin.professions.exception.WorkstationLoadException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class WorkstationLoader {

    private final static Map<WorkstationType, List<CraftedResource>> CRAFTED_RESOURCES;
    private static final Map<WorkstationType, Integer> MAX_PAGES;

    /*
    Static block to load our crafted resources lists into memory from file storage on startup
     */
    static {
        CRAFTED_RESOURCES = new HashMap<>();
        MAX_PAGES = new HashMap<>();
        File shopsFolder = RunicCore.getConfigAPI().getSubFolder(RunicProfessions.getInstance().getDataFolder(), "workstations");
        for (File workstationFile : shopsFolder.listFiles()) {
            if (workstationFile.isDirectory()) continue; // ignore subdirectories
            try {
                FileConfiguration fileConfig = RunicCore.getConfigAPI().getYamlConfigFromFile(workstationFile.getName(), shopsFolder);
                WorkstationType workstationType = WorkstationType.getFromName(fileConfig.getString("workstation"));
                int maxPages = fileConfig.getInt("maxPages");
                MAX_PAGES.put(workstationType, maxPages);
                List<CraftedResource> craftedResources = loadWorkstationContents
                        (
                                RunicCore.getConfigAPI().getYamlConfigFromFile(workstationFile.getName(), shopsFolder),
                                workstationType
                        );
                CRAFTED_RESOURCES.put(workstationType, craftedResources);
            } catch (WorkstationLoadException exception) {
                exception.addMessage("Error loading workstation for file: " + workstationFile.getName());
                exception.displayToConsole();
                exception.displayToOnlinePlayers();
            }
        }
    }

    /**
     * Loads a shop from its corresponding yml file
     *
     * @param config the file configuration of the shop file
     * @return a list of crafted resources that can be used to populate a workstation
     * @throws WorkstationLoadException if the syntax is configured incorrectly
     */
    public static List<CraftedResource> loadWorkstationContents(FileConfiguration config, WorkstationType workstationType) throws WorkstationLoadException {
        try {
            List<CraftedResource> craftedResources = new ArrayList<>();
            for (String itemId : config.getConfigurationSection("items").getKeys(false)) {
                CraftedResource craftedResource;
                try {
                    craftedResource = loadCraftedResource(config.getConfigurationSection("items." + itemId), itemId, workstationType);
                } catch (WorkstationLoadException exception) {
                    exception.addMessage(itemId + "", "resource: " + itemId);
                    throw exception;
                }
                craftedResources.add(craftedResource);
            }
            return craftedResources;
        } catch (WorkstationLoadException exception) {
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new WorkstationLoadException("unknown syntax error").setErrorMessage(exception.getMessage());
        }
    }

    /**
     * Loads a RunicShopItem from its section of a shop config
     *
     * @param section     of the item
     * @param runicItemId of the item
     * @return a RunicShopItem
     * @throws WorkstationLoadException if incorrectly configured
     */
    public static CraftedResource loadCraftedResource(ConfigurationSection section, String runicItemId, WorkstationType workstationType) throws WorkstationLoadException {
        try {
            int page = section.getInt("page");
            int slot = section.getInt("slot");
            LinkedHashMap<String, Integer> reagents = new LinkedHashMap<>();
            for (String reagentId : section.getConfigurationSection("reagents").getKeys(false)) {
                reagents.put(reagentId, section.getConfigurationSection("reagents").getInt(reagentId));
            }
            return new CraftedResource
                    (
                            runicItemId,
                            workstationType.getProfession(),
                            page,
                            slot,
                            reagents
                    );
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new WorkstationLoadException("item initialization syntax error for " + runicItemId).setErrorMessage(exception.getMessage());
        }
    }

    /**
     * Dummy method to force the class to load
     */
    public static void init() {

    }

    public static Map<WorkstationType, List<CraftedResource>> getCraftedResources() {
        return CRAFTED_RESOURCES;
    }

    public static Map<WorkstationType, Integer> getMaxPages() {
        return MAX_PAGES;
    }

}
