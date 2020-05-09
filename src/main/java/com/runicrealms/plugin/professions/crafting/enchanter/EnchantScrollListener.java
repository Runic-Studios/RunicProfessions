package com.runicrealms.plugin.professions.crafting.enchanter;

import com.runicrealms.plugin.enums.ItemTypeEnum;
import com.runicrealms.plugin.item.LoreGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import com.runicrealms.plugin.attributes.AttributeUtil;

/**
 * This class manages the enchantment of armor!
 * It checks to ensure the item has not been enchanted yet
 * @author Skyfallin_
 */
public class EnchantScrollListener implements Listener {

    @EventHandler
    public void onItemEnchant(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) return;
        if (e.getClickedInventory() == null) return;
        if (!(e.getClickedInventory().getType().equals(InventoryType.PLAYER))) return;
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player pl = (Player) e.getWhoClicked();
        ItemStack heldScroll = e.getCursor();
        ItemStack armorToEnchant = e.getCurrentItem();
        ItemTypeEnum enchantArmorType = ItemTypeEnum.matchType(armorToEnchant);
        String enchantItemName = armorToEnchant.getItemMeta().getDisplayName();

        if (enchantArmorType != ItemTypeEnum.CLOTH
                && enchantArmorType != ItemTypeEnum.LEATHER
                && enchantArmorType != ItemTypeEnum.MAIL
                && enchantArmorType != ItemTypeEnum.GILDED
                && enchantArmorType != ItemTypeEnum.PLATE) {
            return;
        }

        // verify that the cursor item is a gemstone
        String isEnchantScroll = AttributeUtil.getCustomString(heldScroll, "scroll.enchantment");
        if (isEnchantScroll.equals("")) return;

        EnchantEnum enchantment = EnchantEnum.getEnum(AttributeUtil.getCustomString(heldScroll, "scroll.enchantment"));

        // verify that the item to be socketed has not been enchanted
        String currentEnchant = AttributeUtil.getCustomString(armorToEnchant, "scroll.enchantment");
        if (!currentEnchant.equals("")) {
            pl.playSound(pl.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.0f);
            pl.sendMessage(ChatColor.RED + "This item has already been enchanted!");
            return;
        }

        // create a new item with updated attributes, update its durability
        armorToEnchant = AttributeUtil.addCustomStat(armorToEnchant, "scroll.enchantment", enchantment.getName());
        double amount = AttributeUtil.getCustomDouble(heldScroll, "scroll.percent");
        armorToEnchant = AttributeUtil.addCustomStat(armorToEnchant, "scroll.percent", amount);

        ChatColor tier = ChatColor.WHITE;
        if (armorToEnchant.getItemMeta().getDisplayName().contains(ChatColor.GRAY + "")) {
            tier = ChatColor.GRAY;
        } else if (armorToEnchant.getItemMeta().getDisplayName().contains(ChatColor.GREEN + "")) {
            tier = ChatColor.GREEN;
        } else if (armorToEnchant.getItemMeta().getDisplayName().contains(ChatColor.AQUA + "")) {
            tier = ChatColor.AQUA;
        } else if (armorToEnchant.getItemMeta().getDisplayName().contains(ChatColor.LIGHT_PURPLE + "")) {
            tier = ChatColor.LIGHT_PURPLE;
        } else if (armorToEnchant.getItemMeta().getDisplayName().contains(ChatColor.GOLD + "")) {
            tier = ChatColor.GOLD;
        }

        LoreGenerator.generateItemLore(armorToEnchant, tier, enchantItemName, "", false, "");

        // remove the gemstone from inventory, update the item in inventory
        //ItemRemover.takeItem(pl, heldScroll, 1);
        e.setCancelled(true);
        e.setCurrentItem(armorToEnchant);
        heldScroll.setAmount(heldScroll.getAmount()-1);
        e.setCursor(heldScroll);
        pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
        pl.sendMessage(ChatColor.GREEN + "You have enchanted this item!");
    }
}
