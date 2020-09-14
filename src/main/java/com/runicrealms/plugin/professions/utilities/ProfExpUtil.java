package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.player.cache.PlayerCache;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import com.runicrealms.plugin.utilities.NumRounder;

/**
 * Utility to grant player profession experience and keep track of it.
 * Since switching between the class lv / profession lv on the actual exp bar
 * proved to be too vulnerable to bugs, I created a brand new leveling curve.
 * @author Skyfallin_
 */
public class ProfExpUtil {

    private static final int maxLevel = 60;

    public static void giveExperience(Player pl, int expGained, boolean sendMsg) {

        PlayerCache playerCache = RunicCoreAPI.getPlayerCache(pl);
        String profName = playerCache.getProfName();
        int currentLv = playerCache.getProfLevel();
        int currentExp = playerCache.getProfExp();

        if (currentLv >= maxLevel) return;

        playerCache.setProfExp(currentExp + expGained);

        int newTotalExp = playerCache.getProfExp();

        if (calculateExpectedLv(newTotalExp) != currentLv) {

            playerCache.setProfLevel(calculateExpectedLv(newTotalExp));
            currentLv = playerCache.getProfLevel();

            pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);

            // title message
            if (currentLv == maxLevel) {
                pl.sendTitle(
                        ChatColor.GOLD + "Max Level!",
                        ChatColor.GOLD + profName + " Level " + ChatColor.WHITE + currentLv, 10, 40, 10);
            } else {
                pl.sendTitle(
                        ChatColor.GREEN + "Level Up!",
                        ChatColor.GREEN + profName + " Level " + ChatColor.WHITE + currentLv, 10, 40, 10);
            }
        }

        // calculate the player's progress towards the next level
        currentExp = playerCache.getProfExp();
        int totalExpAtLevel = calculateTotalExperience(currentLv);
        int totalExpToLevel = calculateTotalExperience(currentLv+1);

        double progress = (double) (currentExp-totalExpAtLevel) / (totalExpToLevel-totalExpAtLevel); // 60 - 55 = 5 / 75 - 55 = 20, 5 /20
        int progressRounded = (int) NumRounder.round(progress * 100);

        if (sendMsg) {
            pl.sendMessage(ChatColor.GREEN + "Profession progress towards next lv: " + progressRounded + "% "
                    + "(" + (currentExp - totalExpAtLevel) + "/" + (totalExpToLevel - totalExpAtLevel) + ")");
        }
    }


    private static int calculateExpectedLv(double experience) {
        return (int) ((Math.cbrt((15*(experience+25))/3)) - 5);
    }

    // 33250 at 50
    // 54900 at 60
    public static int calculateTotalExperience(int currentLv) {
        int cubed = (int) Math.pow((currentLv+5), 3);
        return ((3*cubed)/15)-25;
    }
}
