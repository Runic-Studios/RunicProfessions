package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.donor.boost.api.BoostExperienceType;
import com.runicrealms.plugin.professions.event.RunicGatheringExpEvent;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RunicGatheringExpListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onGatheringExpGainBoost(RunicGatheringExpEvent event) {
        double boost = RunicCore.getBoostAPI().getAdditionalExperienceMultiplier(BoostExperienceType.GATHERING);
        if (boost > 0) event.setBonus(RunicGatheringExpEvent.BonusType.BOOST, boost);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGatheringExpGain(RunicGatheringExpEvent event) {
        if (event.isCancelled()) return;
        ProfExpUtil.giveGatheringExperience(event.getPlayer(), event.getSkill(), event.getFinalAmount());
    }

}
