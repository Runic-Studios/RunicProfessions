package com.runicrealms.plugin.professions.crafting.alchemist;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.utilities.ColorUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class CauldronMenu extends Workstation {

    public CauldronMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player pl) {

        // name the menu
        super.setupWorkstation("&f&l" + pl.getName() + "'s &e&lCauldron");
        ItemGUI baseMenu = getItemGUI();

        //set the visual items
        baseMenu.setOption(3, potionItem(Color.RED, "", ""),
                "&fBrew Potions", "&7Brew powerful and unique potions!", 0, false);

        // set the handler
        baseMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the forging menu
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(cauldronMenu(pl));
                this.setTitle(cauldronMenu(pl).getName());
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
        this.setItemGUI(baseMenu);
    }

    private ItemGUI cauldronMenu(Player pl) {

        // grab the player's current profession level, progress toward that level
        int currentLvl = RunicCoreAPI.getPlayerCache(pl).getProfLevel();

        // health potion
        LinkedHashMap<Material, Integer> healthPotReqs = new LinkedHashMap<>();
        healthPotReqs.put(Material.GLASS_BOTTLE, 1);
        healthPotReqs.put(Material.REDSTONE_ORE, 1);
        healthPotReqs.put(Material.SALMON, 1);

        // mana potion
        LinkedHashMap<Material, Integer> manaPotReqs = new LinkedHashMap<>();
        manaPotReqs.put(Material.GLASS_BOTTLE, 1);
        manaPotReqs.put(Material.LAPIS_ORE, 1);
        manaPotReqs.put(Material.COD, 1);

        // slaying potion
        LinkedHashMap<Material, Integer> slayPotReqs = new LinkedHashMap<>();
        slayPotReqs.put(Material.GLASS_BOTTLE, 1);
        slayPotReqs.put(Material.NETHER_QUARTZ_ORE, 1);
        slayPotReqs.put(Material.DIAMOND_ORE, 1);
        slayPotReqs.put(Material.TROPICAL_FISH, 1);

        // looting potion
        LinkedHashMap<Material, Integer> lootPotReqs = new LinkedHashMap<>();
        lootPotReqs.put(Material.GLASS_BOTTLE, 1);
        lootPotReqs.put(Material.GOLDEN_CARROT, 1);
        lootPotReqs.put(Material.PUFFERFISH, 1);

        // sacred fire potion
        LinkedHashMap<Material, Integer> sacredFirePotReqs = new LinkedHashMap<>();
        sacredFirePotReqs.put(Material.GLASS_BOTTLE, 1);
        sacredFirePotReqs.put(Material.NETHER_WART, 1);
        sacredFirePotReqs.put(Material.PUFFERFISH, 1);

        ItemGUI cauldronMenu = super.craftingMenu(pl, 18);

        cauldronMenu.setOption(4, new ItemStack(Material.CAULDRON), "&eCauldron",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(cauldronMenu, pl, currentLvl);

        cauldronMenu.setHandler(event -> {

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
                int dummyVar = 0;
                int reqLevel = 0;
                int exp = 0;
                LinkedHashMap<Material, Integer> reqHashMap = new LinkedHashMap<>();

                // health potion
                if (slot == 9) {
                    reqHashMap = healthPotReqs;
                    exp = 35;
                    if (currentLvl < 30) {
                        dummyVar = 50;
                    } else if (currentLvl < 50) {
                        dummyVar = 100;
                    } else if (currentLvl < 60) {
                        dummyVar = 200;
                    } else {
                        dummyVar = 350;
                    }
                    // mana potion
                } else if (slot == 10) {
                    reqLevel = 10;
                    reqHashMap = manaPotReqs;
                    exp = 60;
                    if (currentLvl < 30) {
                        dummyVar = 50;
                    } else if (currentLvl < 50) {
                        dummyVar = 100;
                    } else if (currentLvl < 60) {
                        dummyVar = 200;
                    } else {
                        dummyVar = 350;
                    }
                    // slaying potion
                } else if (slot == 11) {
                    reqLevel = 25;
                    reqHashMap = slayPotReqs;
                    exp = 90;
                    if (currentLvl < 30) {
                        dummyVar = 5;
                    } else if (currentLvl < 50) {
                        dummyVar = 10;
                    } else if (currentLvl < 60) {
                        dummyVar = 15;
                    } else {
                        dummyVar = 20;
                    }
                    // looting potion
                } else if (slot == 12) {
                    reqLevel = 40;
                    reqHashMap = lootPotReqs;
                    exp = 600;
                    if (currentLvl < 30) {
                        dummyVar = 5;
                    } else if (currentLvl < 50) {
                        dummyVar = 10;
                    } else if (currentLvl < 60) {
                        dummyVar = 15;
                    } else {
                        dummyVar = 20;
                    }
                    // fire potion
                } else if (slot == 13) {
                    reqLevel = 60;
                    reqHashMap = sacredFirePotReqs;
                    exp = 0;
                    dummyVar = 20;
                }

                // destroy instance of inventory to prevent bugs
                event.setWillClose(true);
                event.setWillDestroy(true);

                // craft item based on experience and reagent amount
                super.startCrafting(pl, reqHashMap, 999, reqLevel, event.getCurrentItem().getType(),
                        meta.getDisplayName(), currentLvl, exp,
                        ((Damageable) meta).getDamage(), Particle.WATER_SPLASH,
                        Sound.BLOCK_BREWING_STAND_BREW, Sound.ENTITY_GENERIC_DRINK, dummyVar, mult);
            }
        });

        return cauldronMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player pl, int currentLv) {

        String healthStr;
        String manaStr;
        String slayingStr;
        String lootingStr;
        String tierStr;
        if (currentLv < 30) {
            healthStr = "50";
            manaStr = "50";
            slayingStr = "5";
            lootingStr = "5";
            tierStr = "Lesser Crafted";
        } else if (currentLv < 50) {
            healthStr = "100";
            manaStr = "100";
            slayingStr = "10";
            lootingStr = "10";
            tierStr = "Minor Crafted";
        } else if (currentLv < 60) {
            healthStr = "200";
            manaStr = "200";
            slayingStr = "15";
            lootingStr = "15";
            tierStr = "Major Crafted";
        } else {
            healthStr = "350";
            manaStr = "350";
            slayingStr = "20";
            lootingStr = "20";
            tierStr = "Greater Crafted";
        }

        // health potion
        LinkedHashMap<Material, Integer> healthPotReqs = new LinkedHashMap<>();
        healthPotReqs.put(Material.GLASS_BOTTLE, 1);
        healthPotReqs.put(Material.REDSTONE_ORE, 1);
        healthPotReqs.put(Material.SALMON, 1);
        super.createMenuItem(forgeMenu, pl, 9, Material.POTION, "&c" + tierStr + " Potion of Healing", healthPotReqs,
                "Glass Bottle\nUncut Ruby\nSalmon", 5, 35, 0, 5,
                "&eRestores &c" + healthStr + "❤ &eon use\n",
                false, true, false);

        // mana potion
        LinkedHashMap<Material, Integer> manaPotReqs = new LinkedHashMap<>();
        manaPotReqs.put(Material.GLASS_BOTTLE, 1);
        manaPotReqs.put(Material.LAPIS_ORE, 1);
        manaPotReqs.put(Material.COD, 1);
        super.createMenuItem(forgeMenu, pl, 10, Material.POTION, "&3" + tierStr + " Potion of Mana", manaPotReqs,
                "Glass Bottle\nUncut Sapphire\nCod", 8, 60, 10, 0,
                "&eRestores &3" + manaStr + "✸ &eon use\n",
                false, true, false);

        // slaying potion
        LinkedHashMap<Material, Integer> slayPotReqs = new LinkedHashMap<>();
        slayPotReqs.put(Material.GLASS_BOTTLE, 1);
        slayPotReqs.put(Material.NETHER_QUARTZ_ORE, 1);
        slayPotReqs.put(Material.DIAMOND_ORE, 1);
        slayPotReqs.put(Material.TROPICAL_FISH, 1);
        super.createMenuItem(forgeMenu, pl, 11, Material.POTION, "&2" + tierStr + " Potion of Slaying", slayPotReqs,
                "Glass Bottle\nUncut Opal\nUncut Diamond\nTropical Fish", 7, 90, 25, 0,
                "&eIncreases spellʔ and weapon⚔ damage" +
                        "\n&evs. monsters by &f20% &efor &f" + lootingStr + " &eminutes\n",
                false, true, false);

        // looting potion
        LinkedHashMap<Material, Integer> lootPotReqs = new LinkedHashMap<>();
        lootPotReqs.put(Material.GLASS_BOTTLE, 1);
        lootPotReqs.put(Material.GOLDEN_CARROT, 1);
        lootPotReqs.put(Material.PUFFERFISH, 1);
        super.createMenuItem(forgeMenu, pl, 12, Material.POTION, "&d" + tierStr + " Potion of Looting", lootPotReqs,
                "Glass Bottle\nAmbrosia Root\nPufferfish", 4, 600, 40, 0,
                "&eGrants &f20% &echance of &ndouble loot" +
                        "\n&efor &f" + slayingStr + " &eminutes\n",
                false, true, false);

        // sacred fire potion
        LinkedHashMap<Material, Integer> firePotReqs = new LinkedHashMap<>();
        firePotReqs.put(Material.GLASS_BOTTLE, 1);
        firePotReqs.put(Material.NETHER_WART, 1);
        firePotReqs.put(Material.PUFFERFISH, 1);
        super.createMenuItem(forgeMenu, pl, 13, Material.POTION, "&6" + tierStr + " Potion of Sacred Fire", firePotReqs,
                "Glass Bottle\nSacred Flame\nPufferfish", 4, 0, 60, 0,
                "&eYour spells have a &f20% &echance to burn" +
                        "\n&eenemies for an additional &f20 &emagicʔ" +
                        "\n&edamage for &f" + slayingStr + " &eminutes\n",
                false, false, false);
    }

    @Override
    public void produceResult(Player pl, Material material, String dispName,
                              int currentLvl, int amt, int rate, int durability, int someVar) {

        // grab the player's current profession level, progress toward that level
        currentLvl = RunicCoreAPI.getPlayerCache(pl).getProfLevel();

        if (currentLvl < 3) {
            rate = 100;
        } else {
            rate = (40 + currentLvl);
        }

        int failCount = 0;

        for (int i = 0; i < amt; i++) {
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta pMeta = (PotionMeta) potion.getItemMeta();
            Color color;
            String desc;
            if (dispName.toLowerCase().contains("healing")) {
                color = Color.RED;
                desc = "\n&eRestores &c" + someVar + "❤ &eon use";
            } else if (dispName.toLowerCase().contains("mana")) {
                color = Color.AQUA;
                desc = "\n&eRestores &3" + someVar + "✸ &eon use";
            } else if (dispName.toLowerCase().contains("slaying")) {
                color = Color.GREEN;
                desc = "\n&eIncreases spellʔ and weapon⚔ damage" +
                        "\n&evs. monsters by &f20% &efor &f" + someVar + " &eminutes";
            } else if (dispName.toLowerCase().contains("looting")) {
                color = Color.FUCHSIA;
                desc = "\n&eGrants &f20% &echance of &ndouble loot" +
                        "\n&efor &f" + someVar + " &eminutes";
            } else {
                color = Color.ORANGE;
                desc = "\n&eYour spells have a &f20% &echance to burn" +
                        "\n&eenemies for an additional 20 magicʔ" +
                        "\n&edamage for &f" + someVar + " &eminutes";
            }
            Objects.requireNonNull(pMeta).setColor(color);

            pMeta.setDisplayName(ColorUtil.format(dispName));
            ArrayList<String> lore = new ArrayList<>();
            for (String s : desc.split("\n")) {
                lore.add(ColorUtil.format(s));
            }
            lore.add("");
            lore.add(ColorUtil.format("&7Consumable"));
            pMeta.setLore(lore);

            pMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            pMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            pMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            pMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            potion.setItemMeta(pMeta);

            // ----------------------------------------------
            // must be set AFTER meta is set
            if (dispName.toLowerCase().contains("healing")) {
                potion = AttributeUtil.addCustomStat(potion, "potion.healing", someVar);
            } else if (dispName.toLowerCase().contains("mana")) {
                potion = AttributeUtil.addCustomStat(potion, "potion.mana", someVar);
            } else if (dispName.toLowerCase().contains("slaying")) {
                potion = AttributeUtil.addCustomStat(potion, "potion.slaying", someVar);
            } else if (dispName.toLowerCase().contains("looting")) {
                potion = AttributeUtil.addCustomStat(potion, "potion.looting", someVar);
            } else {
                potion = AttributeUtil.addCustomStat(potion, "potion.fire", someVar);
            }
            // ----------------------------------------------

            double chance = ThreadLocalRandom.current().nextDouble(0, 100);
            if (chance <= rate) {
                // check that the player has an open inventory space
                // this method prevents items from stacking if the player crafts 5
                if (pl.getInventory().firstEmpty() != -1) {
                    int firstEmpty = pl.getInventory().firstEmpty();
                    pl.getInventory().setItem(firstEmpty, potion);
                } else {
                    pl.getWorld().dropItem(pl.getLocation(), potion);
                }
            } else {
                failCount = failCount + 1;
            }
        }

        // display fail message
        if (failCount == 0) return;
        pl.sendMessage(ChatColor.RED + "You fail to craft this item. [x" + failCount + "]");
    }
}
