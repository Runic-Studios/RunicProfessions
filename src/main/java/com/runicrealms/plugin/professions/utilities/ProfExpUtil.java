package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.professions.gathering.GatherPlayer;
import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.utilities.ActionBarUtil;
import com.runicrealms.plugin.utilities.ChatUtils;
import com.runicrealms.plugin.utilities.NumRounder;
import com.runicrealms.runicitems.RunicItemsAPI;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

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
     * Grants the player experience in the given gathering skill
     *
     * @param player         to receive experience
     * @param gatheringSkill in which to give experience
     * @param expGained      amount of experience gained
     */
    public static void giveGatheringExperience(Player player, GatheringSkill gatheringSkill, int expGained) {
        GatherPlayer gatherPlayer = RunicProfessionsAPI.getGatherPlayer(player.getUniqueId());
        int currentExp = gatherPlayer.getGatheringExp(gatheringSkill);
        int currentLevel = gatherPlayer.getGatheringLevel(gatheringSkill);
        if (currentLevel >= MAX_GATHERING_PROF_LEVEL) return;
        gatherPlayer.setGatheringExp(gatheringSkill, currentExp + expGained);
        int newTotalExp = gatherPlayer.getGatheringExp(gatheringSkill);
        int totalExpAtLevel = calculateTotalExperience(currentLevel);
        int totalExpToLevel = calculateTotalExperience(currentLevel + 1);
        ActionBarUtil.sendTimedMessage
                (
                        player,
                        ChatColor.GREEN + "+ " + ChatColor.WHITE + expGained + ChatColor.GREEN + " " +
                                gatheringSkill.getFormattedIdentifier() + " exp " + ChatColor.GRAY + "(" +
                                ChatColor.WHITE + (newTotalExp - totalExpAtLevel) + ChatColor.GRAY + "/" +
                                (totalExpToLevel - totalExpAtLevel) + ")",
                        3
                );
        if (calculateProfessionLevel(newTotalExp) == currentLevel) return;
        // player has earned a level!
        gatherPlayer.setGatheringLevel(gatheringSkill, calculateProfessionLevel(newTotalExp));
        currentLevel = gatherPlayer.getGatheringLevel(gatheringSkill);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        if (currentLevel == MAX_GATHERING_PROF_LEVEL) {
            player.sendTitle(
                    ChatColor.GOLD + "Max Level!",
                    ChatColor.GOLD + gatheringSkill.getFormattedIdentifier() + " Level " + ChatColor.WHITE + currentLevel, 10, 40, 10);
        } else {
            player.sendTitle(
                    ChatColor.GREEN + "Level Up!",
                    ChatColor.GREEN + gatheringSkill.getFormattedIdentifier() + " Level " + ChatColor.WHITE + currentLevel, 10, 40, 10);
        }
        sendLevelUpMessage(player, gatheringSkill, currentLevel);
    }

    /**
     * Calculates the expected profession level (crafting OR gathering) based on the given experience amount
     * Uses the inverse function of calculateTotalExperience
     *
     * @param experience the total experience of the player in profession (gathering or crafting)
     * @return the level at which they should be (e.g., ~500000 experience should be level 60)
     */
    private static int calculateProfessionLevel(double experience) {
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

    /**
     * Helpful level-up message for informing players when they can unlock a new resource
     *
     * @param player         who is gathering
     * @param gatheringSkill the skill the player has leveled-up
     * @param gatheringLevel level the player just reached
     */
    private static void sendLevelUpMessage(Player player, GatheringSkill gatheringSkill, int gatheringLevel) {
        ChatUtils.sendCenteredMessage(player, "");
        ChatUtils.sendCenteredMessage(
                player, nextReagentUnlockMessage(gatheringSkill, gatheringLevel, false).get(0));
        ChatUtils.sendCenteredMessage(player, "");
    }

    /**
     * Helpful level-up message for informing players when they can unlock a new resource
     *
     * @param gatheringSkill the skill the player has leveled-up
     * @param gatheringLevel level the player just reached
     * @param formatText     boolean value to determine whether the string will be formatted (for menu uis)
     * @return a list of strings (only contains 1 if it's not formatted) with reagent unlock info
     */
    public static List<String> nextReagentUnlockMessage(GatheringSkill gatheringSkill, int gatheringLevel, boolean formatText) {
        for (GatheringResource gatheringResource : GatheringResource.values()) {
            if (gatheringResource.getGatheringSkill() != gatheringSkill) continue;
            if (gatheringLevel < gatheringResource.getRequiredLevel()) {
                String result = ChatColor.YELLOW + "You have " + ChatColor.WHITE +
                        (gatheringResource.getRequiredLevel() - gatheringLevel) + ChatColor.YELLOW +
                        " levels remaining until you can gather " +
                        RunicItemsAPI.generateItemFromTemplate(gatheringResource.getTemplateId()).getDisplayableItem().getDisplayName() +
                        ChatColor.YELLOW + "!";
                if (formatText) return ChatUtils.formattedText(result);
                return Collections.singletonList(result);
            }
        }
        String noUnlocks = ChatColor.GREEN + "You have unlocked all reagents for this skill!";
        if (formatText) return ChatUtils.formattedText(noUnlocks);
        return Collections.singletonList(noUnlocks);
    }
}
