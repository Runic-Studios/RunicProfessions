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
        createMenuItem(forgeMenu, player, CraftedResource.CUT_RUBY_II, 10);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_RUBY_III, 11);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_RUBY_IV, 12);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_RUBY_V, 13);

        createMenuItem(forgeMenu, player, CraftedResource.CUT_SAPPHIRE_I, 18);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_SAPPHIRE_II, 19);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_SAPPHIRE_III, 20);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_SAPPHIRE_IV, 21);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_SAPPHIRE_V, 22);

        createMenuItem(forgeMenu, player, CraftedResource.CUT_DIAMOND_I, 27);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_DIAMOND_II, 28);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_DIAMOND_III, 29);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_DIAMOND_IV, 30);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_DIAMOND_V, 31);

        createMenuItem(forgeMenu, player, CraftedResource.CUT_EMERALD_I, 36);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_EMERALD_II, 37);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_EMERALD_III, 38);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_EMERALD_IV, 39);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_EMERALD_V, 40);

        createMenuItem(forgeMenu, player, CraftedResource.CUT_OPAL_I, 45);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_OPAL_II, 46);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_OPAL_III, 47);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_OPAL_IV, 48);
        createMenuItem(forgeMenu, player, CraftedResource.CUT_OPAL_V, 49);
    }

    private CraftedResource determineItem(int slot) {
        switch (slot) {
            case 9:
                return CraftedResource.CUT_RUBY_I;
            case 10:
                return CraftedResource.CUT_RUBY_II;
            case 11:
                return CraftedResource.CUT_RUBY_III;
            case 12:
                return CraftedResource.CUT_RUBY_IV;
            case 13:
                return CraftedResource.CUT_RUBY_V;
            case 18:
                return CraftedResource.CUT_SAPPHIRE_I;
            case 19:
                return CraftedResource.CUT_SAPPHIRE_II;
            case 20:
                return CraftedResource.CUT_SAPPHIRE_III;
            case 21:
                return CraftedResource.CUT_SAPPHIRE_IV;
            case 22:
                return CraftedResource.CUT_SAPPHIRE_V;
            case 27:
                return CraftedResource.CUT_DIAMOND_I;
            case 28:
                return CraftedResource.CUT_DIAMOND_II;
            case 29:
                return CraftedResource.CUT_DIAMOND_III;
            case 30:
                return CraftedResource.CUT_DIAMOND_IV;
            case 31:
                return CraftedResource.CUT_DIAMOND_V;
            case 36:
                return CraftedResource.CUT_EMERALD_I;
            case 37:
                return CraftedResource.CUT_EMERALD_II;
            case 38:
                return CraftedResource.CUT_EMERALD_III;
            case 39:
                return CraftedResource.CUT_EMERALD_IV;
            case 40:
                return CraftedResource.CUT_EMERALD_V;
            case 45:
                return CraftedResource.CUT_OPAL_I;
            case 46:
                return CraftedResource.CUT_OPAL_II;
            case 47:
                return CraftedResource.CUT_OPAL_III;
            case 48:
                return CraftedResource.CUT_OPAL_IV;
            case 49:
                return CraftedResource.CUT_OPAL_V;
        }
        return CraftedResource.CUT_RUBY_I;
    }
}
