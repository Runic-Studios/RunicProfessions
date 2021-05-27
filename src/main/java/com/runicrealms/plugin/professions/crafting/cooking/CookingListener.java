package com.runicrealms.plugin.professions.crafting.cooking;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.spellapi.spellutil.HealUtil;
import com.runicrealms.runicitems.item.event.RunicItemGenericTriggerEvent;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CookingListener implements Listener {

    /*
    Controls custom food items
     */
    @EventHandler
    public void onCustomFoodEat(RunicItemGenericTriggerEvent e) {
        if (!e.getItemStack().isSimilar(CookingItems.AMBROSIA_STEW_ITEMSTACK)) return;
        Player pl = e.getPlayer();
        if (pl.getHealth() == pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) return;
        takeItem(pl, e.getItemStack());
        pl.setFoodLevel(pl.getFoodLevel() + 6);
        healOverTime(pl, CookingMenu.getAmbrosiaStewAmt(), CookingMenu.getStewDuration());
    }

    /**
     * Removes consumed item from players hand or offhand
     *
     * @param pl to remove item from
     * @param item from consume event
     */
    private void takeItem(Player pl, ItemStack item) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pl.getInventory().getItemInOffHand().equals(item)) {
                    pl.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                } else {
                    pl.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }
            }
        }.runTaskLaterAsynchronously(RunicProfessions.getInstance(), 1L);
    }

    private void healOverTime(Player pl, int healAmt, int duration) {
        new BukkitRunnable() {
            int count = 1;

            @Override
            public void run() {
                if (count > duration) {
                    this.cancel();
                } else {
                    count += 1;
                    HealUtil.healPlayer((healAmt / duration), pl, pl,
                            false, false, false);
                }
            }
        }.runTaskTimer(RunicProfessions.getInstance(), 0, 20L);
    }
}
