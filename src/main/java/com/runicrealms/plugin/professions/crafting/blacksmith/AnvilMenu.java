package com.runicrealms.plugin.professions.crafting.blacksmith;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.WorkstationType;
import com.runicrealms.plugin.professions.config.WorkstationLoader;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
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

public class AnvilMenu extends Workstation {

    private static final int ANVIL_MENU_SIZE = 54;

    public AnvilMenu(Player player) {
        super(WorkstationLoader.getMaxPages().get(WorkstationType.ANVIL));
        this.setupWorkstation(player);
    }

    private ItemGUI forgeMenu(Player player) {

        ItemGUI forgeMenu = super.craftingMenu(player, ANVIL_MENU_SIZE);

        for (int i = 0; i < 9; i++) {
            forgeMenu.setOption(i, GUIUtil.BORDER_ITEM);
        }

        if (this.getMaxPages() > 1)
            forgeMenu.setOption(8, GUIUtil.FORWARD_BUTTON);

        if (this.getCurrentPage() == 1)
            forgeMenu.setOption(0, GUIUtil.CLOSE_BUTTON);
        else
            forgeMenu.setOption(0, GUIUtil.BACK_BUTTON);

        forgeMenu.setOption(4, new ItemStack(Material.ANVIL), "&eAnvil",
                "&6&lClick &7an item to start crafting!", 0, false);

        super.setupItems(player, forgeMenu, WorkstationType.ANVIL);

        forgeMenu.setHandler(event -> {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
            }
            event.setWillClose(false);
            event.setWillDestroy(false);

            if (event.getSlot() == 0) {

                // Close editor
                if (this.getCurrentPage() == 1) {
                    event.setWillClose(true);
                    event.setWillDestroy(true);
                } else {
                    // Return to first page
                    openFirstPage(player);
                }

            } else if (event.getSlot() == 8) {
                super.openNextPage(player);
            } else if (event.getSlot() > 8) { // first row for ui

                int multiplier = 1;
                if (event.isRightClick()) multiplier = 5;
                ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                if (meta == null) return;
                CraftedResource craftedResource = super.determineCraftedResource(WorkstationType.ANVIL, event.getSlot());
                event.setWillClose(true);
                event.setWillDestroy(true);
                startCrafting
                        (
                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.FIREWORKS_SPARK,
                                Sound.BLOCK_ANVIL_PLACE, Sound.BLOCK_ANVIL_USE, multiplier, false
                        );
            }
        });

        return forgeMenu;
    }

    @Override
    public void setupWorkstation(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(), () -> {
            this.setItemGUI(forgeMenu(player));
            this.setTitle(forgeMenu(player).getName());
            Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> this.getItemGUI().open(player));
        });
    }
}
