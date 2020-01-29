package com.runicrealms.plugin.professions.crafting.jeweler;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.enums.ArmorSlotEnum;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.LoreGenerator;
import com.runicrealms.plugin.item.shops.Shop;
import com.runicrealms.plugin.item.util.ItemRemover;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

// todo: test on items w/ two gem slots
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
                "&aRemove Gems", "&cCaution: &7Doing this will cost\n&7you &6&l" + PRICE + "G &7per item and remove\n&7all gems!", 0, false);

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

        // check that the player has the reagents
        if (!pl.getInventory().contains(Material.GOLD_NUGGET, PRICE)) {
            pl.playSound(pl.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
            pl.sendMessage(ChatColor.RED + "You don't have enough gold!");

            // return stored items to player
            if (storedItems.get(pl.getUniqueId()) == null) return;
            for (ItemStack itemStack : storedItems.get(pl.getUniqueId())) {
                pl.getInventory().addItem(itemStack);
            }
            return;
        }

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

    private void removeGemstones(Player pl, ItemStack oldItem) {

        // take gold
        ItemRemover.takeItem(pl, Material.GOLD_NUGGET, PRICE);

        // retrieve misc. info
        String oldItemName = oldItem.getItemMeta().getDisplayName();
        int socketCount = (int) AttributeUtil.getCustomDouble(oldItem, "custom.socketCount");
        double reqLv = AttributeUtil.getCustomDouble(oldItem, "required.level");

        // retrieve old STORED stats
        double storedHealth = AttributeUtil.getCustomDouble(oldItem, "gem.maxHealth");
        double storedMana = AttributeUtil.getCustomDouble(oldItem, "gem.manaBoost");
        double storedDmg = AttributeUtil.getCustomDouble(oldItem, "gem.attackDamage");
        double storedHealing = AttributeUtil.getCustomDouble(oldItem, "gem.healingBoost");
        double storedMagDmg = AttributeUtil.getCustomDouble(oldItem, "gem.magicDamage");

        // retrieve old ITEM stats
        double itemHealth = AttributeUtil.getGenericDouble(oldItem, "generic.maxHealth");
        double itemMana = AttributeUtil.getCustomDouble(oldItem, "custom.manaBoost");
        double itemDmg = AttributeUtil.getCustomDouble(oldItem, "custom.attackDamage");
        double itemHealing = AttributeUtil.getCustomDouble(oldItem, "custom.healingBoost");
        double itemMagDmg = AttributeUtil.getCustomDouble(oldItem, "custom.magicDamage");

        // create a NEW item with updated attributes, update its durability
        ItemStack newItem = new ItemStack(oldItem.getType());
        ItemMeta metaNew = newItem.getItemMeta();
        ((Damageable) metaNew).setDamage(((Damageable) oldItem.getItemMeta()).getDamage());
        newItem.setItemMeta(metaNew);

        // fill 'da stats
        // subtract stored stats from item stats
        newItem = AttributeUtil.addCustomStat(newItem, "custom.socketCount", socketCount);
        newItem = AttributeUtil.addCustomStat(newItem, "required.level", reqLv); // required level
        newItem = AttributeUtil.addGenericStat(newItem, "generic.maxHealth", itemHealth - storedHealth, getSlot(oldItem)); // ruby
        newItem = AttributeUtil.addCustomStat(newItem, "custom.manaBoost", itemMana - storedMana); // sapphire
        newItem = AttributeUtil.addCustomStat(newItem, "custom.attackDamage", itemDmg - storedDmg); // opal
        newItem = AttributeUtil.addCustomStat(newItem, "custom.healingBoost", itemHealing - storedHealing); // emerald
        newItem = AttributeUtil.addCustomStat(newItem, "custom.magicDamage", itemMagDmg - storedMagDmg); // diamond

        // re-make lore
        LoreGenerator.generateItemLore(newItem, ChatColor.WHITE, oldItemName, "", false);

        pl.getInventory().addItem(newItem);
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
