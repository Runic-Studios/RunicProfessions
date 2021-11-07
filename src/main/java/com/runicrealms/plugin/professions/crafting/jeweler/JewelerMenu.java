package com.runicrealms.plugin.professions.crafting.jeweler;

import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class JewelerMenu extends Workstation {

    private static final int JEWELER_MENU_SIZE = 54;

    public JewelerMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player player) {

        // setup the menu
        super.setupWorkstation("&f&l" + player.getName() + "'s &e&lGemcutting Bench");
        ItemGUI jewelerMenu = getItemGUI();

        //set the visual items
        jewelerMenu.setOption(3, new ItemStack(Material.REDSTONE),
                "&fCut Gems", "&7Create gemstones and enhance armor!", 0, false);

        // set the handler
        jewelerMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the bench menu
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(benchMenu(player));
                this.setTitle(benchMenu(player).getName());
                this.getItemGUI().open(player);

                event.setWillClose(false);
                event.setWillDestroy(true);

            } else if (event.getSlot() == 5) {

                // close editor
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        });

        // update our internal menu
        this.setItemGUI(jewelerMenu);
    }

    private ItemGUI benchMenu(Player player) {

        ItemGUI benchMenu = super.craftingMenu(player, JEWELER_MENU_SIZE);

        benchMenu.setOption(4, new ItemStack(Material.STONECUTTER), "&eGemcutting Bench",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(benchMenu, player);

        benchMenu.setHandler(event -> {

            if (event.getSlot() == 4) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                setupWorkstation(player);
                this.getItemGUI().open(player);
                event.setWillClose(false);
                event.setWillDestroy(true);

            } else {

                int mult = 1;
                if (event.isRightClick()) mult = 5;
                ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                if (meta == null) return;
                int slot = event.getSlot();
                CraftedResource craftedResource = determineItem(slot);
                event.setWillClose(true);
                event.setWillDestroy(true);
                startCrafting
                        (
                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.FIREWORKS_SPARK,
                                Sound.BLOCK_ANVIL_PLACE, Sound.BLOCK_ANVIL_USE, mult, false
                        );
            }
        });

        return benchMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player player) {
        createMenuItem(forgeMenu, player, CraftedResource.CUT_RUBY_I, 9);
    }

    private CraftedResource determineItem(int slot) {
        switch (slot) {
            case 9:
                return CraftedResource.CUT_RUBY_I;
        }
        return CraftedResource.CUT_RUBY_I;
    }
}
