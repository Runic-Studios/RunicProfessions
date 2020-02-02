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
        int currentLvl = RunicCore.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.level");

        // ruby
        LinkedHashMap<Material, Integer> cutRubyReqs = new LinkedHashMap<>();
        cutRubyReqs.put(Material.REDSTONE_ORE, 999);

        // sapphire
        LinkedHashMap<Material, Integer> cutSapphireReqs = new LinkedHashMap<>();
        cutSapphireReqs.put(Material.LAPIS_ORE, 999);

        // emerald
        LinkedHashMap<Material, Integer> cutEmeraldReqs = new LinkedHashMap<>();
        cutEmeraldReqs.put(Material.EMERALD_ORE, 999);

        // opal
        LinkedHashMap<Material, Integer> cutOpalReqs = new LinkedHashMap<>();
        cutOpalReqs.put(Material.NETHER_QUARTZ_ORE, 999);

        // diamond
        LinkedHashMap<Material, Integer> cutDiamondReqs = new LinkedHashMap<>();
        cutDiamondReqs.put(Material.DIAMOND_ORE, 999);

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

                // ruby
                if (slot == 9) {
                    reqHashMap = cutRubyReqs;
                    exp = 30;
                    if (currentLvl < 30) {
                        dummyVar = 10;
                    } else if (currentLvl < 50) {
                        dummyVar = 20;
                    } else {
                        dummyVar = 30;
                    }
                // sapphire
                } else if (slot == 10) {
                    reqHashMap = cutSapphireReqs;
                    exp = 60;
                    if (currentLvl < 30) {
                        dummyVar = 15;
                    } else if (currentLvl < 50) {
                        dummyVar = 25;
                    } else {
                        dummyVar = 35;
                    }
                // opal
                } else if (slot == 11) {
                    reqLevel = 10;
                    reqHashMap = cutOpalReqs;
                    exp = 90;
                    if (currentLvl < 30) {
                        dummyVar = 1;
                    } else if (currentLvl < 50) {
                        dummyVar = 2;
                    } else {
                        dummyVar = 3;
                    }
                // emerald
                } else if (slot == 12) {
                    reqLevel = 25;
                    reqHashMap = cutEmeraldReqs;
                    exp = 120;
                    if (currentLvl < 30) {
                        dummyVar = 2;
                    } else if (currentLvl < 50) {
                        dummyVar = 4;
                    } else {
                        dummyVar = 6;
                    }
                // diamond
                } else if (slot == 13) {
                    reqLevel = 25;
                    reqHashMap = cutDiamondReqs;
                    exp = 150;
                    if (currentLvl < 30) {
                        dummyVar = 2;
                    } else if (currentLvl < 50) {
                        dummyVar = 4;
                    } else {
                        dummyVar = 6;
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
        String manaStr;
        String healingStr;
        String weaponStr;
        String spellStr;
        if (currentLv < 30) {
            healthStr = "10";
            manaStr = "15";
            healingStr = "2";
            weaponStr = "1";
            spellStr = "2";
        } else if (currentLv < 50) {
            healthStr = "20";
            manaStr = "25";
            healingStr = "4";
            weaponStr = "2";
            spellStr = "4";
        } else {
            healthStr = "30";
            manaStr = "35";
            healingStr = "6";
            weaponStr = "3";
            spellStr = "6";
        }

        // ruby (+health)
        LinkedHashMap<Material, Integer> cutRubyReqs = new LinkedHashMap<>();
        cutRubyReqs.put(Material.REDSTONE_ORE, 999);
        super.createMenuItem(forgeMenu, pl, 9, Material.REDSTONE, "&fCut Ruby", cutRubyReqs,
                "Uncut Ruby", 1, 30, 0, 0, "&c+" + healthStr + "❤ (Health)\n",
                false, true, false);

        // sapphire (+mana)
        LinkedHashMap<Material, Integer> cutSapphireReqs = new LinkedHashMap<>();
        cutSapphireReqs.put(Material.LAPIS_ORE, 999);
        super.createMenuItem(forgeMenu, pl, 10, Material.LAPIS_LAZULI, "&fCut Sapphire", cutSapphireReqs,
                "Uncut Sapphire", 1, 60, 0, 0, "&3+" + manaStr + "✸ (Mana)\n",
                false, true, false);

        // opal (+dmg)
        LinkedHashMap<Material, Integer> cutOpalReqs = new LinkedHashMap<>();
        cutOpalReqs.put(Material.NETHER_QUARTZ_ORE, 999);
        super.createMenuItem(forgeMenu, pl, 11, Material.QUARTZ, "&fCut Opal", cutOpalReqs,
                "Uncut Opal", 1, 90, 10, 0, "&c+" + weaponStr + "⚔ (DMG)\n",
                false, true, false);

        // emerald (+healing)
        LinkedHashMap<Material, Integer> cutEmeraldReqs = new LinkedHashMap<>();
        cutEmeraldReqs.put(Material.EMERALD_ORE, 999);
        super.createMenuItem(forgeMenu, pl, 12, Material.EMERALD, "&fCut Emerald", cutEmeraldReqs,
                "Uncut Emerald", 1, 120, 25, 0, "&a+" + healingStr + "✦ (Heal)\n",
                false, true, false);

        // diamond (+spell dmg)
        LinkedHashMap<Material, Integer> cutDiamondReqs = new LinkedHashMap<>();
        cutDiamondReqs.put(Material.DIAMOND_ORE, 999);
        super.createMenuItem(forgeMenu, pl, 13, Material.DIAMOND, "&fCut Diamond", cutDiamondReqs,
                "Uncut Diamond", 1, 150, 25, 0, "&3+" + spellStr + "ʔ (Magic)\n",
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

            craftedItem = addGemStat(material, craftedItem, someVar);

            LoreGenerator.generateItemLore(craftedItem, ChatColor.WHITE, dispName,
                    "\n" + ChatColor.DARK_GRAY + "Use this on an item", false);

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

    private ItemStack addGemStat(Material gemType, ItemStack craftedItem, int dummyVar) {
        switch (gemType) {
            case REDSTONE:
                craftedItem = AttributeUtil.addCustomStat(craftedItem, "custom.maxHealth", dummyVar);
                break;
            case LAPIS_LAZULI:
                craftedItem = AttributeUtil.addCustomStat(craftedItem, "custom.manaBoost", dummyVar);
                break;
            case EMERALD:
                craftedItem = AttributeUtil.addCustomStat(craftedItem, "custom.healingBoost", dummyVar);
                break;
            case QUARTZ:
                craftedItem = AttributeUtil.addCustomStat(craftedItem, "custom.attackDamage", dummyVar);
                break;
            case DIAMOND:
                craftedItem = AttributeUtil.addCustomStat(craftedItem, "custom.magicDamage", dummyVar);
                break;
        }
        return craftedItem;
    }
}
