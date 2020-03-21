package com.runicrealms.plugin.professions.commands;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetProfCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Correct usage: /setprof {player} {prof} {admin?}");
            return true;
        }

        Player pl = Bukkit.getPlayer(args[0]);
        if (pl == null) return true;

        String profStr = args[1].toLowerCase();
        if (!(profStr.equals("alchemist")
                || profStr.equals("blacksmith")
                || profStr.equals("enchanter")
                || profStr.equals("hunter")
                || profStr.equals("jeweler"))) {

            sender.sendMessage(ChatColor.RED
                    + "Available classes: alchemist, blacksmith, enchanter, hunter, jeweler");
            return true;
        }

        String formattedStr = profStr.substring(0, 1).toUpperCase() + profStr.substring(1);

        String isAdmin = args[2];;

        if (isAdmin.toLowerCase().equals("true")) {
            updateCache(pl, formattedStr);
            RunicCore.getScoreboardHandler().updatePlayerInfo(pl);
            RunicCore.getScoreboardHandler().updateSideInfo(pl);
            return true;
        } else {
            if (RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).getProfName().toLowerCase().equals("none")) {
                updateCache(pl, formattedStr);
                RunicCore.getScoreboardHandler().updatePlayerInfo(pl);
                RunicCore.getScoreboardHandler().updateSideInfo(pl);
            } else {
                pl.playSound(pl.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.0f);
                pl.sendMessage(ChatColor.RED + "You have already chosen your profession! To change it, visit a profession tutor in a city.");
            }
        }
        return true;
    }

    private static void updateCache(Player pl, String profName) {

        RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).setProfName(profName);
        RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).setProfLevel(0);
        RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).setProfExp(0);

        /*
        Reset hunter info
         */
        RunicProfessions.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_mob", null);
        RunicProfessions.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_points", null);

        RunicProfessions.getInstance().saveConfig();
        RunicProfessions.getInstance().reloadConfig();
    }

}
