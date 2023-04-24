package com.runicrealms.plugin.professions.crafting.cooking;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.WorkstationType;
import com.runicrealms.plugin.professions.config.WorkstationLoader;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.plugin.professions.crafting.ListenerResource;
import com.runicrealms.plugin.professions.utilities.ProfUtil;
import com.runicrealms.plugin.utilities.GUIUtil;
import org.bukkit.Bukkit;
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
    private static final int STEW_DURATION = ProfUtil.getRunicItemDataFieldInt(ListenerResource.AMBROSIA_STEW.getRunicItem(), "duration");
    private static final int AMBROSIA_STEW_AMT = ProfUtil.getRunicItemDataFieldInt(ListenerResource.AMBROSIA_STEW.getRunicItem(), "health-amount");

    public CookingMenu(Player player) {
        super(WorkstationLoader.getMaxPages().get(WorkstationType.COOKING_FIRE));
        setupWorkstation(player);
    }

    public static int getAmbrosiaStewAmt() {
        return AMBROSIA_STEW_AMT;
    }

    public static int getStewDuration() {
        return STEW_DURATION;
    }

    private ItemGUI cookingMenu(Player player) {

        ItemGUI cookingMenu = super.craftingMenu(player, COOKING_MENU_SIZE);

        for (int i = 0; i < 9; i++) {
            cookingMenu.setOption(i, GUIUtil.BORDER_ITEM);
        }

        cookingMenu.setOption(0, GUIUtil.CLOSE_BUTTON);

        cookingMenu.setOption(4, new ItemStack(Material.FLINT_AND_STEEL), "&eCooking Fire",
                "&6&lClick &7an item to start crafting!", 0, false);

        super.setupItems(player, cookingMenu, WorkstationType.COOKING_FIRE);

        cookingMenu.setHandler(event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
            event.setWillClose(false);
            event.setWillDestroy(false);

            if (event.getSlot() == 0) {

                // close editor
                event.setWillClose(true);
                event.setWillDestroy(true);

            } else if (event.getSlot() > 8) { // first row for ui

                int multiplier = 1;
                if (event.isRightClick()) multiplier = 5;
                ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                if (meta == null) return;
                CraftedResource craftedResource = super.determineCraftedResource(WorkstationType.COOKING_FIRE, event.getSlot());
                event.setWillClose(true);
                event.setWillDestroy(true);
                startCrafting
                        (
                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.SMOKE_NORMAL,
                                Sound.ENTITY_GHAST_SHOOT, Sound.BLOCK_LAVA_EXTINGUISH, multiplier, true
                        );
            }
        });

        return cookingMenu;
    }

    @Override
    public void setupWorkstation(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(), () -> {
            this.setItemGUI(cookingMenu(player));
            this.setTitle(cookingMenu(player).getName());
            Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> this.getItemGUI().open(player));
        });
    }
}
