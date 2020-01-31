package com.runicrealms.plugin.professions.crafting.hunter;

import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.shops.Shop;
import com.runicrealms.plugin.professions.Workstation;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    @Override
    public void setupShop(Player pl) {

        // name the menu
        super.setupShop("&eHunter Shop", false);
        ItemGUI shopMenu = getItemGUI();

        shopMenu.setOption(0, Workstation.potionItem(Color.BLACK, "", ""),
                "&fShadowmeld Potion",
                "\n&eAfter standing still for 5s, you turn invisible!\n\n&7Consumable", 0, false);
        shopMenu.setOption(1, new ItemStack(Material.OAK_BOAT),
                "Boat",
                "", 0, false);

        MythicItem mi = MythicMobs.inst().getItemManager().getItem("ScryingOrb").get();
        AbstractItemStack abstractItemStack = mi.generateItemStack(1);
        ItemStack head = BukkitAdapter.adapt(abstractItemStack);

        shopMenu.setOption(2, head,
                "&fScrying Orb",
                "&7Learn the combat stats of a player.\n\n&7Consumable", 0, false);
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
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
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

    // create 6 static itemstacks

    /**
     * This method checks if player has required hunter points amount, then takes it and proceeds
     */
    private boolean attemptToTakeGold(Player pl, int price) {

        int currentPoints = HunterTask.getTotalPoints(pl);

        if (currentPoints >= price) {
            // todo: reduce points
            return true;
        }

        pl.playSound(pl.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
        pl.sendMessage(ChatColor.RED + "You don't have enough hunter points!");
        return false;
    }
}
