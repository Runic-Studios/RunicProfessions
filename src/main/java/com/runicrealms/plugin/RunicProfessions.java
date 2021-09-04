package com.runicrealms.plugin;


import com.runicrealms.plugin.professions.GatheringShopFactory;
import com.runicrealms.plugin.professions.ProfManager;
import com.runicrealms.plugin.professions.commands.ProfLevelCMD;
import com.runicrealms.plugin.professions.commands.SetProfCMD;
import com.runicrealms.plugin.professions.crafting.alchemist.PotionListener;
import com.runicrealms.plugin.professions.crafting.blacksmith.StoneListener;
import com.runicrealms.plugin.professions.crafting.cooking.CookingListener;
import com.runicrealms.plugin.professions.crafting.enchanter.EnchantListener;
import com.runicrealms.plugin.professions.crafting.hunter.HunterCache;
import com.runicrealms.plugin.professions.crafting.hunter.HunterListener;
import com.runicrealms.plugin.professions.crafting.hunter.HunterShop;
import com.runicrealms.plugin.professions.crafting.jeweler.JewelShopListener;
import com.runicrealms.plugin.professions.crafting.jeweler.SocketListener;
import com.runicrealms.plugin.professions.listeners.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RunicProfessions extends JavaPlugin {

    private static RunicProfessions plugin;
    private static ProfManager profManager;
    private static HunterCache hunterCache;

    public static RunicProfessions getInstance() {
        return plugin;
    }

    public static ProfManager getProfManager() {
        return profManager;
    }

    public static HunterCache getHunterCache() {
        return hunterCache;
    }

    @Override
    public void onEnable() {
        plugin = this;
        profManager = new ProfManager();
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
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new WorkstationListener(), this);
        pm.registerEvents(new MiningListener(), this);
        pm.registerEvents(new FarmingListener(), this);
        pm.registerEvents(new WoodcuttingListener(), this);
        pm.registerEvents(new FishingListener(), this);
        pm.registerEvents(new SocketListener(), this);
        pm.registerEvents(new PotionListener(), this);
        pm.registerEvents(new CookingListener(), this);
        pm.registerEvents(new StationClickListener(), this);
        pm.registerEvents(new JewelShopListener(), this);
        pm.registerEvents(new HunterListener(), this);
        pm.registerEvents(new StoneListener(), this);
        pm.registerEvents(new EnchantListener(), this);
        pm.registerEvents(new CustomFishListener(), this);
        pm.registerEvents(new CropTrampleListener(), this);
    }

    private void initializeShops() {
        new HunterShop();
        new GatheringShopFactory();
    }
}
