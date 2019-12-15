package com.runicrealms.plugin.professions.crafting;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.LegendaryManager;
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

public class BSAnvilGUI extends Workstation {

    public BSAnvilGUI(Player pl) {
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
        int currentLvl = RunicCore.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.level");

        // create three hash maps for the reagents, set to 0 since we've only got 1 reagent
        // level 1
        LinkedHashMap<Material, Integer> chainLinkReqs = new LinkedHashMap<>();
        chainLinkReqs.put(Material.IRON_BARS, 999);
        LinkedHashMap<Material, Integer> goldBarReqs = new LinkedHashMap<>();
        goldBarReqs.put(Material.GOLD_INGOT, 999);
        LinkedHashMap<Material, Integer> ironBarReqs = new LinkedHashMap<>();
        ironBarReqs.put(Material.IRON_INGOT, 999);

        // level 5
        LinkedHashMap<Material, Integer> shieldReqs = new LinkedHashMap<>();
        shieldReqs.put(Material.IRON_INGOT, 8);
        shieldReqs.put(Material.OAK_LOG, 1);

        // legendary
        LinkedHashMap<Material, Integer> arrowHeadReqs = new LinkedHashMap<>();
        arrowHeadReqs.put(Material.NETHER_STAR, 1);
        arrowHeadReqs.put(Material.IRON_INGOT, 3);

        LinkedHashMap<Material, Integer> powderReqs = new LinkedHashMap<>();
        powderReqs.put(Material.NETHER_STAR, 1);
        powderReqs.put(Material.GOLDEN_CARROT, 1);

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
                LinkedHashMap<Material, Integer> reqHashMap;

                if (event.getSlot() == 9) {
                    reqHashMap = ironBarReqs;
                    exp = 40;
                } else if (slot == 10) {
                    reqHashMap = shieldReqs;
                    reqLevel = 5;
                    exp = 165;
                } else if (slot == 11 || slot == 12) {
                    reqHashMap = ironBarReqs;
                    reqLevel = 10;
                    reagentAmt = 5;
                    exp = 100;
                } else {
                    reqHashMap = ironBarReqs;
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
                "&eIncrease your weapon⚔ damage by +1 for 3 min!",
                false, false, false);

        // level 5
        LinkedHashMap<Material, Integer> shieldReqs = new LinkedHashMap<>();
        shieldReqs.put(Material.IRON_INGOT, 8);
        shieldReqs.put(Material.OAK_LOG, 1);
        super.createMenuItem(forgeMenu, pl, 10, Material.SHIELD, "&fOaken Shield", shieldReqs,
                "Iron Bar\nOak Log", 999, 165, 5, 0,
                "&c+ "
                        + (int) AttributeUtil.getGenericDouble(BlacksmithItems.oakenShield(), "generic.maxHealth")
                        + "❤\n&f+ "
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.oakenShield(), "custom.shield")
                        + "■",
                false, false, false);

        // level 10
        super.createMenuItem(forgeMenu, pl, 11, Material.WOODEN_SWORD, "&fIron Broadsword", ironReqs,
                "Iron Bar", 5, 100, 10, 6,
                "&c+ "
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironBroadsword(), "custom.minDamage")
                        + "-"
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironBroadsword(), "custom.maxDamage")
                        + "⚔",
                false, false, false);
        super.createMenuItem(forgeMenu, pl, 12, Material.WOODEN_AXE, "&fIron Reaver", ironReqs,
                "Iron Bar", 5, 100, 10, 6,
                "&c+ "
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironReaver(), "custom.minDamage")
                        + "-"
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironReaver(), "custom.maxDamage")
                        + "⚔",
                false, false, false);

        // level 15
        super.createMenuItem(forgeMenu, pl, 13, Material.CHAINMAIL_BOOTS, "&fForged Mail Greaves", chainReqs,
                "Chain Link", 4, 80, 15, 0,
                "&c+ "
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironReaver(), "custom.minDamage")
                        + "-"
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironReaver(), "custom.maxDamage")
                        + "⚔",
                false, false, false);
        super.createMenuItem(forgeMenu, pl, 14, Material.GOLDEN_BOOTS, "&fForged Gilded Boots", goldReqs,
                "Gold Bar", 4, 80, 15, 0,
                "&c+ "
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironReaver(), "custom.minDamage")
                        + "-"
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironReaver(), "custom.maxDamage")
                        + "⚔",
                false, false, false);

        // level 20
        super.createMenuItem(forgeMenu, pl, 15, Material.CHAINMAIL_LEGGINGS, "&fForged Mail Greaves", chainReqs,
                "Chain Link", 4, 80, 15, 0,
                "&c+ "
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironReaver(), "custom.minDamage")
                        + "-"
                        + (int) AttributeUtil.getCustomDouble(BlacksmithItems.ironReaver(), "custom.maxDamage")
                        + "⚔",
                false, false, false);
        super.createMenuItem(forgeMenu, pl, 16, Material.IRON_LEGGINGS, "&fForged Gilded Boots", goldReqs,
                "Gold Bar", 4, 80, 15, 0, MenuUtil.itemStatsToString(BlacksmithItems.plateLegs()),
                false, false, false);

        // level 50 - legendary bow
        LinkedHashMap<Material, Integer> scorpionReqs = new LinkedHashMap<>();
        scorpionReqs.put(Material.NETHER_STAR, 1);
        scorpionReqs.put(Material.IRON_INGOT, 5);
        super.createMenuItem(forgeMenu, pl, 26, Material.BOW, "&6Scorpion", scorpionReqs,
                "Token of Valor\nIron Bar", 999, 0, 50, 7,
                "&c+ "
                        + (int) AttributeUtil.getCustomDouble(LegendaryManager.frostforgedArrowhead(), "custom.attackDamage")
                        + "⚔\n&3+ "
                        + (int) AttributeUtil.getCustomDouble(LegendaryManager.frostforgedArrowhead(), "custom.magicDamage")
                        + "ʔ",
                true, false, false);
        // level 55 - legendary sword
        super.createMenuItem(forgeMenu, pl, 27, Material.WOODEN_SWORD, "&6Nightshade", scorpionReqs,
                "Token of Valor\nIron Bar", 999, 0, 50, 7,
                "&c+ "
                        + (int) AttributeUtil.getCustomDouble(LegendaryManager.frostforgedArrowhead(), "custom.attackDamage")
                        + "⚔\n&3+ "
                        + (int) AttributeUtil.getCustomDouble(LegendaryManager.frostforgedArrowhead(), "custom.magicDamage")
                        + "ʔ",
                true, false, false);
        // level 60 - legendary axe
        super.createMenuItem(forgeMenu, pl, 28, Material.WOODEN_AXE, "&6Warmonger", scorpionReqs,
                "Token of Valor\nIron Bar", 999, 0, 50, 7,
                "&c+ "
                        + (int) AttributeUtil.getCustomDouble(LegendaryManager.frostforgedArrowhead(), "custom.attackDamage")
                        + "⚔\n&3+ "
                        + (int) AttributeUtil.getCustomDouble(LegendaryManager.frostforgedArrowhead(), "custom.magicDamage")
                        + "ʔ",
                true, false, false);
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
        }
        return item;
    }
}
