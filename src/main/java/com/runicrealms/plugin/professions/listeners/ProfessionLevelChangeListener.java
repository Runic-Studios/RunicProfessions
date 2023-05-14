package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.event.ProfessionLevelChangeEvent;
import com.runicrealms.plugin.professions.model.CraftingData;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ProfessionLevelChangeListener implements Listener {

    @EventHandler
    public void onProfessionLevelChange(ProfessionLevelChangeEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        int slot = RunicCore.getCharacterAPI().getCharacterSlot(uuid);
        CraftingData craftingData = RunicProfessions.getDataAPI().loadCraftingData(uuid, slot);
        Player player = event.getPlayer();
        String profession = event.getProfession().getName();
        craftingData.setProfLevel(event.getNewLevel());
        craftingData.writeToJedis(uuid, event.getJedis(), slot);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        if (event.getNewLevel() == ProfExpUtil.MAX_CRAFTING_PROF_LEVEL) {
            player.sendTitle(
                    ChatColor.GOLD + "Max Level!",
                    ChatColor.GOLD + profession + " Level " + ChatColor.WHITE + event.getNewLevel(), 10, 40, 10);
        } else {
            player.sendTitle(
                    ChatColor.GREEN + "Level Up!",
                    ChatColor.GREEN + profession + " Level " + ChatColor.WHITE + event.getNewLevel(), 10, 40, 10);
        }
        RunicCore.getScoreboardAPI().updatePlayerScoreboard(player);
    }
}