package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HunterTask {

    public enum HunterMob {

        GOBLIN(0, 7, 5),
        GOLEM(10, 20, 10),
        VIKING(60, 40, 100);

        int level;
        int experience;
        int points;

        HunterMob(int level, int exp, int points) {
            this.level = level;
            this.experience = exp;
            this.points = points;
        }

        public int getExperience() {
            return experience;
        }

        public int getLevel() {
            return level;
        }

        public int getPoints() {
            return points;
        }

        public String toValue() {
            return name().toLowerCase();
        }
    }

    private static final int MOB_AMOUNT = 25;
    private Player pl;
    private MythicMob mob;

    public HunterTask(Player pl) {
        this.pl = pl;
        this.mob = selectMob(pl);
    }

    /**
     * Check player's hunter level from core config
     */
    private int checkLevel(Player pl) {
        return RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).getProfLevel();
    }

    public static int getCurrentKills(Player pl) {
        return RunicCore.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.hunter_kills");
    }

    /**
     * Add experience to the player's hunter profession
     */
    public static void giveExperience(Player pl, boolean sendMsg) {
        HunterMob hunterMob = HunterMob.valueOf(RunicCore.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob").toUpperCase());
        pl.sendMessage(ChatColor.GREEN + "Hunter mob slain!");
        ProfExpUtil.giveExperience(pl, hunterMob.getExperience(), sendMsg);
    }

    public static int getEarnedPoints(Player pl) {
        HunterMob hunterMob = HunterMob.valueOf(RunicCore.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob").toUpperCase());
        return hunterMob.getPoints();
    }

    public static String getMobName(Player pl) {
        return RunicCore.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob");
    }

    public static int getTotalPoints(Player pl) {
        return RunicCore.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.hunter_points");
    }

    /**
     * Adds to player's hunter score in config
     */
    public static void givePoints(Player pl) {
        HunterMob hunterMob = HunterMob.valueOf(RunicCore.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob").toUpperCase());
        int current = RunicCore.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.hunter_points");
        RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_points", current+hunterMob.getPoints());
        RunicCore.getInstance().saveConfig();
        RunicCore.getInstance().reloadConfig();
    }

    /**
     * Select a random hunter task from a list of available mobs.
     * Checks only mythicmobs with 'hunter' faction, and checks player's hunter
     * level against mobs level. Writes that task to config.
     */
    private MythicMob selectMob(Player pl) {

        // declare a temporary HashSet to pick a random mob
        List<MythicMob> hunterMobs = new ArrayList<>();

        // filter-out mobs above the player's hunter level
        int playLv = checkLevel(pl);
        for (HunterMob mob : HunterMob.values()) {
            int mobLv = mob.getLevel();
            if (mobLv > playLv) continue;
            hunterMobs.add(MythicMobs.inst().getMobManager().getMythicMob(mob.toValue()));
        }

        Random rand = new Random();
        int index = rand.nextInt(hunterMobs.size());
        MythicMob mythicMob = hunterMobs.get(index);

        // set mob as task in config
        RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_mob", mythicMob.getInternalName());
        RunicCore.getInstance().saveConfig();
        RunicCore.getInstance().reloadConfig();
        return mythicMob;
    }

    public MythicMob getMob() {
        return mob;
    }

    public static int getMobAmount() {
        return MOB_AMOUNT;
    }
}
