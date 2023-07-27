package com.runicrealms.plugin.professions.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.runicrealms.plugin.professions.model.CraftingData;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.event.GatheringLevelChangeEvent;
import com.runicrealms.plugin.professions.event.RunicCraftingExpEvent;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@CommandAlias("profset")
@CommandPermission("runic.op")
public class ProfSetCMD extends BaseCommand {

    public ProfSetCMD() {
        RunicProfessions.getCommandManager().getCommandCompletions().registerAsyncCompletion("online", context -> {
            Set<String> onlinePlayers = new HashSet<>();
            for (UUID uuid : RunicDatabase.getAPI().getCharacterAPI().getLoadedCharacters()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                onlinePlayers.add(player.getName());
            }
            return onlinePlayers;
        });
        RunicProfessions.getCommandManager().getCommandCompletions().registerAsyncCompletion("professions", context -> {
            Set<String> professions = new HashSet<>();
            for (Profession profession : Profession.values())
                professions.add(profession.getName());
            return professions;
        });
    }

    // profset gatheringlevel [player] [skill] [level]

    @Subcommand("gatheringlevel")
    @Syntax("<player> <skill> <level>")
    @CommandCompletion("@online @gatheringSkills @nothing")
    @Conditions("is-console-or-op")
    public void onCommandGatheringLevel(CommandSender commandSender, String[] args) {
        if (args.length != 3) {
            commandSender.sendMessage(ChatColor.RED + "Error, incorrect number of arguments. Usage: profset gatheringlevel [player] [skill] [level]");
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return;

        GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(player.getUniqueId());
        GatheringSkill gatheringSkill = GatheringSkill.getFromIdentifier(args[1]);
        if (gatheringSkill == null) return;
        int oldLevel = gatheringData.getGatheringLevel(gatheringSkill);
        int newLevel = Integer.parseInt(args[2]);
        // ----------------------
        // IMPORTANT: You can't set the exp to 0 here. It must be the expected experience at the profession level!
        int expAtLevel = ProfExpUtil.calculateTotalExperience(newLevel);
        // ----------------------
        gatheringData.setGatheringExp(gatheringSkill, expAtLevel + 1);
        // call a level change event to notify redis
        GatheringLevelChangeEvent gatheringLevelChangeEvent = new GatheringLevelChangeEvent
                (
                        player,
                        gatheringData,
                        gatheringSkill,
                        oldLevel,
                        newLevel
                );
        Bukkit.getPluginManager().callEvent(gatheringLevelChangeEvent);
    }

    // profset profession [player] [profession]

    @Subcommand("profession")
    @Syntax("<player> <profession>")
    @CommandCompletion("@online @professions")
    @Conditions("is-console-or-op")
    public void onCommandProfession(CommandSender commandSender, String[] args) {
        if (args.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "Correct usage: /profset profession [player] [profession]");
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return;

        String profStr = args[1];
        if (!(profStr.equalsIgnoreCase("alchemist")
                || profStr.equalsIgnoreCase("blacksmith")
                || profStr.equalsIgnoreCase("enchanter")
                || profStr.equalsIgnoreCase("hunter")
                || profStr.equalsIgnoreCase("jeweler"))) {

            commandSender.sendMessage(ChatColor.RED
                    + "Available classes: alchemist, blacksmith, enchanter, hunter, jeweler");
            return;
        }

        Profession profession = Profession.valueOf(profStr.toUpperCase());
        RunicProfessions.getAPI().changePlayerProfession(player, profession);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
        player.sendTitle
                (

                        ChatColor.DARK_GREEN + "Profession Learned!",
                        ChatColor.GREEN + "You are now a " + ChatColor.WHITE + profession.getName() + ChatColor.GREEN + "!",
                        10, 40, 10
                );
    }

    // profset professionlevel [player] [level]

    @Subcommand("professionlevel")
    @Syntax("<player> <level>")
    @CommandCompletion("@online @nothing")
    @Conditions("is-console-or-op")
    public void onCommandProfessionLevel(CommandSender commandSender, String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return;
        int level = Integer.parseInt(args[1]);
        int exp = ProfExpUtil.calculateTotalExperience(level + 1);
        int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(player.getUniqueId());
        CraftingData craftingData = RunicProfessions.getDataAPI().loadCraftingData(player.getUniqueId(), slot);
        craftingData.setProfExp(0);
        Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new RunicCraftingExpEvent(exp, false, player));
        });
    }
}
