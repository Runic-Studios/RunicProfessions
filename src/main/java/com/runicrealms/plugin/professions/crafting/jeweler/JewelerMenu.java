package com.runicrealms.plugin.professions.crafting.jeweler;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.LoreGenerator;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class JewelerMenu extends Workstation {

    public JewelerMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player pl) {

        // setup the menu
        super.setupWorkstation("&f&l" + pl.getName() + "'s &e&lGemcutting Bench");
        ItemGUI jewelerMenu = getItemGUI();

        //set the visual items
        jewelerMenu.setOption(3, new ItemStack(Material.REDSTONE),
                "&fCut Gems", "&7Create gemstones and enhance armor!", 0, false);

        // set the handler
        jewelerMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the bench menu
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(benchMenu(pl));
                this.setTitle(benchMenu(pl).getName());
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
        this.setItemGUI(jewelerMenu);
    }

    private ItemGUI benchMenu(Player pl) {

        // grab the player's current profession level, progress toward that level
        int currentLvl = RunicCore.getCacheManager().getPlayerCache(pl.getUniqueId()).getProfLevel();

        // ruby
        LinkedHashMap<Material, Integer> cutRubyReqs = new LinkedHashMap<>();
        cutRubyReqs.put(Material.REDSTONE_ORE, 1);
        cutRubyReqs.put(Material.LEATHER, 1);

        LinkedHashMap<Material, Integer> ornateRubyReqs = (LinkedHashMap<Material, Integer>) cutRubyReqs.clone();
        ornateRubyReqs.put(Material.RED_TULIP, 1);

        // sapphire
        LinkedHashMap<Material, Integer> cutSapphireReqs = new LinkedHashMap<>();
        cutSapphireReqs.put(Material.LAPIS_ORE, 1);
        cutSapphireReqs.put(Material.LEATHER, 1);

        LinkedHashMap<Material, Integer> ornateSapphireReqs = (LinkedHashMap<Material, Integer>) cutSapphireReqs.clone();
        ornateSapphireReqs.put(Material.AZURE_BLUET, 1);

        // opal
        LinkedHashMap<Material, Integer> cutOpalReqs = new LinkedHashMap<>();
        cutOpalReqs.put(Material.NETHER_QUARTZ_ORE, 1);
        cutOpalReqs.put(Material.LEATHER, 1);
        cutOpalReqs.put(Material.ORANGE_TULIP, 1);

        // emerald
        LinkedHashMap<Material, Integer> cutEmeraldReqs = new LinkedHashMap<>();
        cutEmeraldReqs.put(Material.EMERALD_ORE, 1);
        cutEmeraldReqs.put(Material.LEATHER, 1);
        cutEmeraldReqs.put(Material.OXEYE_DAISY, 1);

        // diamond
        LinkedHashMap<Material, Integer> cutDiamondReqs = new LinkedHashMap<>();
        cutDiamondReqs.put(Material.DIAMOND_ORE, 1);
        cutDiamondReqs.put(Material.LEATHER, 1);
        cutDiamondReqs.put(Material.BLUE_ORCHID, 1);

        ItemGUI benchMenu = super.craftingMenu(pl, 18);

        benchMenu.setOption(4, new ItemStack(Material.COBBLESTONE_STAIRS), "&eGemcutting Bench",
                "&fClick &7an item to start crafting!"
                        + "\n&fClick &7here to return to the station", 0, false);

        setupItems(benchMenu, pl, currentLvl);

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
                int dummyVar = 0;
                int reqLevel = 0;
                int exp = 0;
                LinkedHashMap<Material, Integer> reqHashMap = new LinkedHashMap<>();

                // ruby (health)
                if (slot == 9) {
                    reqHashMap = cutRubyReqs;
                    exp = 25;
                    if (currentLvl < 30) {
                        dummyVar = 10;
                    } else if (currentLvl < 50) {
                        dummyVar = 20;
                    } else if (currentLvl < 60){
                        dummyVar = 30;
                    } else {
                        dummyVar = 50;
                    }
                    // ruby (health regen)
                } else if (slot == 10) {
                    reqHashMap = ornateRubyReqs;
                    exp = 35;
                    if (currentLvl < 30) {
                        dummyVar = 1;
                    } else if (currentLvl < 50) {
                        dummyVar = 3;
                    } else if (currentLvl < 60) {
                        dummyVar = 5;
                    } else {
                        dummyVar = 7;
                    }
                // sapphire (mana)
                } else if (slot == 11) {
                    reqHashMap = cutSapphireReqs;
                    exp = 50;
                    if (currentLvl < 30) {
                        dummyVar = 15;
                    } else if (currentLvl < 50) {
                        dummyVar = 25;
                    } else if (currentLvl < 60) {
                        dummyVar = 50;
                    } else {
                        dummyVar = 75;
                    }
                    // sapphire (mana regen)
                } else if (slot == 12) {
                    reqHashMap = ornateSapphireReqs;
                    exp = 65;
                    if (currentLvl < 30) {
                        dummyVar = 1;
                    } else if (currentLvl < 50) {
                        dummyVar = 2;
                    } else if (currentLvl < 60) {
                        dummyVar = 3;
                    } else {
                        dummyVar = 4;
                    }
                // opal
                } else if (slot == 13) {
                    reqLevel = 10;
                    reqHashMap = cutOpalReqs;
                    exp = 75;
                    if (currentLvl < 30) {
                        dummyVar = 1;
                    } else if (currentLvl < 50) {
                        dummyVar = 2;
                    } else if (currentLvl < 60) {
                        dummyVar = 3;
                    } else {
                        dummyVar = 4;
                    }
                // emerald
                } else if (slot == 14) {
                    reqLevel = 25;
                    reqHashMap = cutEmeraldReqs;
                    exp = 90;
                    if (currentLvl < 30) {
                        dummyVar = 3;
                    } else if (currentLvl < 50) {
                        dummyVar = 5;
                    } else if (currentLvl < 60) {
                        dummyVar = 7;
                    } else {
                        dummyVar = 9;
                    }
                // diamond
                } else if (slot == 15) {
                    reqLevel = 25;
                    reqHashMap = cutDiamondReqs;
                    exp = 120;
                    if (currentLvl < 30) {
                        dummyVar = 2;
                    } else if (currentLvl < 50) {
                        dummyVar = 4;
                    } else if (currentLvl < 60) {
                        dummyVar = 6;
                    } else {
                        dummyVar = 8;
                    }
                }

                // destroy instance of inventory to prevent bugs
                event.setWillClose(true);
                event.setWillDestroy(true);

                // craft item based on experience and reagent amount
                super.startCrafting(pl, reqHashMap, 1, reqLevel, event.getCurrentItem().getType(),
                        meta.getDisplayName(), currentLvl, exp,
                        ((Damageable) meta).getDamage(), Particle.FIREWORKS_SPARK,
                        Sound.BLOCK_ANVIL_PLACE, Sound.BLOCK_ANVIL_USE, dummyVar, mult);
            }
        });

        return benchMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player pl, int currentLv) {

        String healthStr;
        String healthRegenStr;
        String manaStr;
        String manaRegenStr;
        String healingStr;
        String weaponStr;
        String spellStr;
        if (currentLv < 30) {
            healthStr = "10";
            healthRegenStr = "1";
            manaStr = "15";
            manaRegenStr = "1";
            healingStr = "3";
            weaponStr = "1";
            spellStr = "2";
        } else if (currentLv < 50) {
            healthStr = "20";
            healthRegenStr = "3";
            manaStr = "25";
            manaRegenStr = "2";
            healingStr = "5";
            weaponStr = "2";
            spellStr = "4";
        } else if (currentLv < 60) {
            healthStr = "30";
            healthRegenStr = "5";
            manaStr = "50";
            manaRegenStr = "3";
            healingStr = "7";
            weaponStr = "3";
            spellStr = "6";
        } else {
            healthStr = "50";
            healthRegenStr = "7";
            manaStr = "75";
            manaRegenStr = "4";
            healingStr = "9";
            weaponStr = "4";
            spellStr = "8";
        }

        // ruby (+health)
        LinkedHashMap<Material, Integer> cutRubyReqs = new LinkedHashMap<>();
        cutRubyReqs.put(Material.REDSTONE_ORE, 1);
        cutRubyReqs.put(Material.LEATHER, 1);
        super.createMenuItem(forgeMenu, pl, 9, Material.REDSTONE, "&fCut Ruby", cutRubyReqs,
                "Uncut Ruby\nAnimal Hide", 1, 25, 0, 0, "&c+" + healthStr + "❤ (Health)\n",
                false, true, false);
        // ruby (+health regen)
        LinkedHashMap<Material, Integer> ornateRubyReqs = (LinkedHashMap<Material, Integer>) cutRubyReqs.clone();
        ornateRubyReqs.put(Material.RED_TULIP, 1);
        super.createMenuItem(forgeMenu, pl, 10, Material.REDSTONE, "&fOrnate Ruby", ornateRubyReqs,
                "Uncut Ruby\nAnimal Hide\nHibiscus", 1, 35, 0, 0, "&c+" + healthRegenStr + "❤/s (Health Regen)\n",
                false, true, false);

        // sapphire (+mana)
        LinkedHashMap<Material, Integer> cutSapphireReqs = new LinkedHashMap<>();
        cutSapphireReqs.put(Material.LAPIS_ORE, 1);
        cutSapphireReqs.put(Material.LEATHER, 1);
        super.createMenuItem(forgeMenu, pl, 11, Material.LAPIS_LAZULI, "&fCut Sapphire", cutSapphireReqs,
                "Uncut Sapphire\nAnimal Hide", 1, 50, 0, 0, "&3+" + manaStr + "✸ (Mana)\n",
                false, true, false);
        // sapphire (+mana regen)
        LinkedHashMap<Material, Integer> ornateSapphireReqs = (LinkedHashMap<Material, Integer>) cutSapphireReqs.clone();
        ornateSapphireReqs.put(Material.AZURE_BLUET, 1);
        super.createMenuItem(forgeMenu, pl, 12, Material.LAPIS_LAZULI, "&fOrnate Sapphire", ornateSapphireReqs,
                "Uncut Sapphire\nAnimal Hide\nValerian", 1, 65, 0, 0, "&3+" + manaRegenStr + "✸/s (Mana Regen)\n",
                false, true, false);

        // opal (+dmg)
        LinkedHashMap<Material, Integer> cutOpalReqs = new LinkedHashMap<>();
        cutOpalReqs.put(Material.NETHER_QUARTZ_ORE, 1);
        cutOpalReqs.put(Material.LEATHER, 1);
        cutOpalReqs.put(Material.ORANGE_TULIP, 1);
        super.createMenuItem(forgeMenu, pl, 13, Material.QUARTZ, "&fCut Opal", cutOpalReqs,
                "Uncut Opal\nAnimal Hide\nTurmeric", 1, 75, 10, 0, "&c+" + weaponStr + "⚔ (DMG)\n",
                false, true, false);

        // emerald (+healing)
        LinkedHashMap<Material, Integer> cutEmeraldReqs = new LinkedHashMap<>();
        cutEmeraldReqs.put(Material.EMERALD_ORE, 1);
        cutEmeraldReqs.put(Material.LEATHER, 1);
        cutEmeraldReqs.put(Material.OXEYE_DAISY, 1);
        super.createMenuItem(forgeMenu, pl, 14, Material.EMERALD, "&fCut Emerald", cutEmeraldReqs,
                "Uncut Emerald\nAnimal Hide\nChamomile", 1, 90, 25, 0, "&a+" + healingStr + "✦ (Healing)\n",
                false, true, false);

        // diamond (+spell dmg)
        LinkedHashMap<Material, Integer> cutDiamondReqs = new LinkedHashMap<>();
        cutDiamondReqs.put(Material.DIAMOND_ORE, 1);
        cutDiamondReqs.put(Material.LEATHER, 1);
        cutDiamondReqs.put(Material.BLUE_ORCHID, 1);
        super.createMenuItem(forgeMenu, pl, 15, Material.DIAMOND, "&fCut Diamond", cutDiamondReqs,
                "Uncut Diamond\nAnimal Hide\nWintercress", 1, 120, 25, 0, "&3+" + spellStr + "ʔ (Magic)\n",
                false, true, false);
    }

    @Override
    public void produceResult(Player pl, Material material, String dispName,
                              int currentLvl, int amt, int rate, int durability, int someVar) {

        // check that the player has an open inventory space
        int failCount = 0;
        for (int i = 0; i < amt; i++) {

            // build our item
            ItemStack craftedItem = new ItemStack(material);

            // roll chance for each item
            double chance = ThreadLocalRandom.current().nextDouble(0, 100);

            // tell the game that this is a gemstone
            craftedItem = AttributeUtil.addCustomStat
                    (craftedItem, "custom.isGemstone", "true");

            craftedItem = addGemStat(GemEnum.valueOf
                    (ChatColor.stripColor(dispName.toUpperCase().replace(" ", "_"))), craftedItem, someVar);

            LoreGenerator.generateItemLore(craftedItem, ChatColor.WHITE, dispName,
                    "\n" + ChatColor.DARK_GRAY + "Use this on an item", false, "");

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

    private ItemStack addGemStat(GemEnum gemEnum, ItemStack craftedItem, int dummyVar) {
        craftedItem = AttributeUtil.addCustomStat(craftedItem, gemEnum.getAttributeName(), dummyVar);
        return craftedItem;
    }
}
