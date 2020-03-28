package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.shops.Shop;
import com.runicrealms.plugin.professions.Workstation;
import com.runicrealms.plugin.professions.crafting.enchanter.EnchantEnum;
import com.runicrealms.plugin.professions.crafting.enchanter.EnchantScroll;
import com.runicrealms.plugin.utilities.ColorUtil;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.Objects;

public class HunterShop extends Shop {

    private static final int PRICE_POTION = 5;
    private static final int PRICE_BOAT = 2;
    private static final int PRICE_ORB = 1;
    private static final int PRICE_TRACKING = 50;
    private static final int PRICE_ENCHANT = 200;
    private static final int PRICE_COMPASS = 1000;

    HunterShop(Player pl) {
        setupShop(pl);
    }

    @Override
    public void setupShop(Player pl) {

        // name the menu
        super.setupShop("&eHunter Shop", 18);

        ItemGUI shopMenu = getItemGUI();

        shopMenu.setOption(4, new ItemStack(Material.BOW),
                "&eHunter Shop",
                "&7You have &6&l" + HunterTask.getTotalPoints(pl) + " hunter points!",
                0, false);
        shopMenu.setOption(9, Workstation.potionItem(Color.BLACK, "", ""),
                "&fShadowmeld Potion",
                "\n&eAfter standing still for 5s, you turn invisible!\n\n&7Price: &6&l" + PRICE_POTION + " points",
                0, false);
        shopMenu.setOption(10, new ItemStack(Material.OAK_BOAT),
                "&fBoat",
                "\n&7Price: &6&l" + PRICE_BOAT + " points", 0, false);
        shopMenu.setOption(11, scryingOrb(),
                "&fScrying Orb",
                "\n&eLearn the stats of a player!\n\n&7Price: &6&l" + PRICE_ORB + " points",
                0, false);
        shopMenu.setOption(12, new ItemStack(Material.PURPLE_DYE),
                "&fTracking Scroll",
                "\n&eLearn the location of a player!\n\n&7Price: &6&l" + PRICE_TRACKING + " points", 0, false);
        shopMenu.setOption(13, new ItemStack(Material.PURPLE_DYE),
                "&fSpeed Enchant",
                "\n&eEnchant your armor with +3% speed!\n\n&7Price: &6&l" + PRICE_ENCHANT + " points", 0, false);
        shopMenu.setOption(14, new ItemStack(Material.COMPASS),
                "&6Tracking Compass",
                "\n&eLearn the location of a player! (Reusable)\n\n&7Price: &6&l" + PRICE_COMPASS + " points", 0, false);

        // set the handler
        shopMenu.setHandler(event -> {

            if (event.getSlot() < 15) {

                event.setWillClose(false);
                event.setWillDestroy(false);

                switch (event.getSlot()) {
                    case 9:
                        if (attemptToTakeGold(pl, PRICE_POTION)) {
                            pl.getInventory().addItem(shadowmeldPotion());
                        }
                        break;
                    case 10:
                        if (attemptToTakeGold(pl, PRICE_BOAT)){
                            pl.getInventory().addItem(new ItemStack(Material.OAK_BOAT));
                        }
                        break;
                    case 11:
                        if (attemptToTakeGold(pl, PRICE_ORB)) {
                            pl.getInventory().addItem(scryingOrb());
                        }
                        break;
                    case 12:
                        if (attemptToTakeGold(pl, PRICE_TRACKING)) {
                            pl.getInventory().addItem(trackingScroll());
                        }
                        break;
                    case 13:
                        if (attemptToTakeGold(pl, PRICE_ENCHANT)) {
                            pl.getInventory().addItem(new EnchantScroll(EnchantEnum.SPEED, 3, 30).getItem());
                        }
                        break;
                    case 14:
                        if (attemptToTakeGold(pl, PRICE_COMPASS)) {
                            pl.getInventory().addItem(trackingCompass());
                        }
                        break;
                }

                // close shop
            } else if (event.getSlot() == 17) {
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        });

        // update our internal menu
        this.setItemGUI(shopMenu);
    }

    static ItemStack shadowmeldPotion() {

        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta pMeta = (PotionMeta) potion.getItemMeta();
        String desc = "\n&eAfter standing still for 5 seconds," +
                "\n&eyou turn invisible for 30 seconds!" +
                "\n&eDealing damage ends the effect" +
                "\n&eearly.";

        Objects.requireNonNull(pMeta).setColor(Color.BLACK);

        pMeta.setDisplayName(ColorUtil.format("&fShadowmeld Potion"));
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
        potion = AttributeUtil.addCustomStat(potion, "potion.shadowmeld", "true");
        return potion;
    }

    static ItemStack scryingOrb() {
        MythicItem mi = MythicMobs.inst().getItemManager().getItem("ScryingOrb").get();
        AbstractItemStack abstractItemStack = mi.generateItemStack(1);
        ItemStack orb = BukkitAdapter.adapt(abstractItemStack);
        return orb;
    }

    static ItemStack trackingScroll() {
        ItemStack trackingScroll = new ItemStack(Material.PURPLE_DYE);
        ItemMeta meta = trackingScroll.getItemMeta();
        String desc = "\n&6&lRIGHT CLICK &aTrack" +
                "\n&7Specify a player and learn their location!";
        meta.setDisplayName(ColorUtil.format("&fTracking Scroll"));
        ArrayList<String> lore = new ArrayList<>();
        for (String s : desc.split("\n")) {
            lore.add(ColorUtil.format(s));
        }
        lore.add("");
        lore.add(ColorUtil.format("&7Consumable"));
        meta.setLore(lore);
        trackingScroll.setItemMeta(meta);
        return trackingScroll;
    }

    static ItemStack trackingCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        String desc = "\n&6&lRIGHT CLICK &aTrack" +
                "\n&7Specify a player and learn their location!";
        meta.setDisplayName(ColorUtil.format("&6Tracking Compass"));
        ArrayList<String> lore = new ArrayList<>();
        for (String s : desc.split("\n")) {
            lore.add(ColorUtil.format(s));
        }
        lore.add("");
        lore.add(ColorUtil.format("&8Soulbound"));
        meta.setLore(lore);
        compass.setItemMeta(meta);
        return compass;
    }

    /**
     * This method checks if player has required hunter points amount/inv space,
     * then takes it and proceeds
     */
    private boolean attemptToTakeGold(Player pl, int price) {

        // check that the player has an open inventory space
        if (pl.getInventory().firstEmpty() == -1) {
            pl.playSound(pl.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
            pl.sendMessage(ChatColor.RED + "You don't have any inventory space!");
            return false;
        }

        int currentPoints = HunterTask.getTotalPoints(pl);

        if (currentPoints < price) {
            pl.playSound(pl.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
            pl.sendMessage(ChatColor.RED + "You don't have enough hunter points!");
            return false;
        }

        RunicProfessions.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_points", HunterTask.getTotalPoints(pl)-price);
        RunicProfessions.getInstance().saveConfig();
        RunicProfessions.getInstance().reloadConfig();

        pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
        return true;
    }
}
