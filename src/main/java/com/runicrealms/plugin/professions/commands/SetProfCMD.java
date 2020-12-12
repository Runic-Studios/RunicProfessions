package com.runicrealms.plugin.professions.commands;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.utilities.CurrencyUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetProfCMD implements CommandExecutor {

    private static final int PRICE = 256;

    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Correct usage: /setprof {player} {prof} {admin?}");
            return true;
        }

        Player pl = Bukkit.getPlayer(args[0]);
        if (pl == null) return true;
        if (!sender.isOp()) return false;

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
            return true;
        } else if (isAdmin.toLowerCase().equals("tutor")) {
            if (RunicCoreAPI.getPlayerCache(pl).getProfName().toLowerCase().equals("none")) {
                updateCache(pl, formattedStr);
            } else {
                if (pl.getInventory().contains(Material.GOLD_NUGGET, PRICE)) {
                    ItemRemover.takeItem(pl, CurrencyUtil.goldCoin(), PRICE);
                    updateCache(pl, formattedStr);
                    pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                    pl.sendMessage(ChatColor.GREEN + "You have changed your profession!");
                } else {
                    pl.playSound(pl.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.0f);
                    pl.sendMessage(ChatColor.RED + "You do not have enough gold!");
                }
            }
        } else {
            if (RunicCoreAPI.getPlayerCache(pl).getProfName().toLowerCase().equals("none")) {
                updateCache(pl, formattedStr);
            } else {
                pl.playSound(pl.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.0f);
                pl.sendMessage(ChatColor.RED + "You have already chosen your profession! To change it, visit a profession tutor in a city.");
            }
        }
        return true;
    }

    private static void updateCache(Player pl, String profName) {

        RunicCoreAPI.getPlayerCache(pl).setProfName(profName);
        RunicCoreAPI.getPlayerCache(pl).setProfLevel(0);
        RunicCoreAPI.getPlayerCache(pl).setProfExp(0);

        /*
        Reset hunter info
         */
        RunicProfessions.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_mob", null);
        RunicProfessions.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_points", null);

        RunicProfessions.getInstance().saveConfig();
        RunicProfessions.getInstance().reloadConfig();
    }

}
