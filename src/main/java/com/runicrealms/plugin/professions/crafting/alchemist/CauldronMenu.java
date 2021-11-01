package com.runicrealms.plugin.professions.crafting.alchemist;

import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class CauldronMenu extends Workstation {

    private static final int CAULDRON_MENU_SIZE = 54;

    public CauldronMenu(Player pl) {
        setupWorkstation(pl);
    }

    public void setupWorkstation(Player player) {
        setupWorkstation("&f&l" + player.getName() + "'s &e&lCauldron");
        ItemGUI baseMenu = getItemGUI();
        baseMenu.setOption
                (
                        3,
                        potionItem(Color.RED, "", ""),
                        "&fBrew Potions",
                        "&7Brew powerful and unique potions!",
                        0,
                        false
                );
        baseMenu.setHandler(event -> {
            if (event.getSlot() == 3) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
                setItemGUI(cauldronMenu(player));
                setTitle(cauldronMenu(player).getName());
                getItemGUI().open(player);
                event.setWillClose(false);
                event.setWillDestroy(true);
            } else if (event.getSlot() == 5) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        });
        setItemGUI(baseMenu);
    }

    private ItemGUI cauldronMenu(Player player) {
        ItemGUI cauldronMenu = craftingMenu(player, CAULDRON_MENU_SIZE);
        cauldronMenu.setOption
                (

                        4,
                        new ItemStack(Material.CAULDRON),
                        "&eCauldron",
                        "&fClick &7an item to start crafting!\n&fClick &7here to return to the station",
                        0,
                        false
                );
        setupItems(cauldronMenu, player);
        cauldronMenu.setHandler(event -> {
            if (event.getSlot() == 4) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
                setupWorkstation(player);
                getItemGUI().open(player);
                event.setWillClose(false);
                event.setWillDestroy(true);
            } else {
                int mult = 1;
                if (event.isRightClick())
                    mult = 5;
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta == null) return;
                int slot = event.getSlot();
                CraftedResource craftedResource = determineItem(slot);
                event.setWillClose(true);
                event.setWillDestroy(true);
                startCrafting
                        (
                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.WATER_SPLASH,
                                Sound.BLOCK_BREWING_STAND_BREW, Sound.ENTITY_GENERIC_DRINK, slot, mult, false
                        );
            }
        });
        return cauldronMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player player) {
        createMenuItem(forgeMenu, player, CraftedResource.BOTTLE, 9);
        createMenuItem(forgeMenu, player, CraftedResource.LESSER_POTION_HEALING, 10);
        createMenuItem(forgeMenu, player, CraftedResource.LESSER_POTION_MANA, 11);
        createMenuItem(forgeMenu, player, CraftedResource.LESSER_POTION_SLAYING, 12);
        createMenuItem(forgeMenu, player, CraftedResource.MINOR_POTION_HEALING, 13);
        createMenuItem(forgeMenu, player, CraftedResource.MINOR_POTION_MANA, 14);
        createMenuItem(forgeMenu, player, CraftedResource.MINOR_POTION_SLAYING, 15);
        createMenuItem(forgeMenu, player, CraftedResource.MINOR_POTION_LOOTING, 16);
        createMenuItem(forgeMenu, player, CraftedResource.MAJOR_POTION_HEALING, 17);
        createMenuItem(forgeMenu, player, CraftedResource.MAJOR_POTION_MANA, 18);
        createMenuItem(forgeMenu, player, CraftedResource.MAJOR_POTION_LOOTING, 19);
        createMenuItem(forgeMenu, player, CraftedResource.MAJOR_POTION_SLAYING, 20);
        createMenuItem(forgeMenu, player, CraftedResource.GREATER_POTION_HEALING, 21);
        createMenuItem(forgeMenu, player, CraftedResource.GREATER_POTION_MANA, 22);
        createMenuItem(forgeMenu, player, CraftedResource.GREATER_POTION_LOOTING, 23);
        createMenuItem(forgeMenu, player, CraftedResource.GREATER_POTION_SLAYING, 24);
        createMenuItem(forgeMenu, player, CraftedResource.POTION_SACRED_FIRE, 25);
    }

    @Override
    public void produceResult(Player player, int numberOfItems, int inventorySlot) {
        ItemStack itemStack = determineItem(inventorySlot).getItemStack();
        produceResult(player, numberOfItems, itemStack);
    }

    private CraftedResource determineItem(int slot) {
        switch (slot) {
            case 9:
                return CraftedResource.BOTTLE;
            case 10:
                return CraftedResource.LESSER_POTION_HEALING;
            case 11:
                return CraftedResource.LESSER_POTION_MANA;
            case 12:
                return CraftedResource.LESSER_POTION_SLAYING;
            case 13:
                return CraftedResource.MINOR_POTION_HEALING;
            case 14:
                return CraftedResource.MINOR_POTION_MANA;
            case 15:
                return CraftedResource.MINOR_POTION_SLAYING;
            case 16:
                return CraftedResource.MINOR_POTION_LOOTING;
            case 17:
                return CraftedResource.MAJOR_POTION_HEALING;
            case 18:
                return CraftedResource.MAJOR_POTION_MANA;
            case 19:
                return CraftedResource.MAJOR_POTION_SLAYING;
            case 20:
                return CraftedResource.MAJOR_POTION_LOOTING;
            case 21:
                return CraftedResource.GREATER_POTION_HEALING;
            case 22:
                return CraftedResource.GREATER_POTION_MANA;
            case 23:
                return CraftedResource.GREATER_POTION_SLAYING;
            case 24:
                return CraftedResource.GREATER_POTION_LOOTING;
            case 25:
                return CraftedResource.POTION_SACRED_FIRE;
        }
        return CraftedResource.BOTTLE;
    }
}
