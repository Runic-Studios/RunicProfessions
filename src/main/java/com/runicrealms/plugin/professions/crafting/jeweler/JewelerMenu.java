package com.runicrealms.plugin.professions.crafting.jeweler;

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

public class JewelerMenu extends Workstation {

    private static final int JEWELER_MENU_SIZE = 54;

    public JewelerMenu(Player player) {
        super(WorkstationLoader.getMaxPages().get(WorkstationType.GEMCUTTING_BENCH));
        setupWorkstation(player);
    }

    private ItemGUI benchMenu(Player player) {

        ItemGUI benchMenu = super.craftingMenu(player, JEWELER_MENU_SIZE);

        for (int i = 0; i < 9; i++) {
            benchMenu.setOption(i, GUIUtil.BORDER_ITEM);
        }

        benchMenu.setOption(0, GUIUtil.CLOSE_BUTTON);

        benchMenu.setOption(4, new ItemStack(Material.STONECUTTER), "&eGemcutting Bench",
                "&6&lClick &7an item to start crafting!", 0, false);

        super.setupItems(player, benchMenu, WorkstationType.GEMCUTTING_BENCH);

        benchMenu.setHandler(event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
            event.setWillClose(false);
            event.setWillDestroy(false);

            if (event.getSlot() == 0) {

                // close editor
                event.setWillClose(true);
                event.setWillDestroy(true);

            } else if (event.getSlot() > 8) { // first row for ui

                int mult = 1;
                if (event.isRightClick()) mult = 5;
                ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                if (meta == null) return;
                int slot = event.getSlot();
                CraftedResource craftedResource = super.determineCraftedResource(WorkstationType.GEMCUTTING_BENCH, slot);
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

    @Override
    public void setupWorkstation(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(), () -> {
            this.setItemGUI(benchMenu(player));
            this.setTitle(benchMenu(player).getName());
            Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> this.getItemGUI().open(player));
        });
    }
}
