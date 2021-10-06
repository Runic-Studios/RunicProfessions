package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.professions.gathering.GatherPlayer;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.utilities.NumRounder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Utility to grant player profession experience and keep track of it.
 *
 * @author Skyfallin_
 */
public class ProfExpUtil {

    private static final int MAX_CRAFTING_PROF_LEVEL = 60;
    private static final int MAX_GATHERING_PROF_LEVEL = 100;

    /**
     * Gives the given player experience toward their crafting profession (alchemist, blacksmith, etc.)
     *
     * @param player    to be given experience
     * @param expGained amount of experience gained
     * @param sendMsg   whether to send an experience message. disabled for admin commands
     */
    public static void giveCraftingExperience(Player player, int expGained, boolean sendMsg) {
        String profName = RunicCoreAPI.getPlayerCache(player).getProfName();
        int currentLv = RunicCoreAPI.getPlayerCache(player).getProfLevel();
        int currentExp = RunicCoreAPI.getPlayerCache(player).getProfExp();
        if (currentLv >= MAX_CRAFTING_PROF_LEVEL) return;
        RunicCoreAPI.getPlayerCache(player).setProfExp(currentExp + expGained);
        int newTotalExp = RunicCoreAPI.getPlayerCache(player).getProfExp();
        if (calculateProfessionLevel(newTotalExp) == currentLv) return;
        // player has earned a level!
        RunicCoreAPI.getPlayerCache(player).setProfLevel(calculateProfessionLevel(newTotalExp));
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

    /**
     * @param uuid
     * @param gatheringSkill
     * @param expGained
     * @param sendMsg
     */
    public static void giveGatheringExperience(UUID uuid, GatheringSkill gatheringSkill, int expGained, boolean sendMsg) {
        GatherPlayer gatherPlayer = RunicProfessionsAPI.getGatherPlayer(uuid);
        int currentExp = 0;
        int currentLevel = 0;
        switch (gatheringSkill) {
            case COOKING:
                currentExp = gatherPlayer.getCookingExp();
                currentLevel = gatherPlayer.getCookingLevel();
                break;
            case FARMING:
                currentExp = gatherPlayer.getFarmingExp();
                currentLevel = gatherPlayer.getFarmingLevel();
                break;
            case FISHING:
                currentExp = gatherPlayer.getFishingExp();
                currentLevel = gatherPlayer.getFishingLevel();
                break;
            case HARVESTING:
                currentExp = gatherPlayer.getHarvestingExp();
                currentLevel = gatherPlayer.getHarvestingLevel();
                break;
            case MINING:
                currentExp = gatherPlayer.getMiningExp();
                currentLevel = gatherPlayer.getMiningLevel();
                break;
            case WOODCUTTING:
                currentExp = gatherPlayer.getWoodcuttingExp();
                currentLevel = gatherPlayer.getWoodcuttingLevel();
                break;
        }
        if (currentLevel >= MAX_GATHERING_PROF_LEVEL) return;
    }

    /**
     * Calculates the expected profession level (crafting OR gathering) based on the given experience amount
     * Uses the inverse function of calculateTotalExperience
     *
     * @param experience the total experience of the player in profession (gathering or crafting)
     * @return the level at which they should be (e.g., ~500000 experience should be level 60)
     */
    private static int calculateProfessionLevel(double experience) {
        Bukkit.broadcastMessage("expected level from " + experience + "is " + (int) ((Math.cbrt((1125 + (5 * experience)) / 9)) - 5));
        return (int) ((Math.cbrt((1125 + (5 * experience)) / 9)) - 5);
    }

    /**
     * Calculated the total profession experience (crafting OR gathering, same curve) based on the current level
     * Uses the inverse function of calculateProfessionLevel
     * ~200000 at 50
     * ~500000 at 60
     *
     * @param currentLevel of the given crafting or gathering profession
     * @return the expected level (e.g., level 60 would return ~500000 experience)
     */
    public static int calculateTotalExperience(int currentLevel) {
        int cubed = (int) Math.pow((currentLevel + 5), 3);
        return ((9 * cubed) / 5) - 225;
    }
}
