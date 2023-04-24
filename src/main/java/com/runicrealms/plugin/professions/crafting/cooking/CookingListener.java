package com.runicrealms.plugin.professions.crafting.cooking;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.crafting.ListenerResource;
import com.runicrealms.runicitems.item.event.RunicItemGenericTriggerEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CookingListener implements Listener {

    /**
     * @param player   to be healed
     * @param healAmt  the complete heal amount
     * @param duration the duration of the spell
     */
    private void healOverTime(Player player, double healAmt, double duration) {
        new BukkitRunnable() {
            int count = 1;

            @Override
            public void run() {
                if (count > duration) {
                    this.cancel();
                } else {
                    count += 1;
                    RunicCore.getSpellAPI().healPlayer(player, player, (healAmt / duration));
                }
            }
        }.runTaskTimer(RunicProfessions.getInstance(), 0, 20L);
    }

    /**
     * Controls custom food items
     */
    @EventHandler
    public void onCustomFoodEat(RunicItemGenericTriggerEvent event) {
        if (!event.getItem().getTemplateId().equals(ListenerResource.AMBROSIA_STEW.getTemplateId())) return;
        Player player = event.getPlayer();
        if (player.getHealth() == player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
            player.sendMessage(ChatColor.GRAY + "You can't use this item at full health.");
            return;
        }
        takeItem(player, event.getItemStack());
        player.setFoodLevel(player.getFoodLevel() + 6);
        healOverTime(player, CookingMenu.getAmbrosiaStewAmt(), CookingMenu.getStewDuration());
    }

    /**
     * Removes consumed item from players hand or offhand
     *
     * @param player to remove item from
     * @param item   from consume event
     */
    private void takeItem(Player player, ItemStack item) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getInventory().getItemInOffHand().equals(item)) {
                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                } else {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }
            }
        }.runTaskLaterAsynchronously(RunicProfessions.getInstance(), 1L);
    }
}
