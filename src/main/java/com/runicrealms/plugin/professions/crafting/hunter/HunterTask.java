package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

    private final Player pl;
    private final MythicMob mob;

    public HunterTask(Player pl) {
        this.pl = pl;
        this.mob = selectMob(pl);
        Random rand = ThreadLocalRandom.current();
        RunicProfessions.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_kills_max", rand.nextInt(30 - 15) + 15); // 15-30
        RunicProfessions.getInstance().saveConfig();
        RunicProfessions.getInstance().reloadConfig();
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
     * Adds to player's hunter score in config
     */
    public static void givePoints(Player pl) {
        HunterMob hunterMob = HunterMob.valueOf(RunicProfessions.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob"));
        int current = RunicProfessions.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.hunter_points");
        RunicProfessions.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_points", current+hunterMob.getPoints());
        RunicProfessions.getInstance().saveConfig();
        RunicProfessions.getInstance().reloadConfig();
    }

    /**
     * Select a random hunter task from a list of available mobs.
     * Checks only mythicmobs with 'hunter' faction, and checks player's hunter
     * level against mobs level. Writes that task to config.
     */
    //IS NO LONGER USED, MOVED TO HunterPlayer.java!!!
    private MythicMob selectMob(Player pl) {
        // declare a temporary HashSet to pick a random mob
        List<MythicMob> hunterMobs = new ArrayList<>();
        List<String> names = this.getRegions(pl);

        // filter-out mobs above the player's hunter level and region
        int playLv = this.checkLevel(pl);
        for (HunterMob mob : HunterMob.values()) {
            int mobLv = mob.getLevel();
            if (mobLv > playLv) {
                continue;
            }

            if (!this.containsRegion(mob, names)) {
                continue;
            }

            hunterMobs.add(MythicMobs.inst().getMobManager().getMythicMob(mob.name()));
        }

        Random rand = ThreadLocalRandom.current();
        int index = rand.nextInt(hunterMobs.size());
        MythicMob mythicMob = hunterMobs.get(index);

        // set mob as task in config
        RunicProfessions.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_mob", mythicMob.getInternalName());
        RunicProfessions.getInstance().saveConfig();
        RunicProfessions.getInstance().reloadConfig();
        return mythicMob;
    }

    public MythicMob getMob() {
        return mob;
    }

    public static int getMobAmount(Player pl) {
        return RunicProfessions.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.hunter_kills_max");
    }

    private List<String> getRegions(Player player) {
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        List<String> names = new ArrayList<>();

        regions.forEach(region -> names.add(region.getId()));

        return names;
    }

    private boolean containsRegion(HunterMob mob, List<String> names) {
        for (String name : names) {
            if (mob.getRegions().contains(name)) {
                return true;
            }
        }
        return false;
    }
}
