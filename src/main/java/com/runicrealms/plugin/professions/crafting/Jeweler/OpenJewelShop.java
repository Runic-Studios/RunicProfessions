package com.runicrealms.plugin.professions.crafting.Jeweler;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenJewelShop implements CommandExecutor {

    /**
     * Opens the artifact forge for the given player
     */
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {

        // jewelmaster (player)
        Player pl = Bukkit.getPlayer(args[0]);
        if (pl == null) return false;

        pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
        RunicCore.getShopManager().setPlayerShop(pl, new JewelMaster(pl));
        ItemGUI jewelShop = ((RunicCore.getShopManager().getPlayerShop(pl))).getItemGUI();
        jewelShop.open(pl);
        return true;
    }
}