package com.runicrealms.plugin.professions.crafting.cooking;

import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class CookingMenu extends Workstation {

    private static final int STEW_DURATION = Integer.parseInt(CookingItems.AMBROSIA_STEW.getData().get("duration"));
    private static final int AMBROSIA_STEW_AMT = Integer.parseInt(CookingItems.AMBROSIA_STEW.getData().get("health-amount"));

    public CookingMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player pl) {

        // name the menu
        super.setupWorkstation("&f&l" + pl.getName() + "'s &e&lCooking Fire");
        ItemGUI cookingMenu = getItemGUI();
        cookingMenu.setName(this.getTitle());

        //set the visual items
        cookingMenu.setOption(3, new ItemStack(Material.BREAD),
                "&fCook Food", "&7Create food for your journey!", 0, false);

        // set the handler
        cookingMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the cooking menu
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(cookingMenu(pl));
                this.setTitle(cookingMenu(pl).getName());
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
        this.setItemGUI(cookingMenu);
    }

    private ItemGUI cookingMenu(Player pl) {

        // bread
        LinkedHashMap<Material, Integer> breadReqs = new LinkedHashMap<>();
        breadReqs.put(Material.WHEAT, 3);
        breadReqs.put(Material.SPRUCE_LOG, 1);

        // cod
        LinkedHashMap<Material, Integer> codReqs = new LinkedHashMap<>();
        codReqs.put(Material.COD, 1);
        codReqs.put(Material.OAK_LOG, 1);

        // salmon
        LinkedHashMap<Material, Integer> salmonReqs = new LinkedHashMap<>();
        salmonReqs.put(Material.SALMON, 1);
        salmonReqs.put(Material.OAK_LOG, 1);

        // ambrosia stew
        LinkedHashMap<Material, Integer> ambrosiaStewReqs = new LinkedHashMap<>();
        ambrosiaStewReqs.put(Material.GOLDEN_CARROT, 1);
        ambrosiaStewReqs.put(Material.RABBIT, 1);
        ambrosiaStewReqs.put(Material.DARK_OAK_LOG, 1);

        ItemGUI cookingMenu = super.craftingMenu(pl, 18);

        cookingMenu.setOption(4, new ItemStack(Material.FLINT_AND_STEEL), "&eCooking Fire",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(cookingMenu, pl);

        cookingMenu.setHandler(event -> {

            if (event.getSlot() == 4) {

                // return to the first menu
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                setupWorkstation(pl);
                this.getItemGUI().open(pl);
                event.setWillClose(false);
                event.setWillDestroy(true);

            } else {

                LinkedHashMap<Material, Integer> reqs = new LinkedHashMap<>();

                if (event.getSlot() == 9) {
                    reqs = breadReqs;
                } else if (event.getSlot() == 10) {
                    reqs = codReqs;
                } else if (event.getSlot() == 11) {
                    reqs = salmonReqs;
                } else if (event.getSlot() == 12) {
                    reqs = ambrosiaStewReqs;
                }

                int mult = 1;
                if (event.isRightClick()) mult = 5;
                ItemMeta meta = Objects.requireNonNull(event.getCurrentItem()).getItemMeta();
                if (meta == null) return;

                // destroy instance of inventory to prevent bugs
                event.setWillClose(true);
                event.setWillDestroy(true);

                // craft item based on experience and reagent amount
                super.startCrafting(pl, reqs, 0, 0, event.getCurrentItem().getType(),
                        meta.getDisplayName(), 0, 0,
                        ((Damageable) meta).getDamage(), Particle.SMOKE_NORMAL,
                        Sound.ENTITY_GHAST_SHOOT, Sound.BLOCK_LAVA_EXTINGUISH, event.getSlot(), mult);
            }});

        return cookingMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player pl) {

        // bread
        LinkedHashMap<Material, Integer> breadReqs = new LinkedHashMap<>();
        breadReqs.put(Material.WHEAT, 3);
        breadReqs.put(Material.SPRUCE_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 9, Material.BREAD, "&fBread", breadReqs,
                "Wheat\nSpruce Log", 999, 0, 0, 0, "",
                true, false, false);

        // cod
        LinkedHashMap<Material, Integer> codReqs = new LinkedHashMap<>();
        codReqs.put(Material.COD, 1);
        codReqs.put(Material.OAK_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 10, Material.COOKED_COD, "&fCooked Cod", codReqs,
                "Cod\nOak Log", 999, 0, 0, 0, "",
                true, false, false);

        // salmon
        LinkedHashMap<Material, Integer> salmonReqs = new LinkedHashMap<>();
        salmonReqs.put(Material.SALMON, 1);
        salmonReqs.put(Material.OAK_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 11, Material.COOKED_SALMON, "&fCooked Salmon", salmonReqs,
                "Salmon\nOak Log", 999, 0, 0, 0, "",
                true, false, false);

        // ambrosia stew
        LinkedHashMap<Material, Integer> ambrosiaStewReqs = new LinkedHashMap<>();
        ambrosiaStewReqs.put(Material.GOLDEN_CARROT, 1);
        ambrosiaStewReqs.put(Material.RABBIT, 1);
        ambrosiaStewReqs.put(Material.DARK_OAK_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 12, Material.RABBIT_STEW, "&fAmbrosia Stew", ambrosiaStewReqs,
                "Ambrosia Root\nUncooked Rabbit\nDark Oak Log", 999, 0, 0, 2,
                generateItemLore(CookingItems.AMBROSIA_STEW),
                true, false, true);
    }

    @Override
    public void produceResult(Player pl, Material material, String dispName,
                              int currentLvl, int amt, int rate, int durability, int someVar) {
        for (int i = 0; i < amt; i++) {
            ItemStack craftedItem = determineItem(someVar);
            // this ALLOWS cooking items to stack.
            HashMap<Integer, ItemStack> itemsToAdd = pl.getInventory().addItem(craftedItem);
            // drop leftover items on the floor
            for (ItemStack leftOver : itemsToAdd.values()) {
                pl.getWorld().dropItem(pl.getLocation(), leftOver);
            }
        }
    }

    private ItemStack determineItem(int slot) {
        switch (slot) {
            case 9:
                return CookingItems.BREAD_ITEMSTACK;
            case 10:
                return CookingItems.COOKED_COD_ITEMSTACK;
            case 11:
                return CookingItems.COOKED_SALMON_ITEMSTACK;
            case 12:
                return CookingItems.AMBROSIA_STEW_ITEMSTACK;
        }
        return new ItemStack(Material.STONE); // oops
    }

    public static int getAmbrosiaStewAmt() {
        return AMBROSIA_STEW_AMT;
    }

    public static int getStewDuration() {
        return STEW_DURATION;
    }
}
