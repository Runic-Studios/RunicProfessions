package com.runicrealms.plugin.professions.commands;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.scoreboard.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetProfCMD implements CommandExecutor {

    private ScoreboardHandler sbh = RunicCore.getScoreboardHandler();

    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Correct usage: /setprof {player} {prof}");
            return false;
        }

        Player pl = Bukkit.getPlayer(args[0]);
        if (pl == null) return false;

        String profStr = args[1].toLowerCase();
        if (!(profStr.equals("alchemist")
                || profStr.equals("blacksmith")
                || profStr.equals("enchanter")
                || profStr.equals("hunter")
                || profStr.equals("jeweler"))) {

            sender.sendMessage(ChatColor.RED
                    + "Available classes: alchemist, blacksmith, enchanter, hunter, jeweler");
            return false;
        }

        String formattedStr = profStr.substring(0, 1).toUpperCase() + profStr.substring(1);

        setConfig(pl, formattedStr);
        RunicCore.getScoreboardHandler().updatePlayerInfo(pl);
        RunicCore.getScoreboardHandler().updateSideInfo(pl);
        return true;
    }

    private static void setConfig(Player pl, String profName) {
        RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.name", profName);
        RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.level", 0);
        RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.exp", 0);

        /*
        Reset hunter info
         */
        RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_mob", null);
        RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_points", null);

        RunicCore.getInstance().saveConfig();
        RunicCore.getInstance().reloadConfig();
    }

}
