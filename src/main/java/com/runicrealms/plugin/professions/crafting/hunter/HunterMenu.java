package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.utilities.ColorUtil;
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
        HunterPlayer player = RunicProfessions.getHunterCache().getPlayers().get(pl.getUniqueId());

        // check whether player has a task
        boolean hasTask = player.getTask() != null;

        if (!hasTask) {
            hMenu.setOption(3, new ItemStack(Material.BOW),
                    "&fAccept New Task",
                    "&7Your Hunter Points: &6&l" + player.getHunterPoints() +
                            "\n&aStart a hunter task!\n&7Hunt specific monsters in the\n&7world and earn points!",
                    0, false);
        } else {
            hMenu.setOption(3, new ItemStack(Material.ZOMBIE_HEAD),
                    "&fGet Task Info",
                    "&7Your Hunter Points: &6&l" + player.getHunterPoints() +
                            "\n&7You have a current task!\n&aClick here for information\n&aon your task!",
                    0, false);
        }

        // set the handler
        if (!hasTask) {
            hMenu.setHandler(event -> {

                if (event.getSlot() == 3) {

                    // accept a task if they don't have one
                    RunicProfessions.getHunterCache().getPlayers().get(pl.getUniqueId()).newTask();
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
                    pl.sendMessage(ColorUtil.format("&r&aYour current task is to hunt &r&f" + player.getMaxHunterKills() + " " + player.getTask().getName() + "&r&as"));
                    pl.sendMessage(ColorUtil.format("&r&aSo far, you have slain &r&f" + player.getHunterKills() + "&r&a!"));
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

    @Override
    protected void produceResult(Player player, int numberOfItems, int inventorySlot) {
    }
}
