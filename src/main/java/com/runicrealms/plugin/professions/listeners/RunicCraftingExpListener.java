package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.donor.boost.api.BoostExperienceType;
import com.runicrealms.plugin.professions.event.RunicCraftingExpEvent;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RunicCraftingExpListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCraftingExpGainBoost(RunicCraftingExpEvent event) {
        double boost = RunicCore.getBoostAPI().getAdditionalExperienceMultiplier(BoostExperienceType.CRAFTING);
        if (boost > 0) event.setBonus(RunicCraftingExpEvent.BonusType.BOOST, boost);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftingExpGain(RunicCraftingExpEvent event) {
        if (event.isCancelled()) return;
        ProfExpUtil.giveCraftingExperience(event.getPlayer(), event.getFinalAmount());
    }

}
