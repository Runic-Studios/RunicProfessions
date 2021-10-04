package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.utilities.NumRounder;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Utility to grant player profession experience and keep track of it.
 *
 * @author Skyfallin_
 */
public class ProfExpUtil {

    private static final int MAX_CRAFTING_PROF_LEVEL = 60;

    /**
     * @param player
     * @param expGained
     * @param sendMsg
     */
    public static void giveCraftingExperience(Player player, int expGained, boolean sendMsg) {
        String profName = RunicCoreAPI.getPlayerCache(player).getProfName();
        int currentLv = RunicCoreAPI.getPlayerCache(player).getProfLevel();
        int currentExp = RunicCoreAPI.getPlayerCache(player).getProfExp();
        if (currentLv >= MAX_CRAFTING_PROF_LEVEL) return;
        RunicCoreAPI.getPlayerCache(player).setProfExp(currentExp + expGained);
        int newTotalExp = RunicCoreAPI.getPlayerCache(player).getProfExp();
        if (calculateExpectedLv(newTotalExp) == currentLv) return;
        // player has earned a level!
        RunicCoreAPI.getPlayerCache(player).setProfLevel(calculateExpectedLv(newTotalExp));
        currentLv = RunicCoreAPI.getPlayerCache(player).getProfLevel();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        // title message
        if (currentLv == MAX_CRAFTING_PROF_LEVEL) {
            player.sendTitle(
                    ChatColor.GOLD + "Max Level!",
                    ChatColor.GOLD + profName + " Level " + ChatColor.WHITE + currentLv, 10, 40, 10);
        } else {
            player.sendTitle(
                    ChatColor.GREEN + "Level Up!",
                    ChatColor.GREEN + profName + " Level " + ChatColor.WHITE + currentLv, 10, 40, 10);
        }
        // calculate the player's progress towards the next level
        currentExp = RunicCoreAPI.getPlayerCache(player).getProfExp();
        int totalExpAtLevel = calculateTotalExperience(currentLv);
        int totalExpToLevel = calculateTotalExperience(currentLv + 1);
        double progress = (double) (currentExp - totalExpAtLevel) / (totalExpToLevel - totalExpAtLevel); // 60 - 55 = 5 / 75 - 55 = 20, 5 /20
        int progressRounded = (int) NumRounder.round(progress * 100);
        if (sendMsg) {
            player.sendMessage(ChatColor.GREEN + "Profession progress towards next lv: " + progressRounded + "% "
                    + "(" + (currentExp - totalExpAtLevel) + "/" + (totalExpToLevel - totalExpAtLevel) + ")");
        }
    }

    public static void giveGatheringExperience(Player player) {

    }

    /**
     * @param experience
     * @return
     */
    private static int calculateExpectedLv(double experience) {
        return (int) ((Math.cbrt((15 * (experience + 25)) / 3)) - 5);
    }

    /*
     ~200000 at 50
     ~500000 at 60
     */

    /**
     * @param currentLv
     * @return
     */
    public static int calculateTotalExperience(int currentLv) {
        int cubed = (int) Math.pow((currentLv + 5), 3);
        return ((9 * cubed) / 5) - 225;
    }
}
