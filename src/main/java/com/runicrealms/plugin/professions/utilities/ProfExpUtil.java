package com.runicrealms.plugin.professions.utilities;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.event.GatheringLevelChangeEvent;
import com.runicrealms.plugin.professions.event.ProfessionLevelChangeEvent;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.model.CraftingData;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.utilities.ActionBarUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

/**
 * Utility to grant player profession experience and keep track of it.
 *
 * @author Skyfallin_
 */
public class ProfExpUtil {

    public static final int MAX_CRAFTING_PROF_LEVEL = 60;
    public static final int MAX_CAPPED_GATHERING_PROF_LEVEL = 60;

    /**
     * Asynchronously gives the given player experience toward their crafting profession (alchemist, blacksmith, etc.)
     *
     * @param player    to be given experience
     * @param expGained amount of experience gained
     */
    public static void giveCraftingExperience(Player player, int expGained) {
        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(), () -> {
            try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
                int slot = RunicCore.getCharacterAPI().getCharacterSlot(player.getUniqueId());
                CraftingData craftingData = RunicProfessions.getDataAPI().loadCraftingData(player.getUniqueId(), slot);
                Profession profession = RunicProfessions.getAPI().getPlayerProfession(player.getUniqueId(), slot);
                int currentExp = RunicProfessions.getAPI().getPlayerProfessionExp(player.getUniqueId(), slot);
                int currentLevel = RunicProfessions.getAPI().getPlayerProfessionLevel(player.getUniqueId(), slot);
                if (currentLevel >= MAX_CRAFTING_PROF_LEVEL) return;
                int newTotalExp = currentExp + expGained;
                craftingData.setProfExp(newTotalExp);
                craftingData.writeToJedis(player.getUniqueId(), jedis, slot);
                int totalExpAtLevel = calculateTotalExperience(currentLevel);
                int totalExpToLevel = calculateTotalExperience(currentLevel + 1);
                ActionBarUtil.sendTimedMessage
                        (
                                player,
                                ChatColor.GREEN + "+ " + ChatColor.WHITE + expGained + ChatColor.GREEN + " " +
                                        profession.getName() + " exp " + ChatColor.GRAY + "(" +
                                        ChatColor.WHITE + (newTotalExp - totalExpAtLevel) + ChatColor.GRAY + "/" +
                                        (totalExpToLevel - totalExpAtLevel) + ")",
                                3
                        );
                int newLevel = calculateProfessionLevel(newTotalExp);
                if (newLevel == currentLevel) return;
                if (newLevel > MAX_CRAFTING_PROF_LEVEL) // fixed a bug 59 --> 61
                    newLevel = MAX_CRAFTING_PROF_LEVEL;
                // player has earned a profession level!
                ProfessionLevelChangeEvent professionLevelChangeEvent = new ProfessionLevelChangeEvent
                        (
                                player,
                                profession,
                                currentLevel,
                                newLevel,
                                jedis
                        );
                Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> Bukkit.getPluginManager().callEvent(professionLevelChangeEvent));
            }
        });
    }

    /**
     * Grants the player experience in the given gathering skill
     *
     * @param player         to receive experience
     * @param gatheringSkill in which to give experience
     * @param expGained      amount of experience gained
     */
    public static void giveGatheringExperience(Player player, GatheringSkill gatheringSkill, int expGained) {
        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(), () -> {
            try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
                GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(player.getUniqueId());
                int currentExp = gatheringData.getGatheringExp(gatheringSkill);
                int currentLevel = ProfExpUtil.calculateProfessionLevel(currentExp);
                int maxLevelForGatheringSkill = MAX_CAPPED_GATHERING_PROF_LEVEL;
                if (currentLevel >= maxLevelForGatheringSkill) return;
                gatheringData.setGatheringExp(gatheringSkill, currentExp + expGained);
                int newTotalExp = gatheringData.getGatheringExp(gatheringSkill);
                int totalExpAtLevel = calculateTotalExperience(currentLevel);
                int totalExpToLevel = calculateTotalExperience(currentLevel + 1);
                int combatExp = (int) (expGained * gatheringSkill.getCombatExpMult());
                ActionBarUtil.sendTimedMessage
                        (
                                player,
                                ChatColor.GREEN + "+ " + ChatColor.WHITE + expGained + ChatColor.GREEN + " " +
                                        gatheringSkill.getFormattedIdentifier() + " exp " + ChatColor.GRAY + "(" +
                                        ChatColor.WHITE + (newTotalExp - totalExpAtLevel) + ChatColor.GRAY + "/" +
                                        (totalExpToLevel - totalExpAtLevel) + ")" +

                                        ChatColor.YELLOW + " | " +

                                        ChatColor.GREEN + "+ " + ChatColor.WHITE + combatExp + ChatColor.GREEN + " " +
                                        "combat exp",
                                3
                        );
                int newLevel = calculateProfessionLevel(newTotalExp);
                RunicCore.getCombatAPI().giveCombatExp(player, combatExp);
                gatheringData.writeToJedis(player.getUniqueId(), jedis);
                if (newLevel == currentLevel) return;
                // Player has earned a gathering level!
                if (newLevel > maxLevelForGatheringSkill) // fixed a bug 59 --> 61
                    newLevel = maxLevelForGatheringSkill;
                GatheringLevelChangeEvent gatheringLevelChangeEvent = new GatheringLevelChangeEvent
                        (
                                player,
                                gatheringData,
                                gatheringSkill,
                                currentLevel,
                                newLevel
                        );
                // Call level change event SYNC
                Bukkit.getScheduler().runTask(RunicProfessions.getInstance(),
                        () -> Bukkit.getPluginManager().callEvent(gatheringLevelChangeEvent));
            }
        });
    }

    /**
     * Calculates the expected profession level (crafting OR gathering) based on the given experience amount
     * Uses the inverse function of calculateTotalExperience
     *
     * @param experience the total experience of the player in profession (gathering or crafting)
     * @return the level at which they should be (e.g., ~500000 experience should be level 60)
     */
    public static int calculateProfessionLevel(double experience) {
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
