package com.runicrealms.plugin.professions.crafting.blacksmith;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.events.PhysicalDamageEvent;
import com.runicrealms.runicitems.item.RunicItem;
import com.runicrealms.runicitems.item.event.RunicItemGenericTriggerEvent;
import com.runicrealms.runicitems.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class StoneListener implements Listener {

    private static final String DATA_DAMAGE_STRING = "damage-buff"; // used in the runic item config
    private static final String DATA_DURATION_STRING = "duration";
    private static final Collection<String> SHARP_STONES = new HashSet<>() {{
        add("blacksmith-sharpstone-i");
        add("blacksmith-sharpstone-ii");
        add("blacksmith-sharpstone-iii");
    }};
    private final HashMap<UUID, Integer> boostedPlayers;

    public StoneListener() {
        boostedPlayers = new HashMap<>();
    }

    @EventHandler
    public void onPhysicalDamage(PhysicalDamageEvent event) {
        if (!event.isBasicAttack()) return;
        if (!boostedPlayers.containsKey(event.getPlayer().getUniqueId())) return;
        double bonus = boostedPlayers.get(event.getPlayer().getUniqueId());
        event.setAmount((int) (event.getAmount() + bonus));
        event.getVictim().getWorld().spawnParticle(Particle.CRIT, event.getVictim().getLocation(), 10, 0, 0, 0, 0);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRunicClickTrigger(RunicItemGenericTriggerEvent event) {
        if (event.isCancelled()) return;
        if (!SHARP_STONES.contains(event.getItem().getTemplateId())) return; // verify sharp stone used

        RunicItem runicItem = event.getItem();
        String bonus = runicItem.getData().get(DATA_DAMAGE_STRING);
        String duration = runicItem.getData().get(DATA_DURATION_STRING);
        int durationInt = Integer.parseInt(duration);
        Player player = event.getPlayer();

        ItemUtils.takeItem(player, event.getItemStack(), 1);
        boostedPlayers.put(player.getUniqueId(), Integer.valueOf(bonus));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 2.0f);

        player.sendMessage
                (
                        ChatColor.GREEN + "You consumed a " +
                                runicItem.getDisplayableItem().getDisplayName() +
                                ChatColor.GREEN + "! Your attacks now deal " +
                                bonus + " additional damage for "
                                + durationInt + "s."
                );

        Bukkit.getScheduler().scheduleSyncDelayedTask(RunicProfessions.getInstance(), () -> {
            boostedPlayers.remove(player.getUniqueId());
            event.getPlayer().sendMessage(ChatColor.GRAY + "Your attacks no longer deal additional damage.");
        }, durationInt * 20L);
    }
}
