package com.runicrealms.plugin.professions.crafting;

import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.WorkstationType;
import com.runicrealms.plugin.professions.config.WorkstationLoader;
import com.runicrealms.plugin.common.util.GUIUtil;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class WoodworkingMenu extends Workstation {

    public WoodworkingMenu(Player player) {
        super(WorkstationLoader.getMaxPages().get(WorkstationType.WOODWORKING_TABLE));
        setupWorkstation(player);
    }

    private ItemGUI menu(Player player) {

        ItemGUI menu = super.craftingMenu(player, 54);

        for (int i = 0; i < 9; i++) {
            menu.setOption(i, GUIUtil.BORDER_ITEM);
        }

        menu.setOption(0, GUIUtil.CLOSE_BUTTON);

        menu.setOption(4, new ItemStack(Material.IRON_AXE), "&eWoodworking Table",
                "&6&lClick &7an item to start crafting!", 1, false);

        super.setupItems(player, menu, WorkstationType.WOODWORKING_TABLE);

        menu.setHandler(event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
            event.setWillClose(false);
            event.setWillDestroy(false);

            if (event.getSlot() == 0) {

                // close editor
                event.setWillClose(true);
                event.setWillDestroy(true);

            } else if (event.getSlot() > 8) { // First row reserved for navigation/info buttons

                int multiplier = 1;
                if (event.isRightClick()) multiplier = 5;
                ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                if (meta == null) return;
                CraftedResource craftedResource = super.determineCraftedResource(WorkstationType.WOODWORKING_TABLE, event.getSlot());
                event.setWillClose(true);
                event.setWillDestroy(true);
                // todo: update
                startCrafting
                        (
                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.SMOKE_NORMAL,
                                Sound.ENTITY_GHAST_SHOOT, Sound.BLOCK_LAVA_EXTINGUISH, multiplier, true
                        );
            }
        });

        return menu;
    }

    @Override
    public void setupWorkstation(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(), () -> {
            this.setItemGUI(menu(player));
            this.setTitle(menu(player).getName());
            Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> this.getItemGUI().open(player));
        });
    }
}
