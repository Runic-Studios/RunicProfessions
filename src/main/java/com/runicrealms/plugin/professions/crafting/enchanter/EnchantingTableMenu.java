//package com.runicrealms.plugin.professions.crafting.enchanter;
//
//import com.runicrealms.plugin.RunicCore;
//import com.runicrealms.plugin.RunicProfessions;
//import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
//import com.runicrealms.plugin.professions.Profession;
//import com.runicrealms.plugin.professions.Workstation;
//import com.runicrealms.plugin.professions.config.WorkstationLoader;
//import com.runicrealms.plugin.professions.crafting.CraftedResource;
//import com.runicrealms.plugin.utilities.GUIUtil;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.Particle;
//import org.bukkit.Sound;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.Damageable;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import java.util.Objects;
//
//public class EnchantingTableMenu extends Workstation {
//
//    private static final int ENCHANTER_MENU_SIZE = 54;
//
//    public EnchantingTableMenu(Player player) {
//        super(WorkstationLoader.getMaxPages().get(Profession.ENCHANTER));
//        setupWorkstation(player);
//    }
//
//    @Override
//    public void setupWorkstation(Player player) {
//        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(), () -> {
//            this.setItemGUI(tableMenu(player));
//            this.setTitle(tableMenu(player).getName());
//            Bukkit.getScheduler().runTask(RunicProfessions.getInstance(), () -> this.getItemGUI().open(player));
//        });
//    }
//
//    private ItemGUI tableMenu(Player player) {
//
//        ItemGUI tableMenu = super.craftingMenu(player, ENCHANTER_MENU_SIZE);
//
//        for (int i = 0; i < 9; i++) {
//            tableMenu.setOption(i, GUIUtil.BORDER_ITEM);
//        }
//
//        tableMenu.setOption(0, GUIUtil.CLOSE_BUTTON);
//
//        tableMenu.setOption(4, new ItemStack(Material.ENCHANTING_TABLE), "&eEnchanting Table",
//                "&6&lClick &7an item to start crafting!", 0, false);
//
//        super.setupItems(player, tableMenu, Profession.ENCHANTER);
//
//        tableMenu.setHandler(event -> {
//            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
//            event.setWillClose(false);
//            event.setWillDestroy(false);
//
//            if (event.getSlot() == 0) {
//
//                // close editor
//                event.setWillClose(true);
//                event.setWillDestroy(true);
//
//            } else if (event.getSlot() > 8) { // first row for ui
//
//                int multiplier = 1;
//                if (event.isRightClick()) multiplier = 5;
//                ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
//                if (meta == null) return;
//                int slot = event.getSlot();
//                CraftedResource craftedResource = super.determineCraftedResource(Profession.ENCHANTER, slot);
//                event.setWillClose(true);
//                event.setWillDestroy(true);
//                startCrafting
//                        (
//                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.SPELL_WITCH,
//                                Sound.BLOCK_ENCHANTMENT_TABLE_USE, Sound.ENTITY_PLAYER_LEVELUP, multiplier, false
//                        );
//            }
//        });
//
//        return tableMenu;
//    }
//}
