package com.runicrealms.plugin.professions.commands;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfLevelCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Correct usage: /proflevel {player} {level}");
            return true;
        }

        Player pl = Bukkit.getPlayer(args[0]);
        if (pl == null) return true;

        int level = Integer.parseInt(args[1]);
        int exp = ProfExpUtil.calculateTotalExperience(level+1);

        RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).setProfExp(0);
        ProfExpUtil.giveExperience(pl, exp, false);
        RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).setProfLevel(level);
        RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).setProfExp(exp);

        return true;
    }
}