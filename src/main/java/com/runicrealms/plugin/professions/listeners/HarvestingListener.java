package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.events.MagicDamageEvent;
import com.runicrealms.plugin.events.PhysicalDamageEvent;
import com.runicrealms.plugin.professions.model.GatheringData;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HarvestingListener implements Listener {

    public static final String HERB_FACTION = "Herb";

    @EventHandler
    public void onMythicMobHerbDeath(MagicDamageEvent e) {
        if (!MythicBukkit.inst().getMobManager().isActiveMob(e.getVictim().getUniqueId())) return;
        ActiveMob activeMob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(e.getVictim());
        String faction = activeMob.getFaction();
        if (faction == null) return;
        if (!faction.equals(HERB_FACTION)) return;
        if (!verifyPlayerHarvestingLevel(e.getPlayer(), activeMob))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST) // fires first
    public void onMythicMobHerbDeath(PhysicalDamageEvent e) {
        if (!MythicBukkit.inst().getMobManager().isActiveMob(e.getVictim().getUniqueId())) return;
        ActiveMob activeMob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(e.getVictim());
        String faction = activeMob.getFaction();
        if (faction == null) return;
        if (!faction.equals(HERB_FACTION)) return;
        if (!verifyPlayerHarvestingLevel(e.getPlayer(), activeMob))
            e.setCancelled(true);
    }

    /**
     * Verifies that a player can harvest a MythicMob herb using the mob's level option
     *
     * @param player    who is attempting to harvest herb
     * @param activeMob the herb to be harvested
     * @return true if the player has met the harvesting level requirement
     */
    private boolean verifyPlayerHarvestingLevel(Player player, ActiveMob activeMob) {
        GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(player.getUniqueId());
        int level = (int) activeMob.getLevel();
        int harvestingLevel = gatheringData.getHarvestingLevel();
        if (harvestingLevel < level) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
            player.sendMessage(ChatColor.RED + "Your harvesting level is too low to gather this!");
            return false;
        } else {
            return true;
        }

    }
}
