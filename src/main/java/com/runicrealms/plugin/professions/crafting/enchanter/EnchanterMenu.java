package com.runicrealms.plugin.professions.crafting.enchanter;

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

public class EnchanterMenu extends Workstation {

    public EnchanterMenu(Player pl) {
        setupWorkstation(pl);
    }

    @Override
    public void setupWorkstation(Player pl) {

        // setup the menu
        super.setupWorkstation("&f&l" + pl.getName() + "'s &e&lEnchanting Table");
        ItemGUI jewelerMenu = getItemGUI();

        //set the visual items
        jewelerMenu.setOption(3, new ItemStack(Material.PURPLE_DYE),
                "&fEnchant Scrolls", "&7Create scrolls and enchant items!", 0, false);

        // set the handler
        jewelerMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the bench menu
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(tableMenu(pl));
                this.setTitle(tableMenu(pl).getName());
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

    private ItemGUI tableMenu(Player pl) {

        // grab the player's current profession level, progress toward that level
        int currentLvl = RunicCore.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.level");

        // paper
        LinkedHashMap<Material, Integer> paperReqs = new LinkedHashMap<>();
        paperReqs.put(Material.STRING, 2);

        // ancient powder
        LinkedHashMap<Material, Integer> powderReqs = new LinkedHashMap<>();
        powderReqs.put(Material.BIRCH_LOG, 1);

        // magic powder
        LinkedHashMap<Material, Integer> magicPowderReqs = new LinkedHashMap<>();
        magicPowderReqs.put(Material.DARK_OAK_LOG, 1);

        // teleport scrolls
        LinkedHashMap<Material, Integer> teleportScrollReqs = new LinkedHashMap<>();
        teleportScrollReqs.put(Material.PAPER, 1);
        teleportScrollReqs.put(Material.GUNPOWDER, 1);

        // enchant scrolls
        LinkedHashMap<Material, Integer> enchantScrollReqs = new LinkedHashMap<>();
        enchantScrollReqs.put(Material.PAPER, 1);
        enchantScrollReqs.put(Material.BLAZE_POWDER, 1);

        ItemGUI benchMenu = super.craftingMenu(pl, 27);

        benchMenu.setOption(4, new ItemStack(Material.ENCHANTING_TABLE), "&eEnchanting Table",
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
                int dummyVar;
                int reqLevel = 0;
                int exp = 0;
                LinkedHashMap<Material, Integer> reqHashMap = new LinkedHashMap<>();

                if (currentLvl < 30) {
                    dummyVar = 1;
                } else if (currentLvl < 50) {
                    dummyVar = 2;
                } else {
                    dummyVar = 3;
                }

                // paper
                if (slot == 9) {
                    reqHashMap = paperReqs;
                    exp = 15;
                    // ancient powder
                } else if (slot == 10) {
                    reqHashMap = powderReqs;
                    exp = 10;
                    // magic powder
                } else if (slot == 11) {
                    reqHashMap = magicPowderReqs;
                    exp = 10;
                    // azana scroll
                } else if (slot == 12) {
                    reqHashMap = teleportScrollReqs;
                    exp = 40;
                    // crit scroll
                } else if (slot == 13) {
                    reqLevel = 10;
                    reqHashMap = enchantScrollReqs;
                    exp = 80;
                    // wintervale scroll
                } else if (slot == 14) {
                    reqLevel = 20;
                    reqHashMap = teleportScrollReqs;
                    exp = 40;
                } else if (slot == 15) {
                    // dodge scroll
                    reqLevel = 20;
                    reqHashMap = enchantScrollReqs;
                    exp = 80;
                    // zenyth scroll
                } else if (slot == 16) {
                    reqLevel = 40;
                    reqHashMap = teleportScrollReqs;
                    exp = 40;
                } else if (slot == 17) {
                    // thorns scroll
                    reqLevel = 40;
                    reqHashMap = enchantScrollReqs;
                    exp = 80;
                } else if (slot == 18) {
                    // frost's end scroll
                    reqLevel = 60;
                    reqHashMap = teleportScrollReqs;
                }

                // destroy instance of inventory to prevent bugs
                event.setWillClose(true);
                event.setWillDestroy(true);

                // craft item based on experience and reagent amount
                super.startCrafting(pl, reqHashMap, 1, reqLevel, event.getCurrentItem().getType(),
                        meta.getDisplayName(), currentLvl, exp,
                        ((Damageable) meta).getDamage(), Particle.FIREWORKS_SPARK,
                        Sound.BLOCK_ENCHANTMENT_TABLE_USE, Sound.ENTITY_PLAYER_LEVELUP, dummyVar, mult);
            }
        });

        return benchMenu;
    }

    private void setupItems(ItemGUI tableMenu, Player pl, int currentLv) {

        int critAmt = 1;
        int dodgeAmt = 1;
        int thornsAmt = 1;
        if (currentLv >= 30 && currentLv < 50) {
            critAmt = 2;
            dodgeAmt = 2;
            thornsAmt = 2;
        } else if (currentLv >= 50) {
            critAmt = 3;
            dodgeAmt = 3;
            thornsAmt = 3;
        }

        // paper
        LinkedHashMap<Material, Integer> paperReqs = new LinkedHashMap<>();
        paperReqs.put(Material.STRING, 999);
        super.createMenuItem(tableMenu, pl, 9, Material.PAPER, "&fPaper", paperReqs,
                "String", 2, 15, 0, 0, "",
                true, false, false);

        // ancient powder
        LinkedHashMap<Material, Integer> powderReqs = new LinkedHashMap<>();
        powderReqs.put(Material.BIRCH_LOG, 999);
        super.createMenuItem(tableMenu, pl, 10, Material.GUNPOWDER, "&fAncient Powder", powderReqs,
                "Birch Log", 1, 10, 0, 0, "",
                true, false, false);

        // magic powder
        LinkedHashMap<Material, Integer> magicPowderReqs = new LinkedHashMap<>();
        magicPowderReqs.put(Material.DARK_OAK_LOG, 999);
        super.createMenuItem(tableMenu, pl, 11, Material.BLAZE_POWDER, "&fMagic Powder", magicPowderReqs,
                "Dark Oak Log", 1, 10, 0, 0, "",
                true, false, false);

        // azana scroll
        LinkedHashMap<Material, Integer> teleportScrollReqs = new LinkedHashMap<>();
        teleportScrollReqs.put(Material.PAPER, 1);
        teleportScrollReqs.put(Material.GUNPOWDER, 1);
        super.createMenuItem(tableMenu, pl, 12, Material.PURPLE_DYE, "&fTeleport Scroll: Azana", teleportScrollReqs,
                "Paper\nAncient Powder", 2, 40, 0, 0, "&eTeleport to Azana!\n",
                false, false, false);

        // crit scroll
        LinkedHashMap<Material, Integer> enchantScrollReqs = new LinkedHashMap<>();
        enchantScrollReqs.put(Material.PAPER, 1);
        enchantScrollReqs.put(Material.BLAZE_POWDER, 1);
        super.createMenuItem(tableMenu, pl, 13, Material.PURPLE_DYE, "&fEnchant Scroll: Crit", enchantScrollReqs,
                "Paper\nMagic Powder", 999, 80, 10, 0, "&a+" + critAmt + "% Crit Chance\n",
                false, true, false);

        // wintervale scroll
        // todo: req. combat lv.
        super.createMenuItem(tableMenu, pl, 14, Material.PURPLE_DYE, "&fTeleport Scroll: Wintervale", teleportScrollReqs,
                "Paper\nAncient Powder", 1, 40, 20, 0, "&eTeleport to Wintervale!\n",
                false, false, false);

        // dodge scroll
        super.createMenuItem(tableMenu, pl, 15, Material.PURPLE_DYE, "&fEnchant Scroll: Dodge", enchantScrollReqs,
                "Paper\nMagic Powder", 1, 80, 20, 0, "&a+" + dodgeAmt + "% Dodge Chance\n",
                false, true, false);

        // zenyth scroll
        // todo: req. level
        super.createMenuItem(tableMenu, pl, 16, Material.PURPLE_DYE, "&fTeleport Scroll: Zenyth", teleportScrollReqs,
                "Paper\nAncient Powder", 1, 40, 40, 0, "&eTeleport to Zenyth!\n",
                false, false, false);

        // thorns scroll
        super.createMenuItem(tableMenu, pl, 17, Material.PURPLE_DYE, "&fEnchant Scroll: Thorns", enchantScrollReqs,
                "Paper\nMagic Powder", 1, 80, 40, 0, "&a+" + thornsAmt + "% Thorns Chance\n",
                false, true, false);

        // todo: req. level
        // frosts end scroll
        super.createMenuItem(tableMenu, pl, 18, Material.PURPLE_DYE, "&fTeleport Scroll: Frost's End", teleportScrollReqs,
                "Paper\nAncient Powder", 1, 0, 60, 0, "&eTeleport to Frost's End!\n",
                false, false, false);
    }

    // todo: create static item stacks
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

    private static ItemStack teleportScroll(String location, int reqLv) {
        return new ItemStack(Material.STICK);
    }

    private static ItemStack enchantScroll(String enchant, int reqLv, int dummyVar) {
        return new ItemStack(Material.STICK);
    }
}
