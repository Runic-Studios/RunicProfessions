package com.runicrealms.plugin.professions.crafting.blacksmith;

import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class FurnaceMenu extends Workstation {

    private static final int FURNACE_MENU_SIZE = 54;

    public FurnaceMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player player) {

        // set up the menu
        super.setupWorkstation("&f&l" + player.getName() + "'s &e&lFurnace");
        ItemGUI furnaceMenu = getItemGUI();

        //set the visual items
        furnaceMenu.setOption(3, new ItemStack(Material.IRON_INGOT),
                "&fSmelt Ores", "&7Smelt raw ores into crafting materials!", 0, false);

        // set the handler
        furnaceMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the forging menu
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(smeltingMenu(player));
                this.setTitle(smeltingMenu(player).getName());
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
        this.setItemGUI(furnaceMenu);
    }

    private ItemGUI smeltingMenu(Player player) {

        ItemGUI forgeMenu = super.craftingMenu(player, FURNACE_MENU_SIZE);
        forgeMenu.setOption(4, new ItemStack(Material.FURNACE), "&eFurnace",
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
                                player, craftedResource, 0, Particle.FLAME,
                                Sound.ITEM_BUCKET_FILL_LAVA, Sound.BLOCK_LAVA_EXTINGUISH, mult, false
                        );
            }
        });

        return forgeMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player player) {
        createMenuItem(forgeMenu, player, CraftedResource.CHAIN_LINK, 9);
        createMenuItem(forgeMenu, player, CraftedResource.IRON_BAR, 10);
        createMenuItem(forgeMenu, player, CraftedResource.GOLD_BAR, 11);
    }

    private CraftedResource determineItem(int slot) {
        switch (slot) {
            case 9:
                return CraftedResource.CHAIN_LINK;
            case 10:
                return CraftedResource.IRON_BAR;
            case 11:
                return CraftedResource.GOLD_BAR;
        }
        return CraftedResource.CHAIN_LINK;
    }
}
