package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.shops.Shop;
import com.runicrealms.plugin.professions.Workstation;
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
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.Objects;

public class HunterShop extends Shop {

    private static final int PRICE_POTION = 5;
    private static final int PRICE_BOAT = 2;
    private static final int PRICE_ORB = 15;
    private static final int PRICE_SCROLL = 50;
    private static final int PRICE_ENCHANT = 200;
    private static final int PRICE_COMPASS = 1000;

    public HunterShop(Player pl) {
        setupShop(pl);
    }

    // todo: display prices
    @Override
    public void setupShop(Player pl) {

        // name the menu
        super.setupShop("&eHunter Shop", false);
        ItemGUI shopMenu = getItemGUI();

        shopMenu.setOption(0, Workstation.potionItem(Color.BLACK, "", ""),
                "&fShadowmeld Potion",
                "\n&eAfter standing still for 5s, you turn invisible!\n\n&7Consumable", 0, false);
        shopMenu.setOption(1, new ItemStack(Material.OAK_BOAT),
                "&fBoat",
                "", 0, false);
        shopMenu.setOption(2, scryingOrb(),
                "&fScrying Orb",
                "\n&eLearn the stats of a player!\n\n&7Consumable", 0, false);
        shopMenu.setOption(3, new ItemStack(Material.PAPER),
                "&fTracking Scroll",
                "\n&eLearn the location of a player!\n\n&7Consumable", 0, false);
        shopMenu.setOption(4, new ItemStack(Material.GRAY_DYE),
                "&fSpeed Enchant",
                "\n&eEnchant your armor with +1% speed!\n\n&8Soulbound", 0, false);
        shopMenu.setOption(5, new ItemStack(Material.COMPASS),
                "&6Tracking Compass",
                "\n&eLearn the location of a player!\n\n&8Soulbound", 0, false);

        // set the handler
        shopMenu.setHandler(event -> {

            if (event.getSlot() < 7) {

                event.setWillClose(false);
                event.setWillDestroy(false);

                switch (event.getSlot()) {
                    case 0:
                        if (attemptToTakeGold(pl, PRICE_POTION)) {
                            pl.getInventory().addItem(shadowmeldPotion());
                        }
                        break;
                    case 1:
                        if (attemptToTakeGold(pl, PRICE_BOAT)){
                            pl.getInventory().addItem(new ItemStack(Material.OAK_BOAT));
                        }
                        break;
                    case 2:
                        if (attemptToTakeGold(pl, PRICE_ORB)) {
                            pl.getInventory().addItem(scryingOrb());
                        }
                        break;
                    case 3:
                        if (attemptToTakeGold(pl, PRICE_SCROLL)) {
                            pl.getInventory().addItem(trackingScroll());
                        }
                        break;
                    case 4:
                        if (attemptToTakeGold(pl, PRICE_ENCHANT)) {
                            pl.getInventory().addItem(enchantScroll());
                        }
                        break;
                    case 5:
                        if (attemptToTakeGold(pl, PRICE_COMPASS)) {
                            pl.getInventory().addItem(trackingCompass());
                        }
                        break;
                }

                // close shop
            } else if (event.getSlot() == 8) {
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        });

        // update our internal menu
        this.setItemGUI(shopMenu);
    }

    private static ItemStack shadowmeldPotion() {

        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta pMeta = (PotionMeta) potion.getItemMeta();
        String desc = "\n&eAfter standing still for 5 seconds," +
                "\n&eyou turn invisible for 30 seconds!" +
                "\n&eDealing or taking damage ends" +
                "\n&ethe effect early.";

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

    private static ItemStack scryingOrb() {
        MythicItem mi = MythicMobs.inst().getItemManager().getItem("ScryingOrb").get();
        AbstractItemStack abstractItemStack = mi.generateItemStack(1);
        ItemStack orb = BukkitAdapter.adapt(abstractItemStack);
        return orb;
    }

    private static ItemStack trackingScroll() {
        return new ItemStack(Material.PAPER);
    }

    private static ItemStack enchantScroll() {
        return new ItemStack(Material.GRAY_DYE);
    }

    private static ItemStack trackingCompass() {
        return new ItemStack(Material.COMPASS);
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

        RunicCore.getInstance().getConfig().set(pl.getUniqueId() + ".info.prof.hunter_points", HunterTask.getTotalPoints(pl)-price);
        RunicCore.getInstance().saveConfig();
        RunicCore.getInstance().reloadConfig();
        return true;
    }
}
