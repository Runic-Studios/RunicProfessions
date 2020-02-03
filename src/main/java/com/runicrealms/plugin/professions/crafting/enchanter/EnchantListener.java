package com.runicrealms.plugin.professions.crafting.enchanter;

import com.runicrealms.plugin.events.MobDamageEvent;
import com.runicrealms.plugin.events.SpellDamageEvent;
import com.runicrealms.plugin.events.WeaponDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EnchantListener implements Listener {

    @EventHandler
    public void onSpellDamage(SpellDamageEvent e) {
        // listen for crit if attacker has crit, dodge and thorns for defender
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEvent e) {
        // listen for crit if attacker has crit, dodge and thorns for defender
    }

    @EventHandler
    public void onMobDamage(MobDamageEvent e) {
        // listen for dodge and thorns for defender
    }
}
