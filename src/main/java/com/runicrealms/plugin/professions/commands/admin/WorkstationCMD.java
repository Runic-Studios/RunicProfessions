package com.runicrealms.plugin.professions.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import com.runicrealms.plugin.RunicProfessions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

/**
 * Command to remove old workstations
 */
@CommandAlias("workstation")
@CommandPermission("runic.op")
public class WorkstationCMD extends BaseCommand {

    // workstation delete

    @Subcommand("delete")
    @Conditions("is-console-or-op")
    public void onCommandGatheringLevel(Player player) {
        Map<Location, String> storedStationLocations = RunicProfessions.getAPI().getStoredStationLocations();
        Iterator<Map.Entry<Location, String>> iterator = storedStationLocations.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Location, String> entry = iterator.next();
            Location location = entry.getKey();

            if (player.getLocation().distanceSquared(location) < 25) {
                iterator.remove();
                RunicProfessions.getAPI().removeWorkstation(location);
            }
        }

        player.sendMessage(ChatColor.GREEN + "Workstations deleted!");
    }

}
