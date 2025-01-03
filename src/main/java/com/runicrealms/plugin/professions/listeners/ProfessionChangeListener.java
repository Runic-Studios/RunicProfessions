package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.professions.model.CraftingData;
import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.professions.event.ProfessionChangeEvent;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

/**
 * Updates jedis upon profession change
 */
public class ProfessionChangeListener implements Listener {

    @EventHandler
    public void onProfessionChange(ProfessionChangeEvent event) {
        int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(event.getPlayer().getUniqueId());
        try (Jedis jedis = RunicDatabase.getAPI().getRedisAPI().getNewJedisResource()) {
            CraftingData craftingData = RunicProfessions.getDataAPI().loadCraftingData(event.getPlayer().getUniqueId(), slot);
            craftingData.setProfName(event.getProfession().getName());
            craftingData.setProfLevel(0);
            craftingData.setProfExp(0);
            craftingData.writeToJedis(event.getPlayer().getUniqueId(), jedis, slot);
            RunicCore.getScoreboardAPI().updatePlayerScoreboard(event.getPlayer());
        }
    }
}
