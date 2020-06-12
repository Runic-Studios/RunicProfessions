package com.runicrealms.plugin.professions.crafting.jeweler;

import com.runicrealms.plugin.events.HealthRegenEvent;
import com.runicrealms.plugin.events.ManaRegenEvent;
import com.runicrealms.plugin.item.GearScanner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegenListener implements Listener {

    @EventHandler
    public void onHealthRegen(HealthRegenEvent e) {
        e.setAmount(e.getAmount() + GearScanner.getHealthRegenBoost(e.getPlayer()));
    }

    @EventHandler
    public void onManaRegen(ManaRegenEvent e) {
        e.setAmount(e.getAmount() + GearScanner.getManaRegenBoost(e.getPlayer()));
    }

}
