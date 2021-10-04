//package com.runicrealms.plugin.professions.crafting.jeweler;
//
//import com.runicrealms.plugin.item.LoreGenerator;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.Sound;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.event.inventory.InventoryType;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.Damageable;
//import org.bukkit.inventory.meta.ItemMeta;
//import com.runicrealms.plugin.attributes.AttributeUtil;
//import com.runicrealms.plugin.enums.ArmorSlotEnum;
//
///**
// * This class manages the socketing of gems.
// * It checks to ensure the item has at least one open
// * gem slot.
// * @author Skyfallin_
// */
//public class SocketListener implements Listener {
//
//    // ignore durability 100 (that's harvesting tools)
//    @EventHandler
//    public void onItemSocket(InventoryClickEvent e) {
//
//        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
//        if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) return;
//        if (e.getClickedInventory() == null) return;
//        if (!(e.getClickedInventory().getType().equals(InventoryType.PLAYER))) return;
//        if (!(e.getWhoClicked() instanceof Player)) return;
//
//        Player pl = (Player) e.getWhoClicked();
//        ItemStack heldItem = e.getCursor();
//        ItemStack socketItem = e.getCurrentItem();
//        ItemMeta metaOld = socketItem.getItemMeta();
//        int durabOld = ((Damageable) metaOld).getDamage();
//        Material socketItemType = socketItem.getType();
//        String socketItemName = socketItem.getItemMeta().getDisplayName();
//
//        // verify that the cursor item is a gemstone
//        String isGemstone = AttributeUtil.getCustomString(heldItem, "custom.isGemstone");
//        if (!isGemstone.equals("true")) return;
//
//        switch (socketItemType) {
//            case REDSTONE:
//            case LAPIS_LAZULI:
//            case EMERALD:
//            case QUARTZ:
//            case DIAMOND:
//                return;
//        }
//
//        // verify that the item to be socketed has open slots
//        int socketCount = (int) AttributeUtil.getCustomDouble(socketItem, "custom.socketCount");
//        int currentSockets = (int) AttributeUtil.getCustomDouble(socketItem, "custom.currentSockets");
//        if (socketCount == 0 || currentSockets >= socketCount) {
//            pl.playSound(pl.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.0f);
//            pl.sendMessage(ChatColor.RED + "This item has no available sockets.");
//            return;
//        }
//
//        // retrieve the current custom values of the two items
//        double itemHealth = AttributeUtil.getCustomDouble(socketItem, "custom.maxHealth");
//        double gemHealth = AttributeUtil.getCustomDouble(heldItem, "custom.maxHealth");
//
//        double itemHealthRegen = AttributeUtil.getCustomDouble(socketItem, "custom.healthRegen");
//        double gemHealthRegen = AttributeUtil.getCustomDouble(heldItem, "custom.healthRegen");
//
//        double itemMana = AttributeUtil.getCustomDouble(socketItem, "custom.manaBoost");
//        double gemMana = AttributeUtil.getCustomDouble(heldItem, "custom.manaBoost");
//
//        double itemManaRegen = AttributeUtil.getCustomDouble(socketItem, "custom.manaRegen");
//        double gemManaRegen = AttributeUtil.getCustomDouble(heldItem, "custom.manaRegen");
//
//        double itemDmg = AttributeUtil.getCustomDouble(socketItem, "custom.attackDamage");
//        double gemDmg = AttributeUtil.getCustomDouble(heldItem, "custom.attackDamage");
//        double itemHealing = AttributeUtil.getCustomDouble(socketItem, "custom.healingBoost");
//        double gemHealing = AttributeUtil.getCustomDouble(heldItem, "custom.healingBoost");
//        double itemMagDmg = AttributeUtil.getCustomDouble(socketItem, "custom.magicDamage");
//        double gemMagDmg = AttributeUtil.getCustomDouble(heldItem, "custom.magicDamage");
//        double itemShield = AttributeUtil.getCustomDouble(socketItem, "custom.shield");
//        double gemShield = AttributeUtil.getCustomDouble(heldItem, "custom.shield");
//        double reqLv = AttributeUtil.getCustomDouble(socketItem, "required.level");
//        String enchantment = AttributeUtil.getCustomString(socketItem, "scroll.enchantment");
//        int percent = (int) AttributeUtil.getCustomDouble(socketItem, "scroll.percent");
//        String tierSet = AttributeUtil.getCustomString(socketItem, "tierset");
//
//        // create a new item with updated attributes, update its durability
//        ItemStack newItem = new ItemStack(socketItemType);
//        ItemMeta metaNew = newItem.getItemMeta();
//        ((Damageable) metaNew).setDamage(durabOld);
//        newItem.setItemMeta(metaNew);
//
//        // fill the sockets
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.socketCount", socketCount);
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.currentSockets", currentSockets+1);
//        ArmorSlotEnum itemSlot = ArmorSlotEnum.matchSlot(newItem);
//        String slot;
//        switch (itemSlot) {
//            case HELMET:
//                slot = "head";
//                break;
//            case CHESTPLATE:
//                slot = "chest";
//                break;
//            case LEGGINGS:
//                slot = "legs";
//                break;
//            case BOOTS:
//                slot = "feet";
//                break;
//            case OFFHAND:
//                slot = "offhand";
//                break;
//            default:
//                slot = "mainhand";
//                break;
//        }
//
//        if (AttributeUtil.getCustomString(socketItem, "untradeable").equals("true")) newItem = AttributeUtil.addCustomStat(newItem, "untradeable", "true");
//        if (AttributeUtil.getCustomString(socketItem, "soulbound").equals("true")) newItem = AttributeUtil.addCustomStat(newItem, "soulbound", "true");
//        newItem = AttributeUtil.addGenericStat(newItem, "generic.armor", 0, slot); // remove armor values
//
//        // add 'da stats
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.maxHealth", itemHealth + gemHealth); // ruby (health)
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.healthRegen", itemHealthRegen + gemHealthRegen); // ruby (regen)
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.manaBoost", itemMana + gemMana); // sapphire (mana)
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.manaRegen", itemManaRegen + gemManaRegen); // sapphire (regen)
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.attackDamage", itemDmg + gemDmg); // opal
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.healingBoost", itemHealing + gemHealing); // emerald
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.magicDamage", itemMagDmg + gemMagDmg); // diamond
//        newItem = AttributeUtil.addCustomStat(newItem, "custom.shield", itemShield + gemShield); // tier sets, shields
//        newItem = AttributeUtil.addCustomStat(newItem, "required.level", reqLv); // required level
//        if (!enchantment.equals("")) {
//            newItem = AttributeUtil.addCustomStat(newItem, "scroll.enchantment", enchantment);
//            newItem = AttributeUtil.addCustomStat(newItem, "scroll.percent", percent);
//        }
//        if (!tierSet.equals("")) {
//            newItem = AttributeUtil.addCustomStat(newItem, "tierset", tierSet);
//        }
//
//        // ------------------------------------------------------------------------------------------------------------
//        // NEW: store the stats of the gems in the item for (optional) gem removal
//
//        // retrieve current stored stats
//        double storedHealth = AttributeUtil.getCustomDouble(socketItem, "gem.maxHealth");
//        double storedHealthRegen = AttributeUtil.getCustomDouble(socketItem, "gem.healthRegen");
//        double storedMana = AttributeUtil.getCustomDouble(socketItem, "gem.manaBoost");
//        double storedManaRegen = AttributeUtil.getCustomDouble(socketItem, "gem.manaRegen");
//        double storedDmg = AttributeUtil.getCustomDouble(socketItem, "gem.attackDamage");
//        double storedHealing = AttributeUtil.getCustomDouble(socketItem, "gem.healingBoost");
//        double storedMagDmg = AttributeUtil.getCustomDouble(socketItem, "gem.magicDamage");
//        double storedShield = AttributeUtil.getCustomDouble(socketItem, "gem.shield");
//
//        // store new gem stats.
//        newItem = AttributeUtil.addCustomStat(newItem, "gem.maxHealth", storedHealth + gemHealth); // ruby (health)
//        newItem = AttributeUtil.addCustomStat(newItem, "gem.healthRegen", storedHealthRegen + gemHealthRegen); // ruby (regen)
//        newItem = AttributeUtil.addCustomStat(newItem, "gem.manaBoost", storedMana + gemMana); // sapphire (mana)
//        newItem = AttributeUtil.addCustomStat(newItem, "gem.manaRegen", storedManaRegen + gemManaRegen); // sapphire (regen)
//        newItem = AttributeUtil.addCustomStat(newItem, "gem.attackDamage", storedDmg + gemDmg); // opal
//        newItem = AttributeUtil.addCustomStat(newItem, "gem.healingBoost", storedHealing + gemHealing); // emerald
//        newItem = AttributeUtil.addCustomStat(newItem, "gem.magicDamage", storedMagDmg + gemMagDmg); // diamond
//        newItem = AttributeUtil.addCustomStat(newItem, "gem.shield", storedShield + gemShield); // tier sets, shields
//        // ------------------------------------------------------------------------------------------------------------
//
//        ChatColor tier = ChatColor.WHITE;
//        if (socketItem.getItemMeta().getDisplayName().contains(ChatColor.GRAY + "")) {
//            tier = ChatColor.GRAY;
//        } else if (socketItem.getItemMeta().getDisplayName().contains(ChatColor.GREEN + "")) {
//            tier = ChatColor.GREEN;
//        } else if (socketItem.getItemMeta().getDisplayName().contains(ChatColor.AQUA + "")) {
//            tier = ChatColor.AQUA;
//        } else if (socketItem.getItemMeta().getDisplayName().contains(ChatColor.LIGHT_PURPLE + "")) {
//            tier = ChatColor.LIGHT_PURPLE;
//        } else if (socketItem.getItemMeta().getDisplayName().contains(ChatColor.GOLD + "")) {
//            tier = ChatColor.GOLD;
//        }
//
//        LoreGenerator.generateItemLore(newItem, tier, socketItemName, "", false, "");
//
//        // remove the gemstone from inventory, update the item in inventory
//        e.setCancelled(true);
//        e.setCurrentItem(newItem);
//        heldItem.setAmount(heldItem.getAmount()-1);
//        e.setCursor(heldItem);
//        pl.playSound(pl.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
//        pl.sendMessage(ChatColor.GREEN + "You placed your gemstone into this item's socket!");
//    }
//}
