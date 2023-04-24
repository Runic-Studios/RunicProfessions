package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.event.GatheringLevelChangeEvent;
import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.utilities.ChatUtils;
import com.runicrealms.runicitems.RunicItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;

/**
 * Handles logic for when player changes a level in a gathering skill
 */
public class GatheringLevelChangeListener implements Listener {

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

    @EventHandler(priority = EventPriority.LOW) // early
    public void onGatheringLevelChange(GatheringLevelChangeEvent event) {
        Player player = event.getPlayer();
        GatheringData gatheringData = event.getGatheringData();
        GatheringSkill gatheringSkill = event.getGatheringSkill();
        int currentLevel = gatheringData.getGatheringLevel(gatheringSkill);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        if (currentLevel == ProfExpUtil.MAX_CAPPED_GATHERING_PROF_LEVEL) {
            player.sendTitle(
                    ChatColor.GOLD + "Max Level!",
                    ChatColor.GOLD + gatheringSkill.getFormattedIdentifier() + " Level " + ChatColor.WHITE + currentLevel, 10, 40, 10);
        } else {
            player.sendTitle(
                    ChatColor.GREEN + "Level Up!",
                    ChatColor.GREEN + gatheringSkill.getFormattedIdentifier() + " Level " + ChatColor.WHITE + currentLevel, 10, 40, 10);
        }
        sendLevelUpMessage(player, gatheringSkill, currentLevel);
        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(), () -> {
            try (Jedis jedis = RunicCore.getRedisAPI().getNewJedisResource()) {
                gatheringData.writeToJedis(player.getUniqueId(), jedis);
            }
        });
    }
}
