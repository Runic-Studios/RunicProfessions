package com.runicrealms.plugin.professions.crafting.blacksmith;

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

public class AnvilMenu extends Workstation {

    private static final int ANVIL_MENU_SIZE = 54;

    public AnvilMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player pl) {

        // name the menu
        super.setupWorkstation("&f&l" + pl.getName() + "'s &e&lAnvil");
        ItemGUI blackSmithMenu = getItemGUI();

        //set the visual items
        blackSmithMenu.setOption(3, new ItemStack(Material.IRON_CHESTPLATE),
                "&fCraft Armor", "&7Forge mail, gilded or plate armor!", 0, false);

        // set the handler
        blackSmithMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the forging menu
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(forgeMenu(pl));
                this.setTitle(forgeMenu(pl).getName());
                this.getItemGUI().open(pl);

                event.setWillClose(false);
                event.setWillDestroy(true);

            } else if (event.getSlot() == 5) {

                // close editor
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        });

        // update our internal menu
        this.setItemGUI(blackSmithMenu);
    }

    private ItemGUI forgeMenu(Player player) {

        ItemGUI forgeMenu = super.craftingMenu(player, ANVIL_MENU_SIZE);

        forgeMenu.setOption(4, new ItemStack(Material.ANVIL), "&eAnvil",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(forgeMenu, player);

        forgeMenu.setHandler(event -> {

            if (event.getSlot() == 4) {

                // return to the first menu
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

        return forgeMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player player) {

        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_DEX_10, 9);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_DEX_20, 10);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_DEX_30, 11);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_DEX_40, 12);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_DEX_50, 13);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_DEX_60, 14);

        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_BOW_20, 15);
        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_BOW_30, 16);
        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_BOW_40, 17);

        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_WIS_10, 18);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_WIS_20, 19);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_WIS_30, 20);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_WIS_40, 21);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_WIS_50, 22);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_WIS_60, 23);

        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_MACE_20, 24);
        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_MACE_30, 25);
        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_MACE_40, 26);
//
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_INT_10, 27);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_INT_20, 28);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_INT_30, 29);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_INT_40, 30);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_INT_50, 31);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_INT_60, 32);
//
        createMenuItem(forgeMenu, player, CraftedResource.SHARPSTONE_10, 33);
        createMenuItem(forgeMenu, player, CraftedResource.SHARPSTONE_20, 34);
        createMenuItem(forgeMenu, player, CraftedResource.SHARPSTONE_30, 35);
//
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_STR_10, 36);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_STR_20, 37);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_STR_30, 38);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_STR_40, 39);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_STR_50, 40);
//        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_STR_60, 41);
//
//        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_SWORD_20, 42);
//        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_SWORD_30, 43);
//        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_SWORD_40, 44);

        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_VIT_10, 45);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_VIT_20, 46);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_VIT_30, 47);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_VIT_40, 48);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_VIT_50, 49);
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_VIT_60, 50);
//
//        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_AXE_20, 51);
//        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_AXE_30, 52);
//        createMenuItem(forgeMenu, player, CraftedResource.WEAPON_AXE_40, 53);
    }

    private CraftedResource determineItem(int slot) {
        switch (slot) {
            case 9:
                return CraftedResource.OFFHAND_DEX_10;
            case 33:
                return CraftedResource.SHARPSTONE_10;
            case 34:
                return CraftedResource.SHARPSTONE_20;
            case 35:
                return CraftedResource.SHARPSTONE_30;
        }
        return CraftedResource.OFFHAND_DEX_10;
    }
}
