package com.runicrealms.plugin.professions.crafting.blacksmith;

import com.runicrealms.plugin.api.RunicCoreAPI;
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

public class FurnaceMenu extends Workstation {

    public FurnaceMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player player) {

        // setup the menu
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

    private ItemGUI smeltingMenu(Player pl) {

        // grab the player's current profession level, progress toward that level
        int currentLvl = RunicCoreAPI.getPlayerCache(pl).getProfLevel();

        // create three hashmaps for the reagents, set to 0 since we've only got 1 reagent
        LinkedHashMap<Material, Integer> chainLinkReqs = new LinkedHashMap<>();
        chainLinkReqs.put(Material.IRON_ORE, 1);
        chainLinkReqs.put(Material.SPRUCE_LOG, 1);
        LinkedHashMap<Material, Integer> goldBarReqs = new LinkedHashMap<>();
        goldBarReqs.put(Material.GOLD_ORE, 1);
        goldBarReqs.put(Material.OAK_LOG, 1);
        LinkedHashMap<Material, Integer> ironBarReqs = new LinkedHashMap<>();
        ironBarReqs.put(Material.IRON_ORE, 1);
        ironBarReqs.put(Material.OAK_LOG, 1);

        ItemGUI forgeMenu = super.craftingMenu(pl, 18);

        forgeMenu.setOption(4, new ItemStack(Material.FURNACE), "&eFurnace",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(forgeMenu, pl);

        forgeMenu.setHandler(event -> {

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

                int exp = 10;
                LinkedHashMap<Material, Integer> reqHashMap;
                if (event.getSlot() == 9) {
                    reqHashMap = chainLinkReqs;
                } else if (event.getSlot() == 10) {
                    reqHashMap = goldBarReqs;
                } else {
                    reqHashMap = ironBarReqs;
                }

                // destroy instance of inventory to prevent bugs
                event.setWillClose(true);
                event.setWillDestroy(true);

                // craft item based on experience and reagent amount
                super.startCrafting
                        (
                                pl, reqHashMap, 0, 0, event.getCurrentItem().getType(), currentLvl, exp,
                                ((Damageable) meta).getDamage(), Particle.FLAME,
                                Sound.ITEM_BUCKET_FILL_LAVA, Sound.BLOCK_LAVA_EXTINGUISH, event.getSlot(), mult
                        );
            }
        });

        return forgeMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player pl) {

        // chain link
        LinkedHashMap<Material, Integer> chainLinkReqs = new LinkedHashMap<>();
        chainLinkReqs.put(Material.IRON_ORE, 1);
        chainLinkReqs.put(Material.SPRUCE_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 9, Material.IRON_BARS, "&fChain Link", chainLinkReqs,
                "Iron Ore\nSpruce Log", 999, 10, 0, 0, "",
                true, false, false);

        // gold bar
        LinkedHashMap<Material, Integer> goldBarReqs = new LinkedHashMap<>();
        goldBarReqs.put(Material.GOLD_ORE, 1);
        goldBarReqs.put(Material.OAK_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 10, Material.GOLD_INGOT, "&fGold Bar", goldBarReqs,
                "Gold Ore\nOak Log", 999, 10, 0, 0, "",
                true, false, false);

        // iron bar
        LinkedHashMap<Material, Integer> ironBarReqs = new LinkedHashMap<>();
        ironBarReqs.put(Material.IRON_ORE, 1);
        ironBarReqs.put(Material.OAK_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 11, Material.IRON_INGOT, "&fIron Bar", ironBarReqs,
                "Iron Ore\nOak Log", 999, 10, 0, 0, "",
                true, false, false);
    }

    /**
     * This...
     *
     * @param player
     * @param numberOfItems
     * @param successRate
     * @param inventorySlot
     */
    @Override
    public void produceResult(Player player, int numberOfItems, int successRate, int inventorySlot) {
        ItemStack itemStack = determineItem(inventorySlot);
        produceResult(player, numberOfItems, successRate, itemStack);
    }

    private ItemStack determineItem(int slot) {
        switch (slot) {
            case 9:
                return BlacksmithItems.CHAIN_LINK_ITEMSTACK;
            case 10:
                return BlacksmithItems.IRON_BAR_ITEMSTACK;
            case 11:
                return BlacksmithItems.GOLD_BAR_ITEMSTACK;
        }
        return new ItemStack(Material.STONE); // oops
    }
}
