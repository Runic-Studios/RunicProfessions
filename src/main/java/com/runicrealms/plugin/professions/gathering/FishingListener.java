package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.professions.event.CustomFishEvent;
import com.runicrealms.plugin.utilities.ActionBarUtil;
import com.runicrealms.plugin.utilities.CurrencyUtil;
import com.runicrealms.plugin.utilities.FloatingItemUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.minecraft.server.v1_15_R1.EntityFishingHook;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import com.runicrealms.plugin.utilities.HologramUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Listener for Fishing (Gathering Profesion)
 * randomizes which fish they receive
 * Checks name of WG region for "pond" to perform tasks
 */
@SuppressWarnings("FieldCanBeLocal")
public class FishingListener implements Listener {

    private final double nuggetRate = 5.0;

    @EventHandler
    public void onFishCatch(PlayerFishEvent e) {

        // disable exp
        e.setExpToDrop(0);

        e.setCancelled(false);
        if (e.getCaught() != null) e.getCaught().remove();
        if (e.getState() != PlayerFishEvent.State.BITE) return;

        // grab the player, location
        Player pl = e.getPlayer();
        Material itemType;
        String itemName;
        String holoString;
        String desc = "Crafting Reagent";

        // roll to see if player succesfully fished
        // roll to see what kind of fish they will receive
        double chance = ThreadLocalRandom.current().nextDouble(0, 100);
        int fishType = ThreadLocalRandom.current().nextInt(0, 100);
        Location hookLoc = e.getHook().getLocation();
        Vector fishPath = pl.getLocation().toVector().subtract
                (hookLoc.clone().add(0, 1, 0).toVector()).normalize();

        if (fishType < 50) {
            itemType = Material.SALMON;
            itemName = "Salmon";
            holoString = "+ Salmon";
        } else if (fishType < 75) {
            itemType = Material.COD;
            itemName = "Cod";
            holoString = "+ Cod";
        } else if (fishType < 95) {
            itemType = Material.TROPICAL_FISH;
            itemName = "Tropical Fish";
            holoString = "+ Tropical";
        } else {
            itemType = Material.PUFFERFISH;
            itemName = "Pufferfish";
            holoString = "+ Pufferfish";
        }

        if (pl.getInventory().getItemInMainHand().getType() == Material.AIR) {
            pl.sendMessage(ChatColor.RED + "You need a fishing rod to do that!");
            return;
        }

        // make sure player has harvesting tool
        // this will always be a hoe, so we check for the staff enum
        // we also ensure it has durability 100, arbitrarily chosen.
        ItemStack heldItem = pl.getInventory().getItemInMainHand();
        int slot = pl.getInventory().getHeldItemSlot();
        ItemMeta meta = pl.getInventory().getItemInMainHand().getItemMeta();
        int durability = ((Damageable) Objects.requireNonNull(meta)).getDamage();

        if (heldItem.getType() != Material.FISHING_ROD
                && durability != 1
                && durability != 2
                && durability != 3
                && durability != 4
                && durability != 5) {
            pl.sendMessage(ChatColor.RED + "You need a fishing rod to do that!");
            return;
        }

        // reduce items durability
        double itemDurab = AttributeUtil.getCustomDouble(heldItem, "durability");
        heldItem = AttributeUtil.addCustomStat(heldItem, "durability", itemDurab - 1);
        GatheringUtil.generateToolLore(heldItem, durability);
        if (itemDurab - 1 <= 0) {

            pl.playSound(pl.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.0f);
            pl.sendMessage(ChatColor.RED + "Your tool broke!");
            pl.getInventory().setItem(slot, null);
        } else {
            pl.getInventory().setItem(slot, heldItem);
        }

        // gather material
        gatherMaterial(pl, hookLoc, hookLoc.clone().add(0, 1.5, 0), itemType, holoString,
                itemName, desc, chance, fishPath, durability);
    }

    @EventHandler
    public void onRodUse(PlayerInteractEvent e) {

        Player pl = e.getPlayer();
        Location plLoc = pl.getLocation();

        // grab all regions the player is standing in
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(plLoc));
        Set<ProtectedRegion> regions = set.getRegions();

        if (regions == null) return;

        boolean canFish = false;

        // check the region for the keyword 'pond'
        // ignore the rest of this event if the player cannot fish
        for (ProtectedRegion region : regions) {
            if (region.getId().contains("pond")) {
                canFish = true;
            }
        }

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Material mainHand = pl.getInventory().getItemInMainHand().getType();
        Material offHand = pl.getInventory().getItemInOffHand().getType();

        if (mainHand != Material.FISHING_ROD && offHand != Material.FISHING_ROD) return;

        if (!canFish) {
            e.setCancelled(true);
            pl.sendMessage(ChatColor.RED + "You can't fish here.");
        }
    }

    /**
     * Prevent fish from spawning naturally.
     * We'll spawn them in ponds as NPCs, but not out in the ocean.
     */
    @EventHandler
    public void onFishSpawn(CreatureSpawnEvent e) {
        Entity spawned = e.getEntity();
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (spawned instanceof Fish) e.setCancelled(true);
    }

    /**
     * Prevents a player from consuming raw fish
     */
    @EventHandler
    public void onRawFishEat(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.COD || e.getItem().getType() == Material.SALMON) {
            e.getPlayer().sendMessage(ChatColor.RED + "I need to cook that first.");
            e.setCancelled(true);
        }
    }

    /**
     * Prevents a player from consuming puffer / tropical
     */
    @EventHandler
    public void onPufferTropFishEat(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        Material m = e.getItem().getType();
        if (m == Material.PUFFERFISH || m == Material.TROPICAL_FISH) {
            e.getPlayer().sendMessage(ChatColor.RED + "I shouldn't eat that.");
            e.setCancelled(true);
        }
    }

    private void gatherMaterial(Player pl, Location loc, Location fishLoc, Material gathered,
                                String name, String itemName, String desc,
                                double chance, Vector fishPath, int tier) {

        // call the fishing event
        ItemStack fish = gatheredItem(gathered, itemName, desc);
        CustomFishEvent event = new CustomFishEvent(pl, fish);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        double successRate;
        switch (tier) {
            case 5:
                successRate = 75;
                break;
            case 4:
                successRate = 62.5;
                break;
            case 3:
                successRate = 50;
                break;
            case 2:
                successRate = 37.5;
                break;
            case 1:
            default:
                successRate = 25;
                break;
        }

        if (chance < (100 - successRate)) {
            ActionBarUtil.sendTimedMessage(pl, "&cThe fish got away!", 3);
            return;
        }

        // spawn floating fish
        FloatingItemUtil.spawnFloatingItem(pl, fishLoc, gathered, 1, fishPath);

        // give the player the gathered item, drop on floor if inventory is full
        HologramUtil.createStaticHologram(pl, loc, ChatColor.GREEN + "" + ChatColor.BOLD + name, 0, 2, 0);
        HashMap<Integer, ItemStack> fishToDrop = pl.getInventory().addItem(fish);
        for (ItemStack is : fishToDrop.values()) {
            pl.getWorld().dropItem(pl.getLocation(), is);
        }

        // give the player a coin
        if (chance >= (100 - this.nuggetRate)) {
            pl.getWorld().playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 2.0f);
            HologramUtil.createStaticHologram(pl, loc, ChatColor.GOLD + "" + ChatColor.BOLD + "+ Coin", 0, 1.25, 0);

            HashMap<Integer, ItemStack> coin = pl.getInventory().addItem(CurrencyUtil.goldCoin());
            for (ItemStack is : coin.values()) {
                pl.getWorld().dropItem(pl.getLocation(), is);
            }
        }
    }

    private ItemStack gatheredItem(Material material, String itemName, String desc) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.WHITE + itemName);
        lore.add(ChatColor.GRAY + desc);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Reduces fishing time using NMS
     * Time for a 'bite' will be between 5-25 seconds
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        FishHook plHook = e.getHook();
        Random rand = new Random();
        int time = rand.nextInt(25 - 5) + 5;
        setBiteTime(plHook, time);
    }

    private void setBiteTime(FishHook hook, int time) {
        net.minecraft.server.v1_15_R1.EntityFishingHook hookCopy = (EntityFishingHook) ((CraftEntity) hook).getHandle();

        Field fishCatchTime = null;

        try {
            fishCatchTime = net.minecraft.server.v1_15_R1.EntityFishingHook.class.getDeclaredField("ap");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(fishCatchTime).setAccessible(true);

        try {
            fishCatchTime.setInt(hookCopy, time);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
