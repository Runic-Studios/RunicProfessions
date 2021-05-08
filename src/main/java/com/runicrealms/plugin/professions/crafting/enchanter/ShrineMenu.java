package com.runicrealms.plugin.professions.crafting.enchanter;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ShrineMenu extends Workstation {

    public ShrineMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player pl) {

        // setup the menu
        super.setupWorkstation("&f&l" + pl.getName() + "'s &e&lShrine");
        ItemGUI shrineMenu = getItemGUI();

        //set the visual items
        shrineMenu.setOption(3, new ItemStack(Material.BOOK),
                "&fDungeon Buffs", "&7Give your party an edge in a dungeon!", 0, false);

        // set the handler
        shrineMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the bench menu
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(shrineMenu(pl));
                this.setTitle(shrineMenu(pl).getName());
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
        this.setItemGUI(shrineMenu);
    }

    private ItemGUI shrineMenu(Player pl) {

        // grab the player's current profession level, progress toward that level
        int currentLvl = RunicCoreAPI.getPlayerCache(pl).getProfLevel();

        // paper
        LinkedHashMap<Material, Integer> placeholderReqs = new LinkedHashMap<>();
        placeholderReqs.put(Material.DIRT, 1);

        ItemGUI benchMenu = super.craftingMenu(pl, 27);

        benchMenu.setOption(4, new ItemStack(Material.LECTERN), "&eShrine",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(benchMenu, pl);

        benchMenu.setHandler(event -> {

            if (event.getSlot() == 4) {

                // return to the first menu
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                setupWorkstation(pl);
                this.getItemGUI().open(pl);
                event.setWillClose(false);
                event.setWillDestroy(true);

            } else {

                int mult = 1;
                if (event.isRightClick()) mult = 5;
                ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                if (meta == null) return;

                int slot = event.getSlot();
                int reqLevel = 0;
                int exp = 0;
                LinkedHashMap<Material, Integer> reqHashMap = new LinkedHashMap<>();

                if (slot == 9) {
                    // placeholder
                    reqHashMap = placeholderReqs;
                    exp = 1;
                }

                // destroy instance of inventory to prevent bugs
                event.setWillClose(true);
                event.setWillDestroy(true);

                // craft item based on experience and reagent amount
                super.startCrafting(pl, reqHashMap, 1, reqLevel, event.getCurrentItem().getType(),
                        meta.getDisplayName(), currentLvl, exp,
                        ((Damageable) meta).getDamage(), Particle.SPELL_WITCH,
                        Sound.BLOCK_ENCHANTMENT_TABLE_USE, Sound.ENTITY_PLAYER_LEVELUP, slot, mult);
            }
        });

        return benchMenu;
    }

    private void setupItems(ItemGUI tableMenu, Player pl) {
        // placeholder
        LinkedHashMap<Material, Integer> placeholderReqs = new LinkedHashMap<>();
        placeholderReqs.put(Material.DIRT, 999);
        super.createMenuItem(tableMenu, pl, 9, Material.DIRT, "&fPlaceholder", placeholderReqs,
                "Placeholder", 1, 1, 0, 0, "",
                true, false, false);
    }

    @Override
    public void produceResult(Player pl, Material material, String dispName,
                              int currentLvl, int amt, int rate, int durability, int someVar) {

        ItemStack craftedItem = determineItem(pl, someVar, material, dispName, durability); // someVar is the slot of the item in the menu.

        // create a new item up to the amount
        int failCount = 0;
        for (int i = 0; i < amt; i++) {

            double chance = ThreadLocalRandom.current().nextDouble(0, 100);
            if (chance <= rate) {
                // add items to inventory, drop items that couldn't be added
                HashMap<Integer, ItemStack> leftOvers = pl.getInventory().addItem(craftedItem);
                for (ItemStack is : leftOvers.values()) {
                    pl.getWorld().dropItem(pl.getLocation(), is);
                }
            } else {
                failCount = failCount + 1;
            }
        }

        // display fail message
        if (failCount == 0) return;
        pl.sendMessage(ChatColor.RED + "You fail to craft this item. [x" + failCount + "]");
    }

    private ItemStack determineItem(Player pl, int slot, Material material, String dispName, int durability) {
        ItemStack item = new ItemStack(Material.STICK);
        int percent;
        int profLv = RunicCoreAPI.getPlayerCache(pl).getProfLevel();
        if (profLv < 30) {
            percent = 1;
        } else if (profLv < 50) {
            percent = 2;
        } else {
            percent = 3;
        }

        switch (slot) {
            case 9:
                item = new ItemStack(Material.DIRT);
                break;
        }

        return item;
    }
}