package com.runicrealms.plugin.professions.crafting.alchemist;

import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.WorkstationType;
import com.runicrealms.plugin.professions.config.WorkstationLoader;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.plugin.professions.RunicProfessions;
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

public class CauldronMenu extends Workstation {

    private static final int CAULDRON_MENU_SIZE = 54;

    public CauldronMenu(Player player) {
        super(WorkstationLoader.getMaxPages().get(WorkstationType.CAULDRON));
        setupWorkstation(player);
    }

    private ItemGUI cauldronMenu(Player player) {

        ItemGUI cauldronMenu = super.craftingMenu(player, CAULDRON_MENU_SIZE);

        for (int i = 0; i < 9; i++) {
            cauldronMenu.setOption(i, GUIUtil.BORDER_ITEM);
        }

        cauldronMenu.setOption(0, GUIUtil.CLOSE_BUTTON);

        cauldronMenu.setOption
                (

                        4,
                        new ItemStack(Material.CAULDRON),
                        "&eCauldron",
                        "&6&lClick &7an item to start crafting!",
                        0,
                        false
                );

        super.setupItems(player, cauldronMenu, WorkstationType.CAULDRON);

        cauldronMenu.setHandler(event -> {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
            }
            event.setWillClose(false);
            event.setWillDestroy(false);

            if (event.getSlot() == 0) {

                // close editor
                event.setWillClose(true);
                event.setWillDestroy(true);

            } else if (event.getSlot() > 8) { // first row for ui
                int multiplier = 1;
                if (event.isRightClick())
                    multiplier = 5;
                ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                if (meta == null) return;
                int slot = event.getSlot();
                CraftedResource craftedResource = super.determineCraftedResource(WorkstationType.CAULDRON, slot);
                event.setWillClose(true);
                event.setWillDestroy(true);
                startCrafting
                        (
                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.WATER_SPLASH,
                                Sound.BLOCK_BREWING_STAND_BREW, Sound.ENTITY_GENERIC_DRINK, multiplier, false
                        );
            }
        });
        return cauldronMenu;
    }

    @Override
    public void setupWorkstation(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(), () -> {
            this.setItemGUI(cauldronMenu(player));
            this.setTitle(cauldronMenu(player).getName());
            Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> this.getItemGUI().open(player));
        });
    }
}
