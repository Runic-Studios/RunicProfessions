package com.runicrealms.plugin.professions.crafting.enchanter;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;

public class EnchantingTableMenu extends Workstation {

    private static final int TABLE_MENU_SIZE = 54;

    public EnchantingTableMenu(Player pl) {
        setupWorkstation(pl);
    }

    public static ItemStack partySummonScroll() {
        ItemStack item = new ItemStack(Material.PURPLE_DYE);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(ChatColor.WHITE + "Party Summon Scroll");
        meta.setLore(Arrays.asList(
                "",
                ChatColor.GOLD + "" + ChatColor.BOLD + "RIGHT CLICK " + ChatColor.GREEN + "Summon Party",
                ChatColor.GRAY + "Summon your party to you!",
                "",
                ChatColor.WHITE + "Crafted",
                ChatColor.GRAY + "Consumable"));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void setupWorkstation(Player player) {

        // setup the menu
        super.setupWorkstation("&f&l" + player.getName() + "'s &e&lEnchanting Table");
        ItemGUI enchanterMenu = getItemGUI();

        //set the visual items
        enchanterMenu.setOption(3, new ItemStack(Material.PURPLE_DYE),
                "&fTeleportation Scrolls & Rituals", "&7Create teleportation scrolls and craft rituals!", 0, false);

        // set the handler
        enchanterMenu.setHandler(event -> {

            if (event.getSlot() == 3) {

                // open the bench menu
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                this.setItemGUI(tableMenu(player));
                this.setTitle(tableMenu(player).getName());
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
        this.setItemGUI(enchanterMenu);
    }

    private ItemGUI tableMenu(Player pl) {

        // grab the player's current profession level, progress toward that level
        int currentLvl = RunicCoreAPI.getPlayerCache(pl).getProfLevel();

        // paper
        LinkedHashMap<Material, Integer> paperReqs = new LinkedHashMap<>();
        paperReqs.put(Material.STRING, 2);

        // ancient powder
        LinkedHashMap<Material, Integer> powderReqs = new LinkedHashMap<>();
        powderReqs.put(Material.BIRCH_LOG, 1);

        // magic powder
        LinkedHashMap<Material, Integer> magicPowderReqs = new LinkedHashMap<>();
        magicPowderReqs.put(Material.DARK_OAK_LOG, 1);

        // azana scroll
        LinkedHashMap<Material, Integer> azanaScroll = new LinkedHashMap<>();
        azanaScroll.put(Material.PAPER, 1);
        azanaScroll.put(Material.GUNPOWDER, 1);
        azanaScroll.put(Material.REDSTONE_ORE, 1);

        // vale scroll
        LinkedHashMap<Material, Integer> wintervaleScroll = new LinkedHashMap<>();
        wintervaleScroll.put(Material.PAPER, 1);
        wintervaleScroll.put(Material.GUNPOWDER, 1);
        wintervaleScroll.put(Material.DIAMOND_ORE, 1);

        // zenyth scroll
        LinkedHashMap<Material, Integer> zenythScroll = new LinkedHashMap<>();
        zenythScroll.put(Material.PAPER, 1);
        zenythScroll.put(Material.GUNPOWDER, 1);
        zenythScroll.put(Material.NETHER_QUARTZ_ORE, 1);

        // party scroll
        LinkedHashMap<Material, Integer> partyScroll = new LinkedHashMap<>();
        partyScroll.put(Material.PAPER, 1);
        partyScroll.put(Material.GUNPOWDER, 1);
        partyScroll.put(Material.PINK_TULIP, 1);

        // frost's end scroll
        LinkedHashMap<Material, Integer> frostsEndScroll = new LinkedHashMap<>();
        frostsEndScroll.put(Material.PAPER, 1);
        frostsEndScroll.put(Material.GUNPOWDER, 1);
        frostsEndScroll.put(Material.GOLDEN_CARROT, 1);

        ItemGUI benchMenu = super.craftingMenu(pl, TABLE_MENU_SIZE);

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
                int reqLevel = 0;
                int exp = 0;
                LinkedHashMap<Material, Integer> reqHashMap = new LinkedHashMap<>();

                if (slot == 9) {
                    // paper
                    reqHashMap = paperReqs;
                    exp = 15;
                } else if (slot == 10) {
                    // ancient powder
                    reqHashMap = powderReqs;
                    exp = 10;
                } else if (slot == 11) {
                    // magic powder
                    reqHashMap = magicPowderReqs;
                    exp = 10;
                } else if (slot == 12) {
                    // azana scroll
                    reqHashMap = azanaScroll;
                    exp = 40;
                } else if (slot == 13) {
                    // wintervale scroll
                    reqLevel = 20;
                    reqHashMap = wintervaleScroll;
                    exp = 40;
                } else if (slot == 14) {
                    // zenyth scroll
                    reqLevel = 40;
                    reqHashMap = zenythScroll;
                    exp = 40;
                } else if (slot == 15) {
                    reqLevel = 50;
                    exp = 100;
                    reqHashMap = partyScroll;
                } else if (slot == 16) {
                    // frost's end scroll
                    reqLevel = 60;
                    reqHashMap = frostsEndScroll;
                }

                // destroy instance of inventory to prevent bugs
                event.setWillClose(true);
                event.setWillDestroy(true);

                // craft item based on experience and reagent amount
//                super.startCrafting(pl, null, 1, reqLevel, event.getCurrentItem().getType(),
//                        currentLvl, exp,
//                        ((Damageable) meta).getDamage(), Particle.SPELL_WITCH,
//                        Sound.BLOCK_ENCHANTMENT_TABLE_USE, Sound.ENTITY_PLAYER_LEVELUP, slot, mult, false);
            }
        });

        return benchMenu;
    }

    private void setupItems(ItemGUI tableMenu, Player pl, int currentLv) {
        // paper
        LinkedHashMap<Material, Integer> paperReqs = new LinkedHashMap<>();
        paperReqs.put(Material.STRING, 999);
//        super.createMenuItem(tableMenu, pl, 9, Material.PAPER, "&fPaper", paperReqs,
//                "String", 2, 15, 0, 0, "",
//                true, false, false);
//
//        // ancient powder
//        LinkedHashMap<Material, Integer> powderReqs = new LinkedHashMap<>();
//        powderReqs.put(Material.BIRCH_LOG, 999);
//        super.createMenuItem(tableMenu, pl, 10, Material.GUNPOWDER, "&fAncient Powder", powderReqs,
//                "Elder Log", 1, 10, 0, 0, "",
//                true, false, false);
//
//        // magic powder
//        LinkedHashMap<Material, Integer> magicPowderReqs = new LinkedHashMap<>();
//        magicPowderReqs.put(Material.DARK_OAK_LOG, 999);
//        super.createMenuItem(tableMenu, pl, 11, Material.BLAZE_POWDER, "&fMagic Powder", magicPowderReqs,
//                "Dark Oak Log", 1, 10, 0, 0, "",
//                true, false, false);
//
//        // azana scroll
//        LinkedHashMap<Material, Integer> azanaScrollReqs = new LinkedHashMap<>();
//        azanaScrollReqs.put(Material.PAPER, 1);
//        azanaScrollReqs.put(Material.GUNPOWDER, 1);
//        azanaScrollReqs.put(Material.REDSTONE_ORE, 1);
//        super.createMenuItem(tableMenu, pl, 12, Material.PURPLE_DYE, "&fTeleport Scroll: Azana", azanaScrollReqs,
//                "Paper\nAncient Powder\nUncut Ruby", 2, 40, 0, 0, "&eTeleport to Azana!\n",
//                );
//
//        // wintervale scroll
//        LinkedHashMap<Material, Integer> valeScrollReqs = new LinkedHashMap<>();
//        valeScrollReqs.put(Material.PAPER, 1);
//        valeScrollReqs.put(Material.GUNPOWDER, 1);
//        valeScrollReqs.put(Material.DIAMOND_ORE, 1);
//        super.createMenuItem(tableMenu, pl, 13, Material.PURPLE_DYE, "&fTeleport Scroll: Wintervale", valeScrollReqs,
//                "Paper\nAncient Powder\nUncut Diamond", 1, 40, 20, 0, "&eTeleport to Wintervale!\n",
//                );
//
//        // zenyth scroll
//        LinkedHashMap<Material, Integer> zenythScrollReqs = new LinkedHashMap<>();
//        zenythScrollReqs.put(Material.PAPER, 1);
//        zenythScrollReqs.put(Material.GUNPOWDER, 1);
//        zenythScrollReqs.put(Material.NETHER_QUARTZ_ORE, 1);
//        super.createMenuItem(tableMenu, pl, 14, Material.PURPLE_DYE, "&fTeleport Scroll: Zenyth", zenythScrollReqs,
//                "Paper\nAncient Powder\nUncut Opal", 1, 40, 40, 0, "&eTeleport to Zenyth!\n",
//                );
//
//        // party scroll
//        LinkedHashMap<Material, Integer> partyScrollReqs = new LinkedHashMap<>();
//        partyScrollReqs.put(Material.PAPER, 1);
//        partyScrollReqs.put(Material.GUNPOWDER, 1);
//        partyScrollReqs.put(Material.PINK_TULIP, 1);
//        super.createMenuItem(tableMenu, pl, 15, Material.PURPLE_DYE, "&fParty Summon Scroll", partyScrollReqs,
//                "Paper\nAncient Powder\nLavender", 1, 100, 50, 0, "&eSummon your party to you!\n",
//                );
//
//        // frosts end scroll
//        LinkedHashMap<Material, Integer> frostScrollReqs = new LinkedHashMap<>();
//        frostScrollReqs.put(Material.PAPER, 1);
//        frostScrollReqs.put(Material.GUNPOWDER, 1);
//        frostScrollReqs.put(Material.GOLDEN_CARROT, 1);
//        super.createMenuItem(tableMenu, pl, 16, Material.PURPLE_DYE, "&fTeleport Scroll: Frost's End", frostScrollReqs,
//                "Paper\nAncient Powder\nAmbrosia Root", 1, 0, 60, 0, "&eTeleport to Frost's End!\n",
//                );
    }

    private ItemStack determineItem(int slot) {
        ItemStack item = new ItemStack(Material.STICK);
//        int percent;
//        int profLv = RunicCoreAPI.getPlayerCache(pl).getProfLevel();
//        if (profLv < 30) {
//            percent = 1;
//        } else if (profLv < 50) {
//            percent = 2;
//        } else {
//            percent = 3;
//        }
//        switch (slot) {
//            case 9:
//            case 10:
//            case 11:
//                item = new ItemStack(material);
//                ItemMeta meta = item.getItemMeta();
//                ((Damageable) Objects.requireNonNull(meta)).setDamage(durability);
//
//                ArrayList<String> lore = new ArrayList<>();
//
//                lore.add(ChatColor.GRAY + "Crafting Reagent");
//                meta.setLore(lore);
//                meta.setDisplayName(ChatColor.WHITE + dispName);
//                item.setItemMeta(meta);
//                break;
//            case 12:
//                item = new TeleportScroll(TeleportEnum.AZANA, 0).getItem(); // azana
//                break;
//            case 14:
//                item = new TeleportScroll(TeleportEnum.WINTERVALE, 20).getItem(); // wintervale
//                break;
//            case 16:
//                item = new TeleportScroll(TeleportEnum.ZENYTH, 40).getItem(); // zenyth
//                break;
//            case 18:
//                item = partySummonScroll();
//                break;
//            case 19:
//                item = new TeleportScroll(TeleportEnum.FROSTS_END, 60).getItem(); // frost's end
//                break;
//        }
        return item;
    }
}
