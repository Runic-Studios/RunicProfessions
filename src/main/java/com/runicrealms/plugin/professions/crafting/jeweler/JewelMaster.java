package com.runicrealms.plugin.professions.crafting.jeweler;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.enums.ArmorSlotEnum;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.LoreGenerator;
import com.runicrealms.plugin.item.shops.RunicShop;
import com.runicrealms.plugin.item.util.ItemRemover;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

// todo: test on items w/ two gem slots
public class JewelMaster extends RunicShop {

    private static final int PRICE = 32;
    private final HashMap<UUID, List<ItemStack>> storedItems; // keep track of items stored in menu

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
        String enchantment = AttributeUtil.getCustomString(oldItem, "scroll.enchantment");
        int percent = (int) AttributeUtil.getCustomDouble(oldItem, "scroll.percent");
        String tierSet = AttributeUtil.getCustomString(oldItem, "tierset");
        // todo: just read and copy all custom stats

        // retrieve old STORED stats
        double storedHealth = AttributeUtil.getCustomDouble(oldItem, "gem.maxHealth");
        double storedHealthRegen = AttributeUtil.getCustomDouble(oldItem, "gem.healthRegen");
        double storedMana = AttributeUtil.getCustomDouble(oldItem, "gem.manaBoost");
        double storedManaRegen = AttributeUtil.getCustomDouble(oldItem, "gem.manaRegen");
        double storedDmg = AttributeUtil.getCustomDouble(oldItem, "gem.attackDamage");
        double storedHealing = AttributeUtil.getCustomDouble(oldItem, "gem.healingBoost");
        double storedMagDmg = AttributeUtil.getCustomDouble(oldItem, "gem.magicDamage");
        double storedShield = AttributeUtil.getCustomDouble(oldItem, "gem.shield");

        // retrieve old ITEM stats
        double itemHealth = AttributeUtil.getCustomDouble(oldItem, "custom.maxHealth");
        double itemHealthRegen = AttributeUtil.getCustomDouble(oldItem, "custom.healthRegen");
        double itemMana = AttributeUtil.getCustomDouble(oldItem, "custom.manaBoost");
        double itemManaRegen = AttributeUtil.getCustomDouble(oldItem, "custom.manaRegen");
        double itemDmg = AttributeUtil.getCustomDouble(oldItem, "custom.attackDamage");
        double itemHealing = AttributeUtil.getCustomDouble(oldItem, "custom.healingBoost");
        double itemMagDmg = AttributeUtil.getCustomDouble(oldItem, "custom.magicDamage");
        double itemShield = AttributeUtil.getCustomDouble(oldItem, "custom.shield");

        // create a NEW item with updated attributes, update its durability
        ItemStack newItem = new ItemStack(oldItem.getType());
        ItemMeta metaNew = newItem.getItemMeta();
        ((Damageable) metaNew).setDamage(((Damageable) oldItem.getItemMeta()).getDamage());
        newItem.setItemMeta(metaNew);

        if (AttributeUtil.getCustomString(oldItem, "untradeable").equals("true")) {
            newItem = AttributeUtil.addCustomStat(newItem, "untradeable", "true");
        }
        if (AttributeUtil.getCustomString(oldItem, "soulbound").equals("true")) {
            newItem = AttributeUtil.addCustomStat(newItem, "soulbound", "true");
        }

        ArmorSlotEnum itemSlot = ArmorSlotEnum.matchSlot(newItem);
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

        newItem = AttributeUtil.addGenericStat(newItem, "generic.armor", 0, slot); // remove armor values

        // fill 'da stats
        // subtract stored stats from item stats
        newItem = AttributeUtil.addCustomStat(newItem, "custom.socketCount", socketCount);
        newItem = AttributeUtil.addCustomStat(newItem, "required.level", reqLv); // required level
        if (!enchantment.equals("")) {
            newItem = AttributeUtil.addCustomStat(newItem, "scroll.enchantment", enchantment);
            newItem = AttributeUtil.addCustomStat(newItem, "scroll.percent", percent);
        }
        newItem = AttributeUtil.addCustomStat(newItem, "tierset", tierSet);

        newItem = AttributeUtil.addCustomStat(newItem, "custom.maxHealth", itemHealth - storedHealth); // ruby (health)
        newItem = AttributeUtil.addCustomStat(newItem, "custom.healthRegen", itemHealthRegen - storedHealthRegen); // ruby (regen)
        newItem = AttributeUtil.addCustomStat(newItem, "custom.manaBoost", itemMana - storedMana); // sapphire (mana)
        newItem = AttributeUtil.addCustomStat(newItem, "custom.manaRegen", itemManaRegen - storedManaRegen); // sapphire (regen)
        newItem = AttributeUtil.addCustomStat(newItem, "custom.attackDamage", itemDmg - storedDmg); // opal
        newItem = AttributeUtil.addCustomStat(newItem, "custom.healingBoost", itemHealing - storedHealing); // emerald
        newItem = AttributeUtil.addCustomStat(newItem, "custom.magicDamage", itemMagDmg - storedMagDmg); // diamond
        newItem = AttributeUtil.addCustomStat(newItem, "custom.shield", itemShield - storedShield); // tier sets, shields

        ChatColor tier = ChatColor.WHITE;
        if (oldItem.getItemMeta().getDisplayName().contains(ChatColor.GRAY + "")) {
            tier = ChatColor.GRAY;
        } else if (oldItem.getItemMeta().getDisplayName().contains(ChatColor.GREEN + "")) {
            tier = ChatColor.GREEN;
        } else if (oldItem.getItemMeta().getDisplayName().contains(ChatColor.AQUA + "")) {
            tier = ChatColor.AQUA;
        } else if (oldItem.getItemMeta().getDisplayName().contains(ChatColor.LIGHT_PURPLE + "")) {
            tier = ChatColor.LIGHT_PURPLE;
        } else if (oldItem.getItemMeta().getDisplayName().contains(ChatColor.GOLD + "")) {
            tier = ChatColor.GOLD;
        }

        // re-make lore
        LoreGenerator.generateItemLore(newItem, tier, oldItemName, "", false, "");

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
