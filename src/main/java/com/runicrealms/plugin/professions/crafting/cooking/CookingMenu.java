package com.runicrealms.plugin.professions.crafting.cooking;

import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.plugin.professions.utilities.ProfUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class CookingMenu extends Workstation {

    private static final int COOKING_MENU_SIZE = 54;
    private static final int STEW_DURATION = ProfUtil.getRunicItemDataFieldInt(CraftedResource.AMBROSIA_STEW.getRunicItem(), "duration");
    private static final int AMBROSIA_STEW_AMT = ProfUtil.getRunicItemDataFieldInt(CraftedResource.AMBROSIA_STEW.getRunicItem(), "health-amount");

    public CookingMenu(Player pl) {
        setupWorkstation(pl);
    }

    public static int getAmbrosiaStewAmt() {
        return AMBROSIA_STEW_AMT;
    }

    public static int getStewDuration() {
        return STEW_DURATION;
    }

    @Override
    public void setupWorkstation(Player player) {

        // name the menu
        super.setupWorkstation("&f&l" + player.getName() + "'s &e&lCooking Fire");
        ItemGUI cookingMenu = getItemGUI();
        cookingMenu.setName(this.getTitle());

        //set the visual items
        cookingMenu.setOption(3, new ItemStack(Material.BREAD),
                "&fCook Food", "&7Create food for your journey!", 0, false);

        // set the handler
        cookingMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the cooking menu
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(cookingMenu(player));
                this.setTitle(cookingMenu(player).getName());
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
        this.setItemGUI(cookingMenu);
    }

    private ItemGUI cookingMenu(Player player) {

        ItemGUI cookingMenu = super.craftingMenu(player, COOKING_MENU_SIZE);

        cookingMenu.setOption(4, new ItemStack(Material.FLINT_AND_STEEL), "&eCooking Fire",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(cookingMenu, player);

        cookingMenu.setHandler(event -> {

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
                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.SMOKE_NORMAL,
                                Sound.ENTITY_GHAST_SHOOT, Sound.BLOCK_LAVA_EXTINGUISH, mult, true
                        );
            }
        });

        return cookingMenu;
    }

    private void setupItems(ItemGUI cookingFireMenu, Player player) {
        createMenuItem(cookingFireMenu, player, CraftedResource.BREAD, 9);
        createMenuItem(cookingFireMenu, player, CraftedResource.COOKED_MEAT, 10);
        createMenuItem(cookingFireMenu, player, CraftedResource.COOKED_COD, 11);
        createMenuItem(cookingFireMenu, player, CraftedResource.COOKED_SALMON, 12);
        createMenuItem(cookingFireMenu, player, CraftedResource.AMBROSIA_STEW, 13);
    }

    private CraftedResource determineItem(int slot) {
        switch (slot) {
            case 9:
                return CraftedResource.BREAD;
            case 10:
                return CraftedResource.COOKED_MEAT;
            case 11:
                return CraftedResource.COOKED_COD;
            case 12:
                return CraftedResource.COOKED_SALMON;
            case 13:
                return CraftedResource.AMBROSIA_STEW;
        }
        return CraftedResource.BREAD;
    }
}
