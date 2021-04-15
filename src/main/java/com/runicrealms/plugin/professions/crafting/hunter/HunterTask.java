package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class HunterTask {

    //maybe will have use in future...
    public static final List<String> REGIONS = Arrays.asList("azana", "koldore", "whaletown", "hilstead", "wintervale", "dead_mans_rest", "isfodar", "tireneas", "zenyth", "naheen", "nazmora", "frosts_end");

    public enum HunterMob {

        GOBLIN(0, 7, 5, "azana"),
        GOLEM(10, 18, 10, "koldore"),
        DIREWOLF(20, 22, 15, "hilstead", "wintervale"),
        SPINNER(30, 28, 25, "isfodar", "tireneas"),
        BUG(40, 31, 30, "zenyth", "naheen"),
        FIRE_ELEMENTAL(50, 34, 35, "zenyth", "naheen", "nazmora"),
        BLACKFROST_BEAR(60, 40, 100, "frosts_end");

        private final int level;
        private final int experience;
        private final int points;
        private final List<String> regions;

        HunterMob(int level, int exp, int points, String... regions) {
            this.level = level;
            this.experience = exp;
            this.points = points;
            this.regions = Arrays.asList(regions);
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

        public List<String> getRegions() {
            return this.regions;
        }
    }

    /**
     * Check player's hunter level from player cache
     */
    private int checkLevel(Player pl) {
        return RunicCoreAPI.getPlayerCache(pl).getProfLevel();
    }

    public static int getCurrentKills(Player pl) {
        return RunicProfessions.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.hunter_kills");
    }

    /**
     * Add experience to the player's hunter profession
     */
    public static void giveExperience(Player pl, boolean sendMsg) {
        HunterMob hunterMob = HunterMob.valueOf(RunicProfessions.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob"));
        pl.sendMessage(ChatColor.GREEN + "Hunter mob slain!");
        ProfExpUtil.giveExperience(pl, hunterMob.getExperience(), sendMsg);
    }

    public static int getEarnedPoints(Player pl) {
        HunterMob hunterMob = HunterMob.valueOf(RunicProfessions.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob"));
        return hunterMob.getPoints();
    }

    public static String getMobName(Player pl) {
        return RunicProfessions.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob");
    }

    public static int getTotalPoints(Player pl) {
        return RunicProfessions.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.hunter_points");
    }

    /**
     USED IN HUNTERMENU CLASS, PLEASE DELETE
     */
    public static int getMobAmount(Player pl) {
        return RunicProfessions.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.hunter_kills_max");
    }
}
