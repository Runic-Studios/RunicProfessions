package com.runicrealms.plugin.professions.crafting.alchemist;

import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.Workstation;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;

public class CauldronMenu extends Workstation {

    private static final int CAULDRON_MENU_SIZE = 54;

    public CauldronMenu(Player pl) {
        setupWorkstation(pl);
    }

    public void setupWorkstation(Player pl) {
        setupWorkstation("&f&l" + pl.getName() + "'s &e&lCauldron");
        ItemGUI baseMenu = getItemGUI();
        baseMenu.setOption(3, potionItem(Color.RED, "", ""), "&fBrew Potions", "&7Brew powerful and unique potions!", 0, false);
        baseMenu.setHandler(event -> {
            if (event.getSlot() == 3) {
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
                setItemGUI(cauldronMenu(pl));
                setTitle(cauldronMenu(pl).getName());
                getItemGUI().open(pl);
                event.setWillClose(false);
                event.setWillDestroy(true);
            } else if (event.getSlot() == 5) {
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
        });
        setItemGUI(baseMenu);
    }

    private ItemGUI cauldronMenu(Player pl) {
        int currentLvl = RunicCoreAPI.getPlayerCache(pl).getProfLevel();
        LinkedHashMap<Material, Integer> healthPotReqs = new LinkedHashMap<>();
        healthPotReqs.put(Material.GLASS_BOTTLE, 1);
        healthPotReqs.put(Material.REDSTONE_ORE, 1);
        healthPotReqs.put(Material.SALMON, 1);
        LinkedHashMap<Material, Integer> manaPotReqs = new LinkedHashMap<>();
        manaPotReqs.put(Material.GLASS_BOTTLE, 1);
        manaPotReqs.put(Material.LAPIS_ORE, 1);
        manaPotReqs.put(Material.COD, 1);
        LinkedHashMap<Material, Integer> slayPotReqs = new LinkedHashMap<>();
        slayPotReqs.put(Material.GLASS_BOTTLE, 1);
        slayPotReqs.put(Material.NETHER_QUARTZ_ORE, 1);
        slayPotReqs.put(Material.DIAMOND_ORE, 1);
        slayPotReqs.put(Material.TROPICAL_FISH, 1);
        LinkedHashMap<Material, Integer> lootPotReqs = new LinkedHashMap<>();
        lootPotReqs.put(Material.GLASS_BOTTLE, 1);
        lootPotReqs.put(Material.GOLDEN_CARROT, 1);
        lootPotReqs.put(Material.PUFFERFISH, 1);
        LinkedHashMap<Material, Integer> sacredFirePotReqs = new LinkedHashMap<>();
        sacredFirePotReqs.put(Material.GLASS_BOTTLE, 1);
        sacredFirePotReqs.put(Material.NETHER_WART, 1);
        sacredFirePotReqs.put(Material.PUFFERFISH, 1);
        ItemGUI cauldronMenu = craftingMenu(pl, CAULDRON_MENU_SIZE);
        cauldronMenu.setOption(4, new ItemStack(Material.CAULDRON), "&eCauldron", "&fClick &7an item to start crafting!\n&fClick &7here to return to the station", 0, false);
        setupItems(cauldronMenu, pl);
        cauldronMenu.setHandler(event -> {
            if (event.getSlot() == 4) {
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
                setupWorkstation(pl);
                getItemGUI().open(pl);
                event.setWillClose(false);
                event.setWillDestroy(true);
            } else {
                int mult = 1;
                if (event.isRightClick())
                    mult = 5;
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta == null) return;
                int slot = event.getSlot();
                int reqLevel = 0;
                int exp = 0;
                LinkedHashMap<Material, Integer> reqHashMap = new LinkedHashMap<>();
                if (slot == 9) {
                    reqHashMap = healthPotReqs;
                    exp = 35;
                } else if (slot == 10) {
                    reqLevel = 10;
                    reqHashMap = manaPotReqs;
                    exp = 60;
                } else if (slot == 11) {
                    reqLevel = 25;
                    reqHashMap = slayPotReqs;
                    exp = 90;
                } else if (slot == 12) {
                    reqLevel = 40;
                    reqHashMap = lootPotReqs;
                    exp = 600;
                } else if (slot == 13) {
                    reqLevel = 60;
                    reqHashMap = sacredFirePotReqs;
                }
                event.setWillClose(true);
                event.setWillDestroy(true);
                startCrafting(pl, reqHashMap, 999, reqLevel, event.getCurrentItem().getType(), currentLvl, exp, ((Damageable) meta).getDamage(), Particle.WATER_SPLASH, Sound.BLOCK_BREWING_STAND_BREW, Sound.ENTITY_GENERIC_DRINK, slot, mult);
            }
        });
        return cauldronMenu;
    }

    private void setupItems(ItemGUI forgeMenu, Player pl) {
        LinkedHashMap<Material, Integer> bottleReqs = new LinkedHashMap<>();
        bottleReqs.put(Material.GLASS, 3);
        createMenuItem(forgeMenu, pl, 9, Material.GLASS_BOTTLE,
                AlchemistItems.BOTTLE.getDisplayableItem().getDisplayName(), bottleReqs, "Glass",
                3, 35, 0, 0,
                generateItemLore(AlchemistItems.BOTTLE), true, false, false);
        LinkedHashMap<Material, Integer> healthPotReqs = new LinkedHashMap<>();
        healthPotReqs.put(Material.GLASS_BOTTLE, 1);
        healthPotReqs.put(Material.REDSTONE_ORE, 1);
        healthPotReqs.put(Material.SALMON, 1);
        LinkedHashMap<Material, Integer> manaPotReqs = new LinkedHashMap<>();
        manaPotReqs.put(Material.GLASS_BOTTLE, 1);
        manaPotReqs.put(Material.LAPIS_ORE, 1);
        manaPotReqs.put(Material.COD, 1);
        LinkedHashMap<Material, Integer> slayPotReqs = new LinkedHashMap<>();
        slayPotReqs.put(Material.GLASS_BOTTLE, 1);
        slayPotReqs.put(Material.NETHER_QUARTZ_ORE, 1);
        slayPotReqs.put(Material.DIAMOND_ORE, 1);
        slayPotReqs.put(Material.TROPICAL_FISH, 1);
        LinkedHashMap<Material, Integer> lootPotReqs = new LinkedHashMap<>();
        lootPotReqs.put(Material.GLASS_BOTTLE, 1);
        lootPotReqs.put(Material.GOLDEN_CARROT, 1);
        lootPotReqs.put(Material.PUFFERFISH, 1);
        createMenuItem(forgeMenu, pl, 10, Material.POTION,
                AlchemistItems.LESSER_POTION_HEALING.getDisplayableItem().getDisplayName(), healthPotReqs, "Glass Bottle\nUncut Ruby\nSalmon",
                5, 35, 0, 0,
                generateItemLore(AlchemistItems.LESSER_POTION_HEALING), false, false, false);
        createMenuItem(forgeMenu, pl, 11, Material.POTION, AlchemistItems.LESSER_POTION_MANA
                        .getDisplayableItem().getDisplayName(), manaPotReqs, "Glass Bottle\nUncut Sapphire\nCod", 8, 60, 10, 0,

                generateItemLore(AlchemistItems.LESSER_POTION_MANA), false, false, false);
        createMenuItem(forgeMenu, pl, 12, Material.POTION, AlchemistItems.LESSER_POTION_SLAYING
                        .getDisplayableItem().getDisplayName(), slayPotReqs, "Glass Bottle\nUncut Opal\nUncut Diamond\nTropical Fish", 7, 90, 25, 0,

                generateItemLore(AlchemistItems.LESSER_POTION_SLAYING), false, false, false);
        createMenuItem(forgeMenu, pl, 13, Material.POTION, AlchemistItems.MINOR_POTION_HEALING
                        .getDisplayableItem().getDisplayName(), healthPotReqs, "Glass Bottle\nUncut Ruby\nSalmon", 5, 35, 30, 0,

                generateItemLore(AlchemistItems.MINOR_POTION_HEALING), false, false, false);
        createMenuItem(forgeMenu, pl, 14, Material.POTION, AlchemistItems.MINOR_POTION_MANA
                        .getDisplayableItem().getDisplayName(), manaPotReqs, "Glass Bottle\nUncut Sapphire\nCod", 8, 60, 30, 0,

                generateItemLore(AlchemistItems.MINOR_POTION_MANA), false, false, false);
        createMenuItem(forgeMenu, pl, 15, Material.POTION, AlchemistItems.MINOR_POTION_SLAYING
                        .getDisplayableItem().getDisplayName(), slayPotReqs, "Glass Bottle\nUncut Opal\nUncut Diamond\nTropical Fish", 7, 90, 30, 0,

                generateItemLore(AlchemistItems.MINOR_POTION_SLAYING), false, false, false);
        createMenuItem(forgeMenu, pl, 16, Material.POTION, AlchemistItems.MINOR_POTION_LOOTING
                        .getDisplayableItem().getDisplayName(), lootPotReqs, "Glass Bottle\nAmbrosia Root\nPufferfish", 4, 600, 40, 0,

                generateItemLore(AlchemistItems.MINOR_POTION_LOOTING), false, true, false);
        createMenuItem(forgeMenu, pl, 17, Material.POTION, AlchemistItems.MAJOR_POTION_HEALING
                        .getDisplayableItem().getDisplayName(), healthPotReqs, "Glass Bottle\nUncut Ruby\nSalmon", 5, 35, 0, 0,

                generateItemLore(AlchemistItems.MAJOR_POTION_HEALING), false, false, false);
        createMenuItem(forgeMenu, pl, 18, Material.POTION, AlchemistItems.MAJOR_POTION_MANA
                        .getDisplayableItem().getDisplayName(), manaPotReqs, "Glass Bottle\nUncut Sapphire\nCod", 8, 60, 10, 0,

                generateItemLore(AlchemistItems.MAJOR_POTION_MANA), false, false, false);
        createMenuItem(forgeMenu, pl, 19, Material.POTION, AlchemistItems.MAJOR_POTION_SLAYING
                        .getDisplayableItem().getDisplayName(), slayPotReqs, "Glass Bottle\nUncut Opal\nUncut Diamond\nTropical Fish", 7, 90, 25, 0,

                generateItemLore(AlchemistItems.MAJOR_POTION_SLAYING), false, false, false);
        createMenuItem(forgeMenu, pl, 20, Material.POTION, AlchemistItems.MAJOR_POTION_LOOTING
                        .getDisplayableItem().getDisplayName(), lootPotReqs, "Glass Bottle\nAmbrosia Root\nPufferfish", 4, 600, 40, 0,

                generateItemLore(AlchemistItems.MAJOR_POTION_LOOTING), false, true, false);
        createMenuItem(forgeMenu, pl, 21, Material.POTION, AlchemistItems.MINOR_POTION_HEALING
                        .getDisplayableItem().getDisplayName(), healthPotReqs, "Glass Bottle\nUncut Ruby\nSalmon", 5, 35, 0, 0,

                generateItemLore(AlchemistItems.MINOR_POTION_HEALING), false, false, false);
        createMenuItem(forgeMenu, pl, 22, Material.POTION, AlchemistItems.MINOR_POTION_MANA
                        .getDisplayableItem().getDisplayName(), manaPotReqs, "Glass Bottle\nUncut Sapphire\nCod", 8, 60, 10, 0,

                generateItemLore(AlchemistItems.MINOR_POTION_MANA), false, false, false);
        createMenuItem(forgeMenu, pl, 23, Material.POTION, AlchemistItems.MINOR_POTION_SLAYING
                        .getDisplayableItem().getDisplayName(), slayPotReqs, "Glass Bottle\nUncut Opal\nUncut Diamond\nTropical Fish", 7, 90, 25, 0,

                generateItemLore(AlchemistItems.MINOR_POTION_SLAYING), false, false, false);
        createMenuItem(forgeMenu, pl, 24, Material.POTION, AlchemistItems.MINOR_POTION_LOOTING
                        .getDisplayableItem().getDisplayName(), lootPotReqs, "Glass Bottle\nAmbrosia Root\nPufferfish", 4, 600, 40, 0,

                generateItemLore(AlchemistItems.MINOR_POTION_LOOTING), false, true, false);
        LinkedHashMap<Material, Integer> firePotReqs = new LinkedHashMap<>();
        firePotReqs.put(Material.GLASS_BOTTLE, 1);
        firePotReqs.put(Material.NETHER_WART, 1);
        firePotReqs.put(Material.PUFFERFISH, 1);
        createMenuItem(forgeMenu, pl, 25, Material.POTION, AlchemistItems.POTION_SACRED_FIRE
                        .getDisplayableItem().getDisplayName(), firePotReqs, "Glass Bottle\nSacred Flame\nPufferfish", 4, 0, 60, 0,

                generateItemLore(AlchemistItems.POTION_SACRED_FIRE), false, false, false);
    }

    /**
     * This...
     *
     * @param player
     * @param amt
     * @param rate
     * @param slot
     */
    @Override
    public void produceResult(Player player, int amt, int rate, int slot) {
        ItemStack itemStack = determineItem(slot);
        produceResult(player, amt, rate, itemStack);
    }

    private ItemStack determineItem(int slot) {
        switch (slot) {
            case 9:
                return AlchemistItems.BOTTLE_ITEMSTACK;
            case 10:
                return AlchemistItems.LESSER_POTION_HEALING_ITEMSTACK;
            case 11:
                return AlchemistItems.LESSER_POTION_MANA_ITEMSTACK;
            case 12:
                return AlchemistItems.LESSER_POTION_SLAYING_ITEMSTACK;
            case 13:
                return AlchemistItems.MINOR_POTION_HEALING_ITEMSTACK;
            case 14:
                return AlchemistItems.MINOR_POTION_MANA_ITEMSTACK;
            case 15:
                return AlchemistItems.MINOR_POTION_SLAYING_ITEMSTACK;
            case 16:
                return AlchemistItems.MINOR_POTION_LOOTING_ITEMSTACK;
            case 17:
                return AlchemistItems.MAJOR_POTION_HEALING_ITEMSTACK;
            case 18:
                return AlchemistItems.MAJOR_POTION_MANA_ITEMSTACK;
            case 19:
                return AlchemistItems.MAJOR_POTION_SLAYING_ITEMSTACK;
            case 20:
                return AlchemistItems.MAJOR_POTION_LOOTING_ITEMSTACK;
            case 21:
                return AlchemistItems.GREATER_POTION_HEALING_ITEMSTACK;
            case 22:
                return AlchemistItems.GREATER_POTION_MANA_ITEMSTACK;
            case 23:
                return AlchemistItems.GREATER_POTION_SLAYING_ITEMSTACK;
            case 24:
                return AlchemistItems.GREATER_POTION_LOOTING_ITEMSTACK;
            case 25:
                return AlchemistItems.POTION_SACRED_FIRE_ITEMSTACK;
        }
        return new ItemStack(Material.STONE);
    }
}
