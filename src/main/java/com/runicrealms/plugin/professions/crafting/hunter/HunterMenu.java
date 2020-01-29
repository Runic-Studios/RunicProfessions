package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HunterMenu extends Workstation {

    public HunterMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player pl) {

        // name the menu
        super.setupWorkstation("&f&l" + pl.getName() + "'s &e&lHunting Board");
        ItemGUI hMenu = getItemGUI();

        // check whether player has a task
        boolean hasTask = false;
        if (RunicCore.getInstance().getConfig().getString(pl.getUniqueId() + ".info.prof.hunter_mob") != null) hasTask = true;

        if (!hasTask) {
            hMenu.setOption(3, new ItemStack(Material.BOW),
                    "&fAccept New Task", "&7Start a hunter task!\n&7Slay specific monsters in the world\n&7and earn points!", 0, false);
        } else {
            hMenu.setOption(3, new ItemStack(Material.ZOMBIE_HEAD),
                    "&fGet Task Info", "&7You have a current task!\n&7Click here for information\n&7on your task!", 0, false);
        }

        // set the handler
        if (!hasTask) {
            hMenu.setHandler(event -> {

                if (event.getSlot() == 3) {

                    // accept a task if they don't have one
                    pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    HunterTask hunterTask = new HunterTask(pl);
                    pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                    pl.sendMessage
                            (ChatColor.GREEN + "Your new task is to slay " +
                                    ChatColor.WHITE + HunterTask.getMobAmount() + " " +
                                    ChatColor.GREEN + hunterTask.getMob().getInternalName() + "s!");
                    event.setWillClose(true);
                    event.setWillDestroy(true);

                } else if (event.getSlot() == 5) {

                    // close editor
                    pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    event.setWillClose(true);
                    event.setWillDestroy(true);
                }
            });
        } else {
            hMenu.setHandler(event -> {

                if (event.getSlot() == 3) {

                    // get task info
                    pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    pl.sendMessage
                            (ChatColor.GREEN + "Your current task is to slay " +
                                    ChatColor.WHITE + HunterTask.getMobAmount() + " " );//+
                                    //ChatColor.GREEN + hunterTask.getMob().getInternalName() + "s!");
                    event.setWillClose(true);
                    event.setWillDestroy(true);

                } else if (event.getSlot() == 5) {

                    // close editor
                    pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    event.setWillClose(true);
                    event.setWillDestroy(true);
                }
            });
        }

        // update our internal menu
        this.setItemGUI(hMenu);
    }
}
