package com.runicrealms.plugin;


import com.runicrealms.plugin.professions.ProfManager;
import com.runicrealms.plugin.professions.commands.GathertoolGive;
import com.runicrealms.plugin.professions.commands.SetProfCMD;
import com.runicrealms.plugin.professions.crafting.alchemist.PotionListener;
import com.runicrealms.plugin.professions.crafting.blacksmith.StoneListener;
import com.runicrealms.plugin.professions.crafting.cooking.CookingListener;
import com.runicrealms.plugin.professions.crafting.hunter.*;
import com.runicrealms.plugin.professions.crafting.jeweler.JewelShopListener;
import com.runicrealms.plugin.professions.crafting.jeweler.JewelShopCMD;
import com.runicrealms.plugin.professions.crafting.jeweler.SocketListener;
import com.runicrealms.plugin.professions.event.StationClickEvent;
import com.runicrealms.plugin.professions.gathering.FarmingListener;
import com.runicrealms.plugin.professions.gathering.FishingListener;
import com.runicrealms.plugin.professions.gathering.MiningListener;
import com.runicrealms.plugin.professions.gathering.WCListener;
import com.runicrealms.plugin.professions.listeners.JoinListener;
import com.runicrealms.plugin.professions.listeners.WorkstationListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RunicProfessions extends JavaPlugin {

    private static RunicProfessions plugin;
    private static ProfManager profManager;

    public static RunicProfessions getInstance() { return plugin; }
    public static ProfManager getProfManager() { return profManager; }

    @Override
    public void onEnable() {

        plugin = this;
        profManager = new ProfManager();

        getConfig().options().copyDefaults(true);
        saveConfig();

        // register command
        getCommand("gathertool").setExecutor(new GathertoolGive());
        getCommand("setprof").setExecutor(new SetProfCMD());

        // gem removal shop
        getCommand("jewelmaster").setExecutor(new JewelShopCMD());

        // hunter shop
        getCommand("huntershop").setExecutor(new HunterShopCMD());

        this.registerEvents();

        getLogger().info(" §aRunic§2Professions §ahas been enabled.");
    }

    @Override
    public void onDisable() {
        plugin = null;
        profManager = null;
    }

    private void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new WorkstationListener(), this);
        pm.registerEvents(new MiningListener(), this);
        pm.registerEvents(new FarmingListener(), this);
        pm.registerEvents(new WCListener(), this);
        pm.registerEvents(new FishingListener(), this);
        pm.registerEvents(new SocketListener(), this);
        pm.registerEvents(new PotionListener(), this);
        pm.registerEvents(new CookingListener(), this);
        pm.registerEvents(new StationClickEvent(), this);
        pm.registerEvents(new JewelShopListener(), this);
        pm.registerEvents(new HunterListener(), this);
        pm.registerEvents(new HunterShopListener(), this);
        pm.registerEvents(new StoneListener(), this);
        pm.registerEvents(new JoinListener(), this);
    }
}
