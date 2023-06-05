package com.runicrealms.plugin.professions.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.event.RunicCraftingExpEvent;
import com.runicrealms.plugin.professions.event.RunicGatheringExpEvent;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@CommandAlias("profgive")
public class ProfGiveCMD extends BaseCommand {

    public ProfGiveCMD() {
        RunicProfessions.getCommandManager().getCommandCompletions().registerAsyncCompletion("gatheringSkills", context -> {
            Set<String> gatheringSkills = new HashSet<>();
            for (GatheringSkill gatheringSkill : GatheringSkill.values())
                gatheringSkills.add(gatheringSkill.getIdentifier());
            return gatheringSkills;
        });
    }

    // profgive gatheringexp [player] [skill] [amount]

    @Subcommand("gatheringexp")
    @CommandCompletion("@players @gatheringSkills @nothing")
    @Conditions("is-console-or-op")
    public void onCommandGatheringExp(CommandSender commandSender, String[] args) {
        if (args.length != 3) {
            commandSender.sendMessage(ChatColor.RED + "Error, incorrect number of arguments.");
            commandSender.sendMessage(ChatColor.YELLOW + "Format: runicgive gatheringexp [player] [skill] [amount]");
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return;
        GatheringSkill gatheringSkill = GatheringSkill.getFromIdentifier(args[1]);
        if (gatheringSkill == null) return;
        // skip all other calculations for quest exp
        int exp = Integer.parseInt(args[2]);
        Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new RunicGatheringExpEvent(exp, true, player, gatheringSkill));
        });
    }

    // profgive profexp [player] [amount]

    @Subcommand("profexp")
    @Syntax("<player> <level>")
    @CommandCompletion("@online @nothing")
    @Conditions("is-console-or-op")
    public void onCommandProfExp(CommandSender commandSender, String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "Error, incorrect number of arguments.");
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return;
        int exp = Integer.parseInt(args[1]);
        Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new RunicCraftingExpEvent(exp, true, player));
        });
    }
}
