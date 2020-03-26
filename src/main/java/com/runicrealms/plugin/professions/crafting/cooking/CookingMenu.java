package com.runicrealms.plugin.professions.crafting.cooking;

import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@SuppressWarnings("FieldCanBeLocal")
public class CookingMenu extends Workstation {

    private static final int RABBIT_STEW_AMT = 250;
    private static final int STEW_DURATION = 10;
    private static final int AMBROSIA_STEW_AMT = 250;

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

        // rabbit stew
        LinkedHashMap<Material, Integer> rabbitStewReqs = new LinkedHashMap<>();
        rabbitStewReqs.put(Material.RABBIT, 1);
        rabbitStewReqs.put(Material.DARK_OAK_LOG, 1);

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
                    reqs = rabbitStewReqs;
                } else if (event.getSlot() == 13) {
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
                        Sound.ENTITY_GHAST_SHOOT, Sound.BLOCK_LAVA_EXTINGUISH, 0, mult);
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

        // rabbit stew
        LinkedHashMap<Material, Integer> rabbitSteqReqs = new LinkedHashMap<>();
        rabbitSteqReqs.put(Material.RABBIT, 1);
        rabbitSteqReqs.put(Material.DARK_OAK_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 12, Material.RABBIT_STEW, "&fRabbit Stew", rabbitSteqReqs,
                "Uncooked Rabbit\nDark Oak Log", 999, 0, 0, 1,
                "&eRestores &c" + RABBIT_STEW_AMT + "❤ &eover " + STEW_DURATION + " seconds" +
                        "\n&7(Must be out of combat)\n",
                true, false, false);

        // ambrosia stew
        LinkedHashMap<Material, Integer> ambrosiaStewReqs = new LinkedHashMap<>();
        ambrosiaStewReqs.put(Material.GOLDEN_CARROT, 1);
        ambrosiaStewReqs.put(Material.RABBIT, 1);
        ambrosiaStewReqs.put(Material.DARK_OAK_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 13, Material.RABBIT_STEW, "&fAmbrosia Stew", ambrosiaStewReqs,
                "Ambrosia Root\nUncooked Rabbit\nDark Oak Log", 999, 0, 0, 2,
                "&eRestores &c" + AMBROSIA_STEW_AMT + "❤ &eover " + STEW_DURATION + " seconds\n",
                true, false, true);
    }

    @Override
    public void produceResult(Player pl, Material material, String dispName,
                              int currentLvl, int amt, int rate, int durability, int someVar) {

        for (int i = 0; i < amt; i++) {

            ItemStack craftedItem = new ItemStack(material);

            if (durability == 1) {

                craftedItem = rabbitStew();

            } else if (durability == 2) {

                craftedItem = ambrosiaStew();

            // default food
            } else {

                ItemMeta meta = craftedItem.getItemMeta();
                ((Damageable) Objects.requireNonNull(meta)).setDamage(durability);

                ArrayList<String> lore = new ArrayList<>();

                lore.add(ChatColor.GRAY + "Consumable");
                meta.setLore(lore);
                meta.setDisplayName(ChatColor.WHITE + dispName);
                craftedItem.setItemMeta(meta);
            }

            // add items to inventory, drop items that couldn't be added
            HashMap<Integer, ItemStack> leftOvers = pl.getInventory().addItem(craftedItem);
            for (ItemStack is : leftOvers.values()) {
                pl.getWorld().dropItem(pl.getLocation(), is);
            }
        }
    }

    public static ItemStack rabbitStew() {
        ItemStack rabbitStew = new ItemStack(Material.RABBIT_STEW);
        ItemMeta rabbitStewMeta = rabbitStew.getItemMeta();
        Objects.requireNonNull(rabbitStewMeta).setDisplayName(ChatColor.WHITE + "Rabbit Stew");
        rabbitStewMeta.setLore(Arrays.asList(
                "",
                ChatColor.YELLOW + "Restores " + ChatColor.RED + RABBIT_STEW_AMT + "❤" + ChatColor.YELLOW + " over " + STEW_DURATION + " seconds",
                ChatColor.GRAY + "(Must be out of combat)",
                "",
                ChatColor.GRAY + "Consumable"));
        ((Damageable) rabbitStewMeta).setDamage(2);
        rabbitStewMeta.setUnbreakable(true);
        rabbitStewMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        rabbitStew.setItemMeta(rabbitStewMeta);
        return rabbitStew;
    }

    public static ItemStack ambrosiaStew() {
        ItemStack ambrosiaStew = new ItemStack(Material.RABBIT_STEW);
        ItemMeta ambrosiaStewMeta = ambrosiaStew.getItemMeta();
        Objects.requireNonNull(ambrosiaStewMeta).setDisplayName(ChatColor.WHITE + "Ambrosia Stew");
        ambrosiaStewMeta.setLore(Arrays.asList(
                "",
                ChatColor.YELLOW + "Restores " + ChatColor.RED + AMBROSIA_STEW_AMT + "❤" + ChatColor.YELLOW + " over " + STEW_DURATION + " seconds",
                "",
                ChatColor.GRAY + "Consumable"));
        ambrosiaStewMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        ambrosiaStewMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ((Damageable) ambrosiaStewMeta).setDamage(2);
        ambrosiaStewMeta.setUnbreakable(true);
        ambrosiaStewMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        ambrosiaStew.setItemMeta(ambrosiaStewMeta);
        return ambrosiaStew;
    }

    public static int getRabbitStewAmt() {
        return RABBIT_STEW_AMT;
    }

    public static int getAmbrosiaStewAmt() {
        return AMBROSIA_STEW_AMT;
    }

    public static int getStewDuration() {
        return STEW_DURATION;
    }
}
