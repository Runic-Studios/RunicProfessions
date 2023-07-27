package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.api.event.ScoreboardUpdateEvent;
import com.runicrealms.plugin.professions.model.CraftingData;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ScoreboardListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH) // late
    public void onScoreboardUpdate(ScoreboardUpdateEvent event) {
        try {
            UUID uuid = event.getPlayer().getUniqueId();
            int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getPlayer().getUniqueId());
            CraftingData craftingData = RunicProfessions.getDataAPI().loadCraftingData(uuid, slot);
            event.setProfession(craftingData.getProfName());
            event.setProfessionLevel(craftingData.getProfLevel());
        } catch (Exception ex) {
            Bukkit.getLogger().warning("RunicProfessions failed to update scoreboard for " + event.getPlayer().getUniqueId());
        }
    }
}
