package com.runicrealms.plugin.professions.crafting.cooking;

import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class CookingMenu extends Workstation {

    private static final int STEW_DURATION = Integer.parseInt(CookingItems.AMBROSIA_STEW.getData().get("duration"));
    private static final int AMBROSIA_STEW_AMT = Integer.parseInt(CookingItems.AMBROSIA_STEW.getData().get("health-amount"));

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

    private ItemGUI cookingMenu(Player pl) {

        // meat
        LinkedHashMap<Material, Integer> meatReqs = new LinkedHashMap<>();
        meatReqs.put(Material.MUTTON, 4);
        meatReqs.put(Material.OAK_LOG, 2);

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
        ambrosiaStewReqs.put(Material.RABBIT, 4);
        ambrosiaStewReqs.put(Material.DARK_OAK_LOG, 16);

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

                int exp = 0;
                LinkedHashMap<Material, Integer> reqs = new LinkedHashMap<>();

                if (event.getSlot() == 9) {
                    exp = 10;
                    reqs = meatReqs;
                } else if (event.getSlot() == 10) {
                    exp = 10;
                    reqs = breadReqs;
                } else if (event.getSlot() == 11) {
                    exp = 20;
                    reqs = codReqs;
                } else if (event.getSlot() == 12) {
                    exp = 20;
                    reqs = salmonReqs;
                } else if (event.getSlot() == 13) {
                    exp = 100;
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
                super.startCrafting
                        (
                                pl, reqs, 0, 0, event.getCurrentItem().getType(), 0, exp,
                                ((Damageable) meta).getDamage(), Particle.SMOKE_NORMAL,
                                Sound.ENTITY_GHAST_SHOOT, Sound.BLOCK_LAVA_EXTINGUISH, event.getSlot(), mult, true
                        );
            }
        });

        return cookingMenu;
    }

    private void setupItems(ItemGUI cookingFireMenu, Player player) {

        // meat
        LinkedHashMap<Material, Integer> meatReqs = new LinkedHashMap<>();
        meatReqs.put(Material.MUTTON, 4);
        meatReqs.put(Material.OAK_LOG, 2);
        super.createMenuItem(cookingFireMenu, player, 9, Material.COOKED_MUTTON, "&fCooked Meat", meatReqs,
                "Raw Meat\nOak Log", 999, 10, 0, 0, "",
                true, false, false);

        // bread
        LinkedHashMap<Material, Integer> breadReqs = new LinkedHashMap<>();
        breadReqs.put(Material.WHEAT, 3);
        breadReqs.put(Material.SPRUCE_LOG, 1);
        super.createMenuItem(cookingFireMenu, player, 10, Material.BREAD, "&fBread", breadReqs,
                "Wheat\nSpruce Log", 999, 10, 0, 0, "",
                true, false, false);

        // cod
        LinkedHashMap<Material, Integer> codReqs = new LinkedHashMap<>();
        codReqs.put(Material.COD, 1);
        codReqs.put(Material.OAK_LOG, 1);
        super.createMenuItem(cookingFireMenu, player, 11, Material.COOKED_COD, "&fCooked Cod", codReqs,
                "Cod\nOak Log", 999, 20, 0, 0, "",
                true, false, false);

        // salmon
        LinkedHashMap<Material, Integer> salmonReqs = new LinkedHashMap<>();
        salmonReqs.put(Material.SALMON, 1);
        salmonReqs.put(Material.OAK_LOG, 1);
        super.createMenuItem(cookingFireMenu, player, 12, Material.COOKED_SALMON, "&fCooked Salmon", salmonReqs,
                "Salmon\nOak Log", 999, 20, 0, 0, "",
                true, false, false);

        // ambrosia stew
        LinkedHashMap<Material, Integer> ambrosiaStewReqs = new LinkedHashMap<>();
        ambrosiaStewReqs.put(Material.GOLDEN_CARROT, 1);
        ambrosiaStewReqs.put(Material.RABBIT, 4);
        ambrosiaStewReqs.put(Material.DARK_OAK_LOG, 16);
        super.createMenuItem(cookingFireMenu, player, 13, Material.RABBIT_STEW, "&fAmbrosia Stew", ambrosiaStewReqs,
                "Ambrosia Root\nUncooked Rabbit\nDark Oak Log", 999, 100, 0, 2,
                generateItemLore(CookingItems.AMBROSIA_STEW),
                true, false, true);
    }

    @Override
    public void produceResult(Player player, int numberOfItems, int inventorySlot) {
        ItemStack itemStack = determineItem(inventorySlot);
        produceResult(player, numberOfItems, itemStack);
    }

    private ItemStack determineItem(int slot) {
        switch (slot) {
            case 9:
                return CookingItems.COOKED_MEAT_ITEMSTACK;
            case 10:
                return CookingItems.BREAD_ITEMSTACK;
            case 11:
                return CookingItems.COOKED_COD_ITEMSTACK;
            case 12:
                return CookingItems.COOKED_SALMON_ITEMSTACK;
            case 13:
                return CookingItems.AMBROSIA_STEW_ITEMSTACK;
        }
        return new ItemStack(Material.STONE); // oops
    }
}
