package com.runicrealms.plugin.professions.crafting.Jeweler;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.enums.ArmorSlotEnum;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.LoreGenerator;
import com.runicrealms.plugin.item.shops.Shop;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class JewelMaster extends Shop {

    private static final int PRICE = 32;
    private HashMap<UUID, List<ItemStack>> storedItems; // keep track of items stored in menu

    public JewelMaster(Player pl) {
        setupShop(pl);
        storedItems = new HashMap<>();
        List<ItemStack> items = new ArrayList<>();
        storedItems.put(pl.getUniqueId(), items);
    }

    @Override
    public void setupShop(Player pl) {

        // name the menu
        super.setupShop("&eJewel Master", true);
        ItemGUI jewelMenu = getItemGUI();

        //set the visual items
        jewelMenu.setOption(0, new ItemStack(Material.AIR), "", "", 0, false);
        jewelMenu.setOption(7, new ItemStack(Material.SLIME_BALL),
                "&aRemove Gems", "&cCaution: &7Doing this will cost\n&7you &6&l" + PRICE + "G per item &7and remove all gems!", 0, false);

        // set the handler
        jewelMenu.setHandler(event -> {

            if (event.getSlot() < 7) {
                event.setWillClose(false);
                event.setWillDestroy(false);

            } else if (event.getSlot() == 7) {

                // check the contents
                if (jewelMenu.getItem(0) == null) {
                    pl.playSound(pl.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1);
                    pl.sendMessage(ChatColor.RED + "You must place an item in the shop!");
                    event.setWillClose(false);
                    event.setWillDestroy(false);
                    return;
                }

                storedItems.get(pl.getUniqueId()).add(jewelMenu.getItem(0));
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                event.setWillClose(true);
                event.setWillDestroy(true);

                attemptToRemoveGems(pl, jewelMenu.getItem(0));

            } else if (event.getSlot() == 8) {
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        });

        // update our internal menu
        this.setItemGUI(jewelMenu);
    }

    private void attemptToRemoveGems(Player pl, ItemStack item) {

        if (!checkHasGemstones(item)) {
            pl.playSound(pl.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1);
            pl.sendMessage(ChatColor.RED + "This item has no socketed gemstones!");

            // return stored items to player
            if (storedItems.get(pl.getUniqueId()) == null) return;
            for (ItemStack itemStack : storedItems.get(pl.getUniqueId())) {
                pl.getInventory().addItem(itemStack);
            }
            return;
        }

        removeGemstones(pl, item);
    }

    /**
     * Will only return true if the item has gem slots AND at least ONE current gem.
     */
    private boolean checkHasGemstones(ItemStack item) {
        if (item.getItemMeta() == null) return false;
        int socketCount = (int) AttributeUtil.getCustomDouble(item, "custom.socketCount");
        int currentSockets = (int) AttributeUtil.getCustomDouble(item, "custom.currentSockets");
        return socketCount != 0 && currentSockets != 0;
    }

    private void removeGemstones(Player pl, ItemStack item) {

        // retrieve current STORED stats
        double storedHealth = AttributeUtil.getCustomDouble(item, "gem.maxHealth");
        double storedMana = AttributeUtil.getCustomDouble(item, "gem.manaBoost");
        double storedDmg = AttributeUtil.getCustomDouble(item, "gem.attackDamage");
        double storedHealing = AttributeUtil.getCustomDouble(item, "gem.healingBoost");
        double storedMagDmg = AttributeUtil.getCustomDouble(item, "gem.magicDamage");

        // retrieve current ITEM stats
        double itemHealth = AttributeUtil.getGenericDouble(item, "generic.maxHealth");
        double itemMana = AttributeUtil.getCustomDouble(item, "custom.manaBoost");
        double itemDmg = AttributeUtil.getCustomDouble(item, "custom.attackDamage");
        double itemHealing = AttributeUtil.getCustomDouble(item, "custom.healingBoost");
        double itemMagDmg = AttributeUtil.getCustomDouble(item, "custom.magicDamage");

        // subtract stored stats from item stats
        item = AttributeUtil.addGenericStat(item, "generic.maxHealth", itemHealth - storedHealth, getSlot(item)); // ruby
        item = AttributeUtil.addCustomStat(item, "custom.manaBoost", itemMana - storedMana); // sapphire
        item = AttributeUtil.addCustomStat(item, "custom.attackDamage", itemDmg - storedDmg); // opal
        item = AttributeUtil.addCustomStat(item, "custom.healingBoost", itemHealing - storedHealing); // emerald
        item = AttributeUtil.addCustomStat(item, "custom.magicDamage", itemMagDmg - storedMagDmg); // diamond

        // re-make lore
        // todo: update color using method from item scrapper (make static)
        LoreGenerator.generateItemLore(item, ChatColor.YELLOW, item.getItemMeta().getDisplayName(), "", false);

        pl.getInventory().addItem(item);
        pl.playSound(pl.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5f, 2.0f);
        pl.sendMessage(ChatColor.GREEN + "Your item had its gemstones removed!");
    }

    private String getSlot(ItemStack item) {
        ArmorSlotEnum itemSlot = ArmorSlotEnum.matchSlot(item);
        String slot;
        switch (itemSlot) {
            case HELMET:
                slot = "head";
                break;
            case CHESTPLATE:
                slot = "chest";
                break;
            case LEGGINGS:
                slot = "legs";
                break;
            case BOOTS:
                slot = "feet";
                break;
            case OFFHAND:
                slot = "offhand";
                break;
            default:
                slot = "mainhand";
                break;
        }
        return slot;
    }

    public HashMap<UUID, List<ItemStack>> getStoredItems() {
        return storedItems;
    }
}
