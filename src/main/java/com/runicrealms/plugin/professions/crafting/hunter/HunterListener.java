package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicCore;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

// todo: CACHE this
public class HunterListener implements Listener {

    @EventHandler
    public void onHunterMobDeath(MythicMobDeathEvent e) {

        // verify that a hunter is on-task
        if (!(e.getKiller() instanceof Player)) return;
        Player pl = (Player) e.getKiller();
        String mobInternal = e.getMobType().getInternalName();
        String playerTask = RunicCore.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob");
        if (!mobInternal.equals(playerTask)) return;

        int totalKills = HunterTask.getCurrentKills(pl);
        boolean sendMsg = true;

        if (totalKills+1 < HunterTask.getMobAmount()) {
            RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_kills", totalKills+1);
        } else {
            sendMsg = false;
            HunterTask.givePoints(pl);
            pl.sendMessage
                    (ChatColor.GREEN + "You have completed your hunter task and receive " +
                            ChatColor.GOLD + ChatColor.BOLD + HunterTask.getEarnedPoints(pl) + " points!" +
                            ChatColor.GREEN + " Return to a hunting board for another task.");
            launchFirework(pl);
            RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_mob", null);
            RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_kills", null);
            RunicCore.getInstance().saveConfig();
            RunicCore.getInstance().reloadConfig();
        }

        // give experience
        HunterTask.giveExperience(pl, sendMsg);
    }

    private void launchFirework(Player pl) {
        Firework firework = pl.getWorld().spawn(pl.getEyeLocation(), Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(0);
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).build());
        firework.setFireworkMeta(meta);
    }

    // todo:
    // potion listener
    // orb listener
    // scroll listener
    // enchant listener
    // compass listener
}
