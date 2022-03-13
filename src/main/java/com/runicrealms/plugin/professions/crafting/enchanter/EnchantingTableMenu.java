package com.runicrealms.plugin.professions.crafting.enchanter;

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

public class EnchantingTableMenu extends Workstation {

    private static final int ENCHANTER_MENU_SIZE = 54;

    public EnchantingTableMenu(Player player) {
        setupWorkstation(player);
    }

    @Override
    public void setupWorkstation(Player player) {

        // setup the menu
        super.setupWorkstation("&f&l" + player.getName() + "'s &e&lEnchanting Table");
        ItemGUI enchanterMenu = getItemGUI();

        //set the visual items
        enchanterMenu.setOption(3, new ItemStack(Material.REDSTONE),
                "&fCreate scrolls", "&7Enhance magic and cast rituals!", 0, false);

        // set the handler
        enchanterMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the bench menu
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(tableMenu(player));
                this.setTitle(tableMenu(player).getName());
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
        this.setItemGUI(enchanterMenu);
    }

    private ItemGUI tableMenu(Player player) {

        ItemGUI tableMenu = super.craftingMenu(player, ENCHANTER_MENU_SIZE);

        tableMenu.setOption(4, new ItemStack(Material.ENCHANTING_TABLE), "&eEnchanting Table",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(tableMenu, player);

        tableMenu.setHandler(event -> {

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
                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.SPELL_WITCH,
                                Sound.BLOCK_ENCHANTMENT_TABLE_USE, Sound.ENTITY_PLAYER_LEVELUP, mult, false
                        );
            }
        });

        return tableMenu;
    }

    private void setupItems(ItemGUI tableMenu, Player player) {
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_DEX_10, 9);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_DEX_20, 10);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_DEX_30, 11);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_DEX_40, 12);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_DEX_50, 13);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_DEX_60, 14);
//
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_WIS_10, 18);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_WIS_20, 19);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_WIS_30, 20);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_WIS_40, 21);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_WIS_50, 22);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_WIS_60, 23);
//
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_INT_10, 27);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_INT_20, 28);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_INT_30, 29);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_INT_40, 30);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_INT_50, 31);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_INT_60, 32);

        createMenuItem(tableMenu, player, CraftedResource.ENCHANTER_POWDER_10, 33);
        createMenuItem(tableMenu, player, CraftedResource.ENCHANTER_POWDER_20, 34);
        createMenuItem(tableMenu, player, CraftedResource.ENCHANTER_POWDER_30, 35);

//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_STR_10, 36);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_STR_20, 37);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_STR_30, 38);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_STR_40, 39);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_STR_50, 40);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_STR_60, 41);
//
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_VIT_10, 45);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_VIT_20, 46);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_VIT_30, 47);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_VIT_40, 48);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_VIT_50, 49);
//        createMenuItem(tableMenu, player, CraftedResource.OFFHAND_VIT_60, 50);

    }

    private CraftedResource determineItem(int slot) {
        switch (slot) {
//            case 9:
//                return CraftedResource.OFFHAND_DEX_10;
//            case 10:
//                return CraftedResource.OFFHAND_DEX_20;
//            case 11:
//                return CraftedResource.OFFHAND_DEX_30;
//            case 12:
//                return CraftedResource.OFFHAND_DEX_40;
//            case 13:
//                return CraftedResource.OFFHAND_DEX_50;
//            case 14:
//                return CraftedResource.OFFHAND_DEX_60;
//            case 18:
//                return CraftedResource.OFFHAND_WIS_10;
//            case 19:
//                return CraftedResource.OFFHAND_WIS_20;
//            case 20:
//                return CraftedResource.OFFHAND_WIS_30;
//            case 21:
//                return CraftedResource.OFFHAND_WIS_40;
//            case 22:
//                return CraftedResource.OFFHAND_WIS_50;
//            case 23:
//                return CraftedResource.OFFHAND_WIS_60;
//            case 27:
//                return CraftedResource.OFFHAND_INT_10;
//            case 28:
//                return CraftedResource.OFFHAND_INT_20;
//            case 29:
//                return CraftedResource.OFFHAND_INT_30;
//            case 30:
//                return CraftedResource.OFFHAND_INT_40;
//            case 31:
//                return CraftedResource.OFFHAND_INT_50;
//            case 32:
//                return CraftedResource.OFFHAND_INT_60;
            case 33:
                return CraftedResource.ENCHANTER_POWDER_10;
            case 34:
                return CraftedResource.ENCHANTER_POWDER_20;
            case 35:
                return CraftedResource.ENCHANTER_POWDER_30;
//            case 36:
//                return CraftedResource.OFFHAND_STR_10;
//            case 37:
//                return CraftedResource.OFFHAND_STR_20;
//            case 38:
//                return CraftedResource.OFFHAND_STR_30;
//            case 39:
//                return CraftedResource.OFFHAND_STR_40;
//            case 40:
//                return CraftedResource.OFFHAND_STR_50;
//            case 41:
//                return CraftedResource.OFFHAND_STR_60;
//            case 45:
//                return CraftedResource.OFFHAND_VIT_10;
//            case 46:
//                return CraftedResource.OFFHAND_VIT_20;
//            case 47:
//                return CraftedResource.OFFHAND_VIT_30;
//            case 48:
//                return CraftedResource.OFFHAND_VIT_40;
//            case 49:
//                return CraftedResource.OFFHAND_VIT_50;
//            case 50:
//                return CraftedResource.OFFHAND_VIT_60;
        }
        return CraftedResource.ENCHANTER_POWDER_10;
    }
}
