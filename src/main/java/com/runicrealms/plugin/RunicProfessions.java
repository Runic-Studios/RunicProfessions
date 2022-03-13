package com.runicrealms.plugin;

import com.runicrealms.plugin.professions.ProfManager;
import com.runicrealms.plugin.professions.commands.ProfLevelCMD;
import com.runicrealms.plugin.professions.commands.SetProfCMD;
import com.runicrealms.plugin.professions.crafting.alchemist.PotionListener;
import com.runicrealms.plugin.professions.crafting.blacksmith.StoneListener;
import com.runicrealms.plugin.professions.crafting.cooking.CookingListener;
import com.runicrealms.plugin.professions.crafting.enchanter.PowderListener;
import com.runicrealms.plugin.professions.crafting.hunter.HunterCache;
import com.runicrealms.plugin.professions.crafting.hunter.HunterListener;
import com.runicrealms.plugin.professions.gathering.GatherPlayerManager;
import com.runicrealms.plugin.professions.gathering.GatheringGUIListener;
import com.runicrealms.plugin.professions.gathering.GatheringSkillGUIListener;
import com.runicrealms.plugin.professions.listeners.*;
import com.runicrealms.plugin.professions.shop.GatheringShopFactory;
import com.runicrealms.plugin.professions.shop.HunterShop;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RunicProfessions extends JavaPlugin {

    private static RunicProfessions plugin;
    private static ProfManager profManager;
    private static GatherPlayerManager gatherPlayerManager;
    private static HunterCache hunterCache;

    public static RunicProfessions getInstance() {
        return plugin;
    }

    public static ProfManager getProfManager() {
        return profManager;
    }

    public static GatherPlayerManager getGatherPlayerManager() {
        return gatherPlayerManager;
    }

    public static HunterCache getHunterCache() {
        return hunterCache;
    }

    @Override
    public void onEnable() {
        plugin = this;
        profManager = new ProfManager();
        gatherPlayerManager = new GatherPlayerManager();
        hunterCache = new HunterCache();

        // register commands
        getCommand("setprof").setExecutor(new SetProfCMD());
        getCommand("proflevel").setExecutor(new ProfLevelCMD());

        this.initializeShops();
        this.registerEvents();

        getLogger().info(" §aRunic§2Professions §ahas been enabled.");
    }

    @Override
    public void onDisable() {
        plugin.saveConfig();
        plugin = null;
        profManager = null;
    }

    private void registerEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new WorkstationListener(), this);
        pluginManager.registerEvents(new FishingListener(), this);
        pluginManager.registerEvents(new PotionListener(), this);
        pluginManager.registerEvents(new CookingListener(), this);
        pluginManager.registerEvents(new StationClickListener(), this);
        pluginManager.registerEvents(new HunterListener(), this);
        pluginManager.registerEvents(new StoneListener(), this);
        pluginManager.registerEvents(new PowderListener(), this);
        pluginManager.registerEvents(new CustomFishListener(), this);
        pluginManager.registerEvents(new CropTrampleListener(), this);
        pluginManager.registerEvents(new GatherPlayerListener(), this);
        pluginManager.registerEvents(new GatheringListener(), this);
        pluginManager.registerEvents(new GatheringGUIListener(), this);
        pluginManager.registerEvents(new GatheringSkillGUIListener(), this);
        pluginManager.registerEvents(new HarvestingListener(), this);
        pluginManager.registerEvents(new VanillaStationListener(), this);
    }

    private void initializeShops() {
        new HunterShop();
        new GatheringShopFactory();
    }
}
