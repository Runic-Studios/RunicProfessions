package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.common.util.ChatUtils;
import com.runicrealms.plugin.professions.WorkstationType;
import com.runicrealms.plugin.professions.config.WorkstationLoader;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.plugin.professions.event.GatheringLevelChangeEvent;
import com.runicrealms.plugin.professions.gathering.GatheringResource;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.rdb.RunicDatabase;
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
        String currentUnlockedReagent = currentReagentUnlockMessage(gatheringSkill, gatheringLevel);
        if (!currentUnlockedReagent.equalsIgnoreCase("")) {
            ChatUtils.sendCenteredMessage(player, currentUnlockedReagent);
        }
        if (gatheringSkill == GatheringSkill.COOKING) {
            ChatUtils.sendCenteredMessage(
                    player, nextReagentUnlockMessageCooking(gatheringLevel, false).get(0));
        } else {
            ChatUtils.sendCenteredMessage(
                    player, nextReagentUnlockMessage(gatheringSkill, gatheringLevel, false).get(0));
        }
        ChatUtils.sendCenteredMessage(player, "");
    }

    public static String currentReagentUnlockMessage(GatheringSkill gatheringSkill, int gatheringLevel) {
        for (GatheringResource gatheringResource : GatheringResource.values()) {
            if (gatheringResource.getGatheringSkill() != gatheringSkill) continue;
            if (gatheringLevel == gatheringResource.getRequiredLevel()) {
                return ChatColor.GREEN + String.valueOf(ChatColor.BOLD) + "You can now gather " +
                        ChatColor.BOLD + RunicItemsAPI.generateItemFromTemplate(gatheringResource.getTemplateId()).getDisplayableItem().getDisplayName() +
                        ChatColor.GREEN + ChatColor.BOLD + "!";
            }
        }
        return "";
    }

    public static List<String> nextReagentUnlockMessageCooking(int gatheringLevel, boolean formatText) {
        for (CraftedResource craftedResource : WorkstationLoader.getCraftedResources().get(WorkstationType.COOKING_FIRE)) {
            if (gatheringLevel < craftedResource.getRequiredLevel()) {
                String result = ChatColor.YELLOW + "You have " + ChatColor.WHITE +
                        (craftedResource.getRequiredLevel() - gatheringLevel) + ChatColor.YELLOW +
                        " level(s) left until you can cook " +
                        RunicItemsAPI.generateItemFromTemplate(craftedResource.getTemplateId()).getDisplayableItem().getDisplayName() +
                        ChatColor.YELLOW + "!";
                if (formatText) return ChatUtils.formattedText(result);
                return Collections.singletonList(result);
            }
        }
        String noUnlocks = ChatColor.GREEN + "You have unlocked all reagents for this skill!";
        if (formatText) return ChatUtils.formattedText(noUnlocks);
        return Collections.singletonList(noUnlocks);
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
        GatheringResource next = null;
        for (GatheringResource gatheringResource : GatheringResource.values()) {
            if (gatheringResource.getGatheringSkill() != gatheringSkill) continue;
            if (gatheringLevel < gatheringResource.getRequiredLevel() && (next == null || gatheringResource.getRequiredLevel() < next.getRequiredLevel())) {
                next = gatheringResource;
            }
        }

        if (next != null) {
            String result = ChatColor.YELLOW + "You have " + ChatColor.WHITE +
                    (next.getRequiredLevel() - gatheringLevel) + ChatColor.YELLOW +
                    " level(s) left until you can gather " +
                    RunicItemsAPI.generateItemFromTemplate(next.getTemplateId()).getDisplayableItem().getDisplayName() +
                    ChatColor.YELLOW + "!";
            if (formatText) return ChatUtils.formattedText(result);
            return Collections.singletonList(result);
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
            try (Jedis jedis = RunicDatabase.getAPI().getRedisAPI().getNewJedisResource()) {
                gatheringData.writeToJedis(player.getUniqueId(), jedis);
            }
        });
    }
}
