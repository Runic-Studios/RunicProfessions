package com.runicrealms.plugin.professions.crafting.blacksmith;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.utilities.MenuUtil;
import com.runicrealms.plugin.professions.utilities.itemutil.BlacksmithItems;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class AnvilMenu extends Workstation {

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

    private ItemGUI forgeMenu(Player pl) {

        // grab the player's current profession level, progress toward that level
        int currentLvl = RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).getProfLevel();

        // create three hash maps for the reagents, set to 0 since we've only got 1 reagent
        // level 1
        LinkedHashMap<Material, Integer> chainLinkReqs = new LinkedHashMap<>();
        chainLinkReqs.put(Material.IRON_BARS, 999);
        LinkedHashMap<Material, Integer> goldBarReqs = new LinkedHashMap<>();
        goldBarReqs.put(Material.GOLD_INGOT, 999);
        LinkedHashMap<Material, Integer> ironBarReqs = new LinkedHashMap<>();
        ironBarReqs.put(Material.IRON_INGOT, 999);

        // oaken shield
        LinkedHashMap<Material, Integer> shieldReqs = new LinkedHashMap<>();
        shieldReqs.put(Material.IRON_INGOT, 8);
        shieldReqs.put(Material.OAK_LOG, 1);

        // flail
        LinkedHashMap<Material, Integer> flailReqs = new LinkedHashMap<>();
        flailReqs.put(Material.GOLD_INGOT, 5);
        flailReqs.put(Material.IRON_INGOT, 2);
        flailReqs.put(Material.EMERALD_ORE, 1);
        flailReqs.put(Material.BIRCH_LOG, 2);

        // wand
        LinkedHashMap<Material, Integer> wandReqs = new LinkedHashMap<>();
        wandReqs.put(Material.IRON_INGOT, 5);
        wandReqs.put(Material.DIAMOND_ORE, 2);
        wandReqs.put(Material.BIRCH_LOG, 2);

        // dagger
        LinkedHashMap<Material, Integer> daggerReqs = new LinkedHashMap<>();
        daggerReqs.put(Material.IRON_INGOT, 2);
        daggerReqs.put(Material.NETHER_QUARTZ_ORE, 3);
        daggerReqs.put(Material.OAK_LOG, 2);

        // bastion
        LinkedHashMap<Material, Integer> bastionReqs = new LinkedHashMap<>();
        bastionReqs.put(Material.IRON_INGOT, 8);
        bastionReqs.put(Material.PHANTOM_MEMBRANE, 1);

        // legendary weapons
        LinkedHashMap<Material, Integer> legendaryReqs = new LinkedHashMap<>();
        legendaryReqs.put(Material.IRON_INGOT, 5);
        legendaryReqs.put(Material.NETHER_STAR, 1);

        // legendary off-hands
        LinkedHashMap<Material, Integer> shieldReqs2 = (LinkedHashMap<Material, Integer>) shieldReqs.clone();
        shieldReqs2.put(Material.NETHER_STAR, 3);

        LinkedHashMap<Material, Integer> flailReqs2 = (LinkedHashMap<Material, Integer>) flailReqs.clone();
        flailReqs2.put(Material.NETHER_STAR, 3);

        LinkedHashMap<Material, Integer> wandReqs2 = (LinkedHashMap<Material, Integer>) wandReqs.clone();
        wandReqs2.put(Material.NETHER_STAR, 3);

        LinkedHashMap<Material, Integer> daggerReqs2 = (LinkedHashMap<Material, Integer>) daggerReqs.clone();
        daggerReqs2.put(Material.NETHER_STAR, 3);

        ItemGUI forgeMenu = super.craftingMenu(pl, 36);

        forgeMenu.setOption(4, new ItemStack(Material.ANVIL), "&eAnvil",
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

                int slot = event.getSlot();
                int reqLevel = 0;
                int reagentAmt = 0;
                int exp = 0;
                LinkedHashMap<Material, Integer> reqHashMap = new LinkedHashMap<>();

                if (event.getSlot() == 9) {
                    reqHashMap = ironBarReqs;
                    reagentAmt = 2;
                    exp = 40;
                } else if (slot == 10) {
                    reqLevel = 5;
                    reqHashMap = shieldReqs;
                    exp = 165;
                } else if (slot == 11 || slot == 12) {
                    reqLevel = 10;
                    reqHashMap = ironBarReqs;
                    reagentAmt = 5;
                    exp = 100;
                } else if (slot == 13) {
                    reqLevel = 15;
                    reqHashMap = chainLinkReqs;
                    reagentAmt = 4;
                    exp = 80;
                } else if (slot == 14) {
                    reqLevel = 15;
                    reqHashMap = goldBarReqs;
                    reagentAmt = 4;
                    exp = 80;
                } else if (slot == 15) {
                    reqLevel = 20;
                    reqHashMap = chainLinkReqs;
                    reagentAmt = 7;
                    exp = 140;
                } else if (slot == 16) {
                    reqLevel = 20;
                    reqHashMap = ironBarReqs;
                    reagentAmt = 7;
                    exp = 140;
                } else if (slot == 17) {
                    reqLevel = 20;
                    reqHashMap = flailReqs;
                    exp = 140;
                } else if (slot == 18) {
                    reqLevel = 20;
                    reqHashMap = wandReqs;
                    exp = 120;
                } else if (slot == 19) {
                    reqLevel = 20;
                    reqHashMap = daggerReqs;
                    exp = 60;
                } else if (slot == 20) {
                    reqLevel = 25;
                    reqHashMap = ironBarReqs;
                    reagentAmt = 2;
                    exp = 40;
                } else if (slot == 21) {
                    reqLevel = 30;
                    reqHashMap = bastionReqs;
                    exp = 310;
                } else if (slot == 22 || slot == 23) {
                    reqLevel = 35;
                    reqHashMap = ironBarReqs;
                    reagentAmt = 5;
                    exp = 100;
                } else if (slot == 24) {
                    reqLevel = 40;
                    reqHashMap = goldBarReqs;
                    reagentAmt = 8;
                    exp = 160;
                } else if (slot == 25) {
                    reqLevel = 40;
                    reqHashMap = ironBarReqs;
                    reagentAmt = 8;
                    exp = 160;
                } else if (slot == 26) {
                    reqLevel = 45;
                    reqHashMap = chainLinkReqs;
                    reagentAmt = 5;
                    exp = 100;
                } else if (slot == 27) {
                    reqLevel = 45;
                    reqHashMap = ironBarReqs;
                    reagentAmt = 5;
                    exp = 100;
                } else if (slot == 28) {
                    reqLevel = 50;
                    reqHashMap = legendaryReqs;
                    exp = 400;
                } else if (slot == 29) {
                    reqLevel = 50;
                    reqHashMap = legendaryReqs;
                    exp = 400;
                } else if (slot == 30) {
                    reqLevel = 50;
                    reqHashMap = legendaryReqs;
                    exp = 400;
                } else if (slot == 31) {
                    reqLevel = 60;
                    reqHashMap = shieldReqs2;
                    exp = 0;
                } else if (slot == 32) {
                    reqLevel = 60;
                    reqHashMap = flailReqs2;
                    exp = 0;
                } else if (slot == 33) {
                    reqLevel = 60;
                    reqHashMap = wandReqs2;
                    exp = 0;
                } else if (slot == 34) {
                    reqLevel = 60;
                    reqHashMap = daggerReqs2;
                    exp = 0;
                }

                // destroy instance of inventory to prevent bugs
                event.setWillClose(true);
                event.setWillDestroy(true);

                // craft item based on experience and reagent amount
                super.startCrafting(pl, reqHashMap, reagentAmt, reqLevel, event.getCurrentItem().getType(),
                        meta.getDisplayName(), currentLvl, exp,
                        ((Damageable) meta).getDamage(), Particle.FIREWORKS_SPARK,
                        Sound.BLOCK_ANVIL_PLACE, Sound.BLOCK_ANVIL_USE, slot, mult);
            }});

        return forgeMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player pl) {

        // requirements used to generate lore
        LinkedHashMap<Material, Integer> chainReqs = new LinkedHashMap<>();
        chainReqs.put(Material.IRON_BARS, 999); // amount is irrelevant
        LinkedHashMap<Material, Integer> ironReqs = new LinkedHashMap<>();
        ironReqs.put(Material.IRON_INGOT, 999);
        LinkedHashMap<Material, Integer> goldReqs = new LinkedHashMap<>();
        goldReqs.put(Material.GOLD_INGOT, 999);

        // level 1
        super.createMenuItem(forgeMenu, pl, 9, Material.FLINT, "&fWhetstone", ironReqs,
                "Iron Bar", 2, 40, 0, 0,
                "&eIncrease your weapon⚔ damage by +1 for 3 min!\n",
                false, false, false);

        // level 5
        LinkedHashMap<Material, Integer> shieldReqs = new LinkedHashMap<>();
        shieldReqs.put(Material.IRON_INGOT, 8);
        shieldReqs.put(Material.OAK_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 10, Material.SHIELD, "&fOaken Shield", shieldReqs,
                "Iron Bar\nOak Log", 999, 165, 5, 0, MenuUtil.itemStatsToString(BlacksmithItems.oakenShield()),
                false, false, false);

        // level 10
        super.createMenuItem(forgeMenu, pl, 11, Material.WOODEN_SWORD, "&fForged Iron Broadsword", ironReqs,
                "Iron Bar", 5, 100, 10, 7, MenuUtil.itemStatsToString(BlacksmithItems.ironBroadsword()),
                false, false, false);
        super.createMenuItem(forgeMenu, pl, 12, Material.WOODEN_AXE, "&fForged Iron Reaver", ironReqs,
                "Iron Bar", 5, 100, 10, 7, MenuUtil.itemStatsToString(BlacksmithItems.ironReaver()),
                false, false, false);

        // level 15
        super.createMenuItem(forgeMenu, pl, 13, Material.CHAINMAIL_BOOTS, "&fForged Mail Greaves", chainReqs,
                "Chain Link", 4, 80, 15, 0, MenuUtil.itemStatsToString(BlacksmithItems.mailGreaves()),
                false, false, false);
        super.createMenuItem(forgeMenu, pl, 14, Material.GOLDEN_BOOTS, "&fForged Gilded Boots", goldReqs,
                "Gold Bar", 4, 80, 15, 0, MenuUtil.itemStatsToString(BlacksmithItems.gildedBoots()),
                false, false, false);

        // level 20
        super.createMenuItem(forgeMenu, pl, 15, Material.CHAINMAIL_LEGGINGS, "&fForged Mail Tassets", chainReqs,
                "Chain Link", 7, 140, 20, 0, MenuUtil.itemStatsToString(BlacksmithItems.mailTassets()),
                false, false, false);
        super.createMenuItem(forgeMenu, pl, 16, Material.IRON_LEGGINGS, "&fForged Iron Platelegs", ironReqs,
                "Iron Bar", 7, 140, 20, 0, MenuUtil.itemStatsToString(BlacksmithItems.plateLegs()),
                false, false, false);

        LinkedHashMap<Material, Integer> flailReqs = new LinkedHashMap<>();
        flailReqs.put(Material.GOLD_INGOT, 5);
        flailReqs.put(Material.IRON_INGOT, 2);
        flailReqs.put(Material.EMERALD_ORE, 1);
        flailReqs.put(Material.BIRCH_LOG, 2);
        super.createMenuItem(forgeMenu, pl, 17, Material.STONE_SHOVEL, "&fFlail of Retribution", flailReqs,
                "Gold Bar\nIron Bar\nUncut Emerald\nElder Log", 999, 140, 20, 0, MenuUtil.itemStatsToString(BlacksmithItems.flailOfRetribution()),
                false, false, false);

        LinkedHashMap<Material, Integer> wandReqs = new LinkedHashMap<>();
        wandReqs.put(Material.IRON_INGOT, 5);
        wandReqs.put(Material.DIAMOND_ORE, 2);
        wandReqs.put(Material.BIRCH_LOG, 2);
        super.createMenuItem(forgeMenu, pl, 18, Material.STONE_HOE, "&fIllusioner's Wand", wandReqs,
                "Iron Bar\nUncut Diamond\nElder Log", 999, 120, 20, 0, MenuUtil.itemStatsToString(BlacksmithItems.illusionersWand()),
                false, false, false);

        LinkedHashMap<Material, Integer> daggerReqs = new LinkedHashMap<>();
        daggerReqs.put(Material.IRON_INGOT, 2);
        daggerReqs.put(Material.NETHER_QUARTZ_ORE, 3);
        daggerReqs.put(Material.OAK_LOG, 2);
        super.createMenuItem(forgeMenu, pl, 19, Material.STONE_SWORD, "&fEtched Dagger", daggerReqs,
                "Iron Bar\nUncut Opal\nOak Log", 999, 60, 20, 0, MenuUtil.itemStatsToString(BlacksmithItems.etchedDagger()),
                false, false, false);

        // level 25
        super.createMenuItem(forgeMenu, pl, 20, Material.FLINT, "&fSharpening Stone", ironReqs,
                "Iron Bar", 2, 40, 25, 0,
                "&eIncrease your weapon⚔ damage by +3 for 3 min!\n",
                false, false, false);

        // level 30
        LinkedHashMap<Material, Integer> bastionReqs = new LinkedHashMap<>();
        bastionReqs.put(Material.IRON_INGOT, 8);
        bastionReqs.put(Material.PHANTOM_MEMBRANE, 1);
        super.createMenuItem(forgeMenu, pl, 21, Material.SHIELD, "&fBastion", bastionReqs,
                "Iron Bar\nFlag of Honor", 999, 310, 30, 0, MenuUtil.itemStatsToString(BlacksmithItems.bastion()),
                false, false, false);

        // level 35
        super.createMenuItem(forgeMenu, pl, 22, Material.BOW, "&fForged Iron Longbow", chainReqs,
                "Iron Bar", 5, 100, 35, 7, MenuUtil.itemStatsToString(BlacksmithItems.ironLongbow()),
                false, false, false);
        super.createMenuItem(forgeMenu, pl, 23, Material.WOODEN_HOE, "&fForged Iron Scepter", ironReqs,
                "Iron Bar", 5, 100, 35, 7, MenuUtil.itemStatsToString(BlacksmithItems.ironScepter()),
                false, false, false);

        // level 40
        super.createMenuItem(forgeMenu, pl, 24, Material.GOLDEN_CHESTPLATE, "&fForged Gilded Body", goldReqs,
                "Gold Bar", 8, 160, 40, 0, MenuUtil.itemStatsToString(BlacksmithItems.gildedBody()),
                false, false, false);
        super.createMenuItem(forgeMenu, pl, 25, Material.IRON_CHESTPLATE, "&fForged Iron Platebody", ironReqs,
                "Iron Bar", 8, 160, 40, 0, MenuUtil.itemStatsToString(BlacksmithItems.plateBody()),
                false, false, false);

        // level 45
        super.createMenuItem(forgeMenu, pl, 26, Material.CHAINMAIL_HELMET, "&fForged Mail Helm", chainReqs,
                "Chain Link", 5, 100, 45, 0, MenuUtil.itemStatsToString(BlacksmithItems.mailHelm()),
                false, false, false);
        super.createMenuItem(forgeMenu, pl, 27, Material.IRON_HELMET, "&fForged Iron Helm", ironReqs,
                "Iron Bar", 5, 100, 45, 0, MenuUtil.itemStatsToString(BlacksmithItems.ironHelm()),
                false, false, false);

        // level 50 - legendary weapons
        LinkedHashMap<Material, Integer> legenReqs = new LinkedHashMap<>();
        legenReqs.put(Material.IRON_INGOT, 5);
        legenReqs.put(Material.NETHER_STAR, 1);
        super.createMenuItem(forgeMenu, pl, 28, Material.BOW, "&6Stormsong", legenReqs,
                "Iron Bar\nToken of Valor", 999, 400, 50, 10,
                MenuUtil.itemStatsToString(BlacksmithItems.Stormsong()),
                true, false, false);
        super.createMenuItem(forgeMenu, pl, 29, Material.WOODEN_SWORD, "&6Valkyrie", legenReqs,
                "Iron Bar\nToken of Valor", 999, 400, 50, 10,
                MenuUtil.itemStatsToString(BlacksmithItems.Valkyrie()),
                true, false, false);
        super.createMenuItem(forgeMenu, pl, 30, Material.WOODEN_AXE, "&6The Minotaur", legenReqs,
                "Iron Bar\nToken of Valor", 999, 400, 50, 10,
                MenuUtil.itemStatsToString(BlacksmithItems.theMinotaur()),
                true, false, false);

        // level 60 - legendary off-hands
        LinkedHashMap<Material, Integer> shieldReqs2 = new LinkedHashMap<>();
        shieldReqs2.put(Material.IRON_INGOT, 8);
        shieldReqs2.put(Material.OAK_LOG, 1);
        shieldReqs2.put(Material.NETHER_STAR, 3);
        super.createMenuItem(forgeMenu, pl, 31, Material.SHIELD, getName(BlacksmithItems.bulwark()), shieldReqs2,
                "Iron Bar\nOak Log\nToken of Valor", 999, 0, 60, 0,
                MenuUtil.itemStatsToString(BlacksmithItems.bulwark()),
                false, false, false);

        LinkedHashMap<Material, Integer> flailReqs2 = new LinkedHashMap<>();
        flailReqs2.put(Material.GOLD_INGOT, 5);
        flailReqs2.put(Material.IRON_INGOT, 2);
        flailReqs2.put(Material.EMERALD_ORE, 1);
        flailReqs2.put(Material.BIRCH_LOG, 2);
        flailReqs2.put(Material.NETHER_STAR, 3);
        super.createMenuItem(forgeMenu, pl, 32, Material.STONE_SHOVEL, getName(BlacksmithItems.redeemersFlail()), flailReqs2,
                "Gold Bar\nIron Bar\nUncut Emerald\nElder Log\nToken of Valor", 999, 0, 60, 0,
                MenuUtil.itemStatsToString(BlacksmithItems.redeemersFlail()),
                false, false, false);

        LinkedHashMap<Material, Integer> wandReqs2 = new LinkedHashMap<>();
        wandReqs2.put(Material.IRON_INGOT, 5);
        wandReqs2.put(Material.DIAMOND_ORE, 2);
        wandReqs2.put(Material.BIRCH_LOG, 2);
        wandReqs2.put(Material.NETHER_STAR, 3);
        super.createMenuItem(forgeMenu, pl, 33, Material.STONE_HOE, getName(BlacksmithItems.icefuryWand()), wandReqs2,
                "Iron Bar\nUncut Diamond\nElder Log\nToken of Valor", 999, 0, 60, 0,
                MenuUtil.itemStatsToString(BlacksmithItems.icefuryWand()),
                false, false, false);

        LinkedHashMap<Material, Integer> daggerReqs2 = new LinkedHashMap<>();
        daggerReqs2.put(Material.IRON_INGOT, 2);
        daggerReqs2.put(Material.NETHER_QUARTZ_ORE, 3);
        daggerReqs2.put(Material.OAK_LOG, 2);
        daggerReqs2.put(Material.NETHER_STAR, 3);
        super.createMenuItem(forgeMenu, pl, 34, Material.STONE_SWORD, getName(BlacksmithItems.blackSteelDirk()), daggerReqs2,
                "Iron Bar\nUncut Opal\nOak Log\nToken of Valor", 999, 0, 60, 0,
                MenuUtil.itemStatsToString(BlacksmithItems.blackSteelDirk()),
                false, false, false);
    }

    /**
     * @param someVar is the slot of the item in the crafting menu. confusing, I know.
     */
    @Override
    public void produceResult(Player pl, Material material, String dispName,
                              int currentLvl, int amt, int rate, int durability, int someVar) {

        ItemStack craftedItem = determineItem(someVar); // someVar is the slot of the item in the menu.

        // create a new item up to the amount
        int failCount = 0;
        for (int i = 0; i < amt; i++) {

            double chance = ThreadLocalRandom.current().nextDouble(0, 100);
            if (chance <= rate) {
                if (pl.getInventory().firstEmpty() != -1) {
                    int firstEmpty = pl.getInventory().firstEmpty();
                    pl.getInventory().setItem(firstEmpty, craftedItem);
                } else {
                    pl.getWorld().dropItem(pl.getLocation(), craftedItem);
                }
            } else {
                failCount = failCount + 1;
            }
        }

        // display fail message
        if (failCount == 0) return;
        pl.sendMessage(ChatColor.RED + "You fail to craft this item. [x" + failCount + "]");
    }

    private ItemStack determineItem(int slot) {
        ItemStack item = new ItemStack(Material.STICK);
        switch (slot) {
            case 9:
                item = BlacksmithItems.whetStone();
                break;
            case 10:
                item = BlacksmithItems.oakenShield();
                break;
            case 11:
                item = BlacksmithItems.ironBroadsword();
                break;
            case 12:
                item = BlacksmithItems.ironReaver();
                break;
            case 13:
                item = BlacksmithItems.mailGreaves();
                break;
            case 14:
                item = BlacksmithItems.gildedBoots();
                break;
            case 15:
                item = BlacksmithItems.mailTassets();
                break;
            case 16:
                item = BlacksmithItems.plateLegs();
                break;
            case 17:
                item = BlacksmithItems.flailOfRetribution();
                break;
            case 18:
                item = BlacksmithItems.illusionersWand();
                break;
            case 19:
                item = BlacksmithItems.etchedDagger();
                break;
            case 20:
                item = BlacksmithItems.sharpStone();
                break;
            case 21:
                item = BlacksmithItems.bastion();
                break;
            case 22:
                item = BlacksmithItems.ironLongbow();
                break;
            case 23:
                item = BlacksmithItems.ironScepter();
                break;
            case 24:
                item = BlacksmithItems.gildedBody();
                break;
            case 25:
                item = BlacksmithItems.plateBody();
                break;
            case 26:
                item = BlacksmithItems.mailHelm();
                break;
            case 27:
                item = BlacksmithItems.ironHelm();
                break;
            case 28:
                item = BlacksmithItems.Stormsong();
                break;
            case 29:
                item = BlacksmithItems.Valkyrie();
                break;
            case 30:
                item = BlacksmithItems.theMinotaur();
                break;
            case 31:
                item = BlacksmithItems.bulwark();
                break;
            case 32:
                item = BlacksmithItems.redeemersFlail();
                break;
            case 33:
                item = BlacksmithItems.icefuryWand();
                break;
            case 34:
                item = BlacksmithItems.blackSteelDirk();
                break;
        }
        return item;
    }
}
