package com.runicrealms.plugin.professions.commands;

import com.runicrealms.plugin.professions.gathering.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GathertoolGive implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        Player pl = Bukkit.getPlayer(args[1]);
        if (pl == null) return false;
        if (!sender.isOp()) return false;
        String toolName = args[2];
        int tier = Integer.parseInt(args[3]);

        switch (toolName.toLowerCase()) {
            case "axe":
                pl.getInventory().addItem(GatheringUtil.getGatheringTool(Material.IRON_AXE, tier));
                break;
            case "hoe":
                pl.getInventory().addItem(GatheringUtil.getGatheringTool(Material.IRON_HOE, tier));
                break;
            case "pickaxe":
                pl.getInventory().addItem(GatheringUtil.getGatheringTool(Material.IRON_PICKAXE, tier));
                break;
            case "rod":
                pl.getInventory().addItem(GatheringUtil.getGatheringTool(Material.FISHING_ROD, tier));
                break;
            default:
                pl.sendMessage(ChatColor.DARK_RED + "Please choose a tool: axe, hoe, pickaxe, or rod");
        }
        return true;
    }


//    @Override
//    public void onConsoleCommand(CommandSender sender, String[] args)  {
//
//        Player pl = Bukkit.getPlayer(args[1]);
//        if (pl == null) return;
//        String toolName = args[2];
//        int tier = Integer.parseInt(args[3]);
//
//        switch (toolName.toLowerCase()) {
//            case "axe":
//                pl.getInventory().addItem(GatheringUtil.getGatheringTool(Material.IRON_AXE, tier));
//                break;
//            case "hoe":
//                pl.getInventory().addItem(GatheringUtil.getGatheringTool(Material.IRON_HOE, tier));
//                break;
//            case "pickaxe":
//                pl.getInventory().addItem(GatheringUtil.getGatheringTool(Material.IRON_PICKAXE, tier));
//                break;
//            case "rod":
//                pl.getInventory().addItem(GatheringUtil.getGatheringTool(Material.FISHING_ROD, tier));
//                break;
//            default:
//                pl.sendMessage(ChatColor.DARK_RED + "Please choose a tool: axe, hoe, pickaxe, or rod");
//        }
//    }
//
//    @Override
//    public void onOPCommand(Player sender, String[] args) {
//
//        if(args.length != 4) {
//            sender.sendMessage(ChatColor.YELLOW + "Command usage: /gathertool give [player] [tool] [tier]");
//            return;
//        }
//
//        this.onConsoleCommand(sender, args);
//    }
//
//    @Override
//    public void onUserCommand(Player sender, String[] args) {
//    }
//
//    @Override
//    public String permissionLabel() {
//        return "tool.give";
//    }
//
//    @Override
//    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
//        return null;
//        //return TabCompleteUtil.getPlayers(commandSender, strings, RunicProfessions.getInstance());
//    }
}
