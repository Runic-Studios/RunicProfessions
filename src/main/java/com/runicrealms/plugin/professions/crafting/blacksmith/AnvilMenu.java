package com.runicrealms.plugin.professions.crafting.blacksmith;

import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
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

    public AnvilMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player pl) {

        // name the menu
        super.setupWorkstation("&f&l" + pl.getName() + "'s &e&lAnvil");
        ItemGUI blackSmithMenu = getItemGUI();

        //set the visual items
        blackSmithMenu.setOption(3, new ItemStack(Material.IRON_CHESTPLATE),
                "&fCraft Armor", "&7Forge mail, gilded or plate armor!", 0, false);

        // set the handler
        blackSmithMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the forging menu
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(forgeMenu(pl));
                this.setTitle(forgeMenu(pl).getName());
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
        this.setItemGUI(blackSmithMenu);
    }

    private ItemGUI forgeMenu(Player player) {

        ItemGUI forgeMenu = super.craftingMenu(player, ANVIL_MENU_SIZE);

        forgeMenu.setOption(4, new ItemStack(Material.ANVIL), "&eAnvil",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(forgeMenu, player);

        forgeMenu.setHandler(event -> {

            if (event.getSlot() == 4) {

                // return to the first menu
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
                                player, craftedResource, ((Damageable) meta).getDamage(), Particle.FIREWORKS_SPARK,
                                Sound.BLOCK_ANVIL_PLACE, Sound.BLOCK_ANVIL_USE, mult, false
                        );
            }
        });

        return forgeMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player player) {
        createMenuItem(forgeMenu, player, CraftedResource.OFFHAND_VIT_10, 9);

        // requirements used to generate lore
//        LinkedHashMap<Material, Integer> chainReqs = new LinkedHashMap<>();
//        chainReqs.put(Material.IRON_BARS, 999); // amount is irrelevant
//        LinkedHashMap<Material, Integer> ironReqs = new LinkedHashMap<>();
//        ironReqs.put(Material.IRON_INGOT, 999);
//        LinkedHashMap<Material, Integer> goldReqs = new LinkedHashMap<>();
//        goldReqs.put(Material.GOLD_INGOT, 999);

//        // level 1
//        super.createMenuItem(forgeMenu, pl, 9, Material.FLINT, "&fWhetstone", ironReqs,
//                "Iron Bar", 2, 40, 0, 0,
//                generateItemLore(BlacksmithItems.WHETSTONE));
//
//        // level 5
//        LinkedHashMap<Material, Integer> shieldReqs = new LinkedHashMap<>();
//        shieldReqs.put(Material.IRON_INGOT, 8);
//        shieldReqs.put(Material.OAK_LOG, 1);
//        super.createMenuItem(forgeMenu, pl, 10, Material.SHIELD, "&fOaken Shield", shieldReqs,
//                "Iron Bar\nOak Log", 999, 165, 5, 0, this.generateItemLore(BlacksmithItems.OAKEN_SHIELD));
//
//        // level 10
//        super.createMenuItem(forgeMenu, pl, 11, Material.WOODEN_SWORD, "&fForged Iron Broadsword", ironReqs,
//                "Iron Bar", 5, 100, 10, 7, this.generateItemLore(BlacksmithItems.FORGED_IRON_BROADSWORD));
//        super.createMenuItem(forgeMenu, pl, 12, Material.WOODEN_AXE, "&fForged Iron Reaver", ironReqs,
//                "Iron Bar", 5, 100, 10, 7, this.generateItemLore(BlacksmithItems.FORGED_IRON_REAVER));
//
//        // level 15
//        super.createMenuItem(forgeMenu, pl, 13, Material.CHAINMAIL_BOOTS, "&fForged Mail Greaves", chainReqs,
//                "Chain Link", 4, 80, 15, 0, this.generateItemLore(BlacksmithItems.FORGED_MAIL_GREAVES));
//        super.createMenuItem(forgeMenu, pl, 14, Material.GOLDEN_BOOTS, "&fForged Gilded Boots", goldReqs,
//                "Gold Bar", 4, 80, 15, 0, this.generateItemLore(BlacksmithItems.FORGED_GILDED_BOOTS));
//
//        // level 20
//        super.createMenuItem(forgeMenu, pl, 15, Material.CHAINMAIL_LEGGINGS, "&fForged Mail Tassets", chainReqs,
//                "Chain Link", 7, 140, 20, 0, this.generateItemLore(BlacksmithItems.FORGED_MAIL_TASSEST));
//        super.createMenuItem(forgeMenu, pl, 16, Material.IRON_LEGGINGS, "&fForged Iron Platelegs", ironReqs,
//                "Iron Bar", 7, 140, 20, 0, this.generateItemLore(BlacksmithItems.FORGED_IRON_PLATELEGS));
//
//        LinkedHashMap<Material, Integer> flailReqs = new LinkedHashMap<>();
//        flailReqs.put(Material.GOLD_INGOT, 5);
//        flailReqs.put(Material.IRON_INGOT, 2);
//        flailReqs.put(Material.EMERALD_ORE, 1);
//        flailReqs.put(Material.BIRCH_LOG, 2);
//        super.createMenuItem(forgeMenu, pl, 17, Material.STONE_SHOVEL, "&fFlail of Retribution", flailReqs,
//                "Gold Bar\nIron Bar\nUncut Emerald\nElder Log", 999, 140, 20, 0, this.generateItemLore(BlacksmithItems.FLAIL_OF_RETRIBUTION));
//
//        LinkedHashMap<Material, Integer> wandReqs = new LinkedHashMap<>();
//        wandReqs.put(Material.IRON_INGOT, 5);
//        wandReqs.put(Material.DIAMOND_ORE, 2);
//        wandReqs.put(Material.BIRCH_LOG, 2);
//        super.createMenuItem(forgeMenu, pl, 18, Material.STONE_HOE, "&fIllusioner's Wand", wandReqs,
//                "Iron Bar\nUncut Diamond\nElder Log", 999, 120, 20, 0, this.generateItemLore(BlacksmithItems.ILLUSIONERS_WAND));
//
//        LinkedHashMap<Material, Integer> daggerReqs = new LinkedHashMap<>();
//        daggerReqs.put(Material.IRON_INGOT, 2);
//        daggerReqs.put(Material.NETHER_QUARTZ_ORE, 3);
//        daggerReqs.put(Material.OAK_LOG, 2);
//        super.createMenuItem(this, pl, 19, Material.STONE_SWORD, "&fEtched Dagger", daggerReqs,
//                "Iron Bar\nUncut Opal\nOak Log", 999, 60, 20, 0, this.generateItemLore(BlacksmithItems.ETCHED_DAGGER));
//
//        // level 25
//        super.createMenuItem(forgeMenu, pl, 20, Material.FLINT, "&fSharpening Stone", ironReqs,
//                "Iron Bar", 2, 40, 25, 0,
//                generateItemLore(BlacksmithItems.SHARPENING_STONE));
//
//        // level 30
//        LinkedHashMap<Material, Integer> bastionReqs = new LinkedHashMap<>();
//        bastionReqs.put(Material.IRON_INGOT, 8);
//        bastionReqs.put(Material.PHANTOM_MEMBRANE, 1);
//        super.createMenuItem(forgeMenu, pl, 21, Material.SHIELD, "&fBastion", bastionReqs,
//                "Iron Bar\nFlag of Honor", 999, 310, 30, 0, this.generateItemLore(BlacksmithItems.BASTION));
//
//        // level 35
//        super.createMenuItem(forgeMenu, pl, 22, Material.BOW, "&fForged Iron Longbow", chainReqs,
//                "Iron Bar", 5, 100, 35, 7, this.generateItemLore(BlacksmithItems.FORGED_IRON_LONGBOW));
//        super.createMenuItem(forgeMenu, pl, 23, Material.WOODEN_HOE, "&fForged Iron Scepter", ironReqs,
//                "Iron Bar", 5, 100, 35, 7, this.generateItemLore(BlacksmithItems.FORGED_IRON_SCEPTER));
//
//        // level 40
//        super.createMenuItem(forgeMenu, pl, 24, Material.GOLDEN_CHESTPLATE, "&fForged Gilded Body", goldReqs,
//                "Gold Bar", 8, 160, 40, 0, this.generateItemLore(BlacksmithItems.FORGED_GILDED_BODY));
//        super.createMenuItem(forgeMenu, pl, 25, Material.IRON_CHESTPLATE, "&fForged Iron Platebody", ironReqs,
//                "Iron Bar", 8, 160, 40, 0, this.generateItemLore(BlacksmithItems.FORGED_IRON_PLATEBODY));
//
//        // level 45
//        super.createMenuItem(forgeMenu, pl, 26, Material.CHAINMAIL_HELMET, "&fForged Mail Helm", chainReqs,
//                "Chain Link", 5, 100, 45, 0, this.generateItemLore(BlacksmithItems.FORGED_MAIL_HELM));
//        super.createMenuItem(forgeMenu, pl, 27, Material.IRON_HELMET, "&fForged Iron Helm", ironReqs,
//                "Iron Bar", 5, 100, 45, 0, this.generateItemLore(BlacksmithItems.FORGED_IRON_HELM));
//
//        // level 50 - legendary weapons
//        LinkedHashMap<Material, Integer> legenReqs = new LinkedHashMap<>();
//        legenReqs.put(Material.IRON_INGOT, 5);
//        legenReqs.put(Material.NETHER_STAR, 1);
//        super.createMenuItem(forgeMenu, pl, 28, Material.BOW, "&6Stormsong", legenReqs,
//                "Iron Bar\nToken of Valor", 999, 400, 50, 10,
//                this.generateItemLore(BlacksmithItems.STORMSONG),
//                true, false, false);
//        super.createMenuItem(forgeMenu, pl, 29, Material.WOODEN_SWORD, "&6Valkyrie", legenReqs,
//                "Iron Bar\nToken of Valor", 999, 400, 50, 10,
//                this.generateItemLore(BlacksmithItems.VALKYRIE),
//                true, false, false);
//        super.createMenuItem(forgeMenu, pl, 30, Material.WOODEN_AXE, "&6The Minotaur", legenReqs,
//                "Iron Bar\nToken of Valor", 999, 400, 50, 10,
//                this.generateItemLore(BlacksmithItems.THE_MINOTAUR),
//                true, false, false);
//
//        // level 60 - legendary off-hands
//        LinkedHashMap<Material, Integer> shieldReqs2 = new LinkedHashMap<>();
//        shieldReqs2.put(Material.IRON_INGOT, 8);
//        shieldReqs2.put(Material.OAK_LOG, 1);
//        shieldReqs2.put(Material.NETHER_STAR, 3);
//        super.createMenuItem(forgeMenu, pl, 31, Material.SHIELD, "&6Frost Lord's Bulwark", shieldReqs2,
//                "Iron Bar\nOak Log\nToken of Valor", 999, 0, 60, 0,
//                this.generateItemLore(BlacksmithItems.FROST_LORDS_BULWARK));
//
//        LinkedHashMap<Material, Integer> flailReqs2 = new LinkedHashMap<>();
//        flailReqs2.put(Material.GOLD_INGOT, 5);
//        flailReqs2.put(Material.IRON_INGOT, 2);
//        flailReqs2.put(Material.EMERALD_ORE, 1);
//        flailReqs2.put(Material.BIRCH_LOG, 2);
//        flailReqs2.put(Material.NETHER_STAR, 3);
//        super.createMenuItem(forgeMenu, pl, 32, Material.STONE_SHOVEL, "&6Redeemer's Flail", flailReqs2,
//                "Gold Bar\nIron Bar\nUncut Emerald\nElder Log\nToken of Valor", 999, 0, 60, 0,
//                this.generateItemLore(BlacksmithItems.REDEEMERS_FLAIL));
//
//        LinkedHashMap<Material, Integer> wandReqs2 = new LinkedHashMap<>();
//        wandReqs2.put(Material.IRON_INGOT, 5);
//        wandReqs2.put(Material.DIAMOND_ORE, 2);
//        wandReqs2.put(Material.BIRCH_LOG, 2);
//        wandReqs2.put(Material.NETHER_STAR, 3);
//        super.createMenuItem(forgeMenu, pl, 33, Material.STONE_HOE, "&6Icefury Wand", wandReqs2,
//                "Iron Bar\nUncut Diamond\nElder Log\nToken of Valor", 999, 0, 60, 0,
//                this.generateItemLore(BlacksmithItems.ICEFURY_WAND));
//
//        LinkedHashMap<Material, Integer> daggerReqs2 = new LinkedHashMap<>();
//        daggerReqs2.put(Material.IRON_INGOT, 2);
//        daggerReqs2.put(Material.NETHER_QUARTZ_ORE, 3);
//        daggerReqs2.put(Material.OAK_LOG, 2);
//        daggerReqs2.put(Material.NETHER_STAR, 3);
//        super.createMenuItem(forgeMenu, pl, 34, Material.STONE_SWORD, "&6Black Steel Dirk", daggerReqs2,
//                "Iron Bar\nUncut Opal\nOak Log\nToken of Valor", 999, 0, 60, 0,
//                this.generateItemLore(BlacksmithItems.BLACK_STEEL_DIRK));
    }

    private CraftedResource determineItem(int slot) {
        switch (slot) {
            case 9:
                return CraftedResource.OFFHAND_VIT_10;
        }
        return CraftedResource.OFFHAND_VIT_10;
    }
}
