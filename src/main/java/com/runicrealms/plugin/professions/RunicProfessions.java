package com.runicrealms.plugin.professions;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.runicrealms.plugin.professions.api.DataAPI;
import com.runicrealms.plugin.professions.api.ProfessionsAPI;
import com.runicrealms.plugin.professions.commands.ProfGiveCMD;
import com.runicrealms.plugin.professions.commands.ProfSetCMD;
import com.runicrealms.plugin.professions.commands.WorkstationCMD;
import com.runicrealms.plugin.professions.config.WorkstationLoader;
import com.runicrealms.plugin.professions.crafting.alchemist.PotionListener;
import com.runicrealms.plugin.professions.crafting.blacksmith.StoneListener;
import com.runicrealms.plugin.professions.crafting.cooking.CookingListener;
import com.runicrealms.plugin.professions.crafting.enchanter.PowderListener;
import com.runicrealms.plugin.professions.gathering.GatheringGUIListener;
import com.runicrealms.plugin.professions.gathering.GatheringSkillGUIListener;
import com.runicrealms.plugin.professions.gathering.mining.OreNodeManager;
import com.runicrealms.plugin.professions.listeners.CropTrampleListener;
import com.runicrealms.plugin.professions.listeners.FishingListener;
import com.runicrealms.plugin.professions.listeners.GatheringLevelChangeListener;
import com.runicrealms.plugin.professions.listeners.GatheringListener;
import com.runicrealms.plugin.professions.listeners.HarvestingListener;
import com.runicrealms.plugin.professions.listeners.PlayerMenuListener;
import com.runicrealms.plugin.professions.listeners.ProfessionChangeListener;
import com.runicrealms.plugin.professions.listeners.ProfessionLevelChangeListener;
import com.runicrealms.plugin.professions.listeners.RunicCraftingExpListener;
import com.runicrealms.plugin.professions.listeners.RunicGatheringExpListener;
import com.runicrealms.plugin.professions.listeners.ScoreboardListener;
import com.runicrealms.plugin.professions.listeners.StationClickListener;
import com.runicrealms.plugin.professions.listeners.VanillaStationListener;
import com.runicrealms.plugin.professions.listeners.WorkstationListener;
import com.runicrealms.plugin.professions.model.DataManager;
import com.runicrealms.plugin.professions.model.MongoTask;
import com.runicrealms.plugin.professions.shop.JewelRemoverListener;
import com.runicrealms.plugin.professions.shop.ProfessionTutorHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RunicProfessions extends JavaPlugin {
    private static RunicProfessions plugin;
    private static TaskChainFactory taskChainFactory;
    private static ProfessionsAPI professionsAPI;
    private static DataAPI dataAPI;
    private static MongoTask mongoTask;
    private static PaperCommandManager commandManager;
    private static OreNodeManager oreNodeManager;

    public static RunicProfessions getInstance() {
        return plugin;
    }

    public static ProfessionsAPI getAPI() {
        return professionsAPI;
    }

    public static DataAPI getDataAPI() {
        return dataAPI;
    }

    public static PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public static MongoTask getMongoTask() {
        return mongoTask;
    }

    public static OreNodeManager getOreNodeManager() {
        return oreNodeManager;
    }

    /**
     * @return a new TaskChain for thread context switching
     */
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    /**
     * Load workstation contents from file storage
     */
    private void initializeWorkstations() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RunicProfessions.getInstance(), WorkstationLoader::init, 10 * 20L);
    }

    @Override
    public void onDisable() {
        plugin.saveConfig();
        plugin = null;
        professionsAPI = null;
        dataAPI = null;
        mongoTask = null;
        commandManager = null;
        taskChainFactory = null;
        oreNodeManager = null;
    }

    @Override
    public void onEnable() {
        plugin = this;
        taskChainFactory = BukkitTaskChainFactory.create(this);
        professionsAPI = new ProfManager();
        dataAPI = new DataManager();
        mongoTask = new MongoTask();
        commandManager = new PaperCommandManager(this);
        oreNodeManager = new OreNodeManager();

        new ProfessionTutorHelper(); // initialize profession tutors

        this.registerCommandCompletions();
        this.registerCommands();
        this.registerEvents();

        this.initializeWorkstations();

        getLogger().info(" §aRunic§2Professions §ahas been enabled.");
    }

    private void registerCommandCompletions() {
        commandManager.getCommandConditions().addCondition("is-console-or-op", context -> {
            if (!(context.getIssuer().getIssuer() instanceof ConsoleCommandSender) && !context.getIssuer().getIssuer().isOp()) // ops can execute console commands
                throw new ConditionFailedException("Only the console may run this command!");
        });
        commandManager.getCommandConditions().addCondition("is-op", context -> {
            if (!context.getIssuer().getIssuer().isOp())
                throw new ConditionFailedException("You must be an operator to run this command!");
        });
        commandManager.getCommandConditions().addCondition("is-player", context -> {
            if (!(context.getIssuer().getIssuer() instanceof Player))
                throw new ConditionFailedException("This command cannot be run from console!");
        });
    }

    private void registerCommands() {
        commandManager.registerCommand(new ProfGiveCMD());
        commandManager.registerCommand(new ProfSetCMD());
        commandManager.registerCommand(new WorkstationCMD());
    }

    private void registerEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new WorkstationListener(), this);
        pluginManager.registerEvents(new FishingListener(), this);
        pluginManager.registerEvents(new PotionListener(), this);
        pluginManager.registerEvents(new CookingListener(), this);
        pluginManager.registerEvents(new StationClickListener(), this);
        pluginManager.registerEvents(new StoneListener(), this);
        pluginManager.registerEvents(new PowderListener(), this);
        pluginManager.registerEvents(new CropTrampleListener(), this);
        pluginManager.registerEvents(new GatheringListener(), this);
        pluginManager.registerEvents(new GatheringGUIListener(), this);
        pluginManager.registerEvents(new GatheringSkillGUIListener(), this);
        pluginManager.registerEvents(new HarvestingListener(), this);
        pluginManager.registerEvents(new VanillaStationListener(), this);
        pluginManager.registerEvents(new ProfessionLevelChangeListener(), this);
        pluginManager.registerEvents(new GatheringLevelChangeListener(), this);
        pluginManager.registerEvents(new PlayerMenuListener(), this);
        pluginManager.registerEvents(new ProfessionChangeListener(), this);
        pluginManager.registerEvents(new ScoreboardListener(), this);
        pluginManager.registerEvents(new JewelRemoverListener(), this);
        pluginManager.registerEvents(new RunicCraftingExpListener(), this);
        pluginManager.registerEvents(new RunicGatheringExpListener(), this);
    }
}
