package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.attributes.AttributeUtil;
import com.runicrealms.plugin.enums.ArmorSlotEnum;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.LoreGenerator;
import com.runicrealms.plugin.professions.listeners.WorkstationListener;
import com.runicrealms.plugin.professions.utilities.FloatingItemUtil;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.utilities.ColorUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Basic workstation class with some handy methods
 */
public abstract class Workstation implements Listener {

    private String title;
    private ItemGUI itemGUI;

    public Workstation() {
        this.title = "";
        this.itemGUI = new ItemGUI();
    }

    public Workstation(String title) {
        this.title = title;
        this.itemGUI = new ItemGUI();
    }

    protected void setupWorkstation(Player pl) {
        this.title = "&f&l" + pl.getName() + "'s &e&lWorkstation";
        this.itemGUI = new ItemGUI(this.title, 9, event -> {
        },
                RunicProfessions.getInstance()).setOption(5, new ItemStack(Material.BARRIER),
                "&cClose", "&7Close the menu", 0, false);
    }

    protected void setupWorkstation(String title) {
        this.title = title;
        this.itemGUI = new ItemGUI(this.title, 9, event -> {
        },
                RunicProfessions.getInstance()).setOption(5, new ItemStack(Material.BARRIER),
                "&cClose", "&7Close the menu", 0, false);
    }

    protected ItemGUI craftingMenu(Player pl, int size) {

        return new ItemGUI("&f&l" + pl.getName() + "'s Crafting Menu", size, event -> {
        },
                RunicProfessions.getInstance());
    }

    protected void createMenuItem(ItemGUI gui, Player pl, int slot, Material itemType, String name,
                                  LinkedHashMap<Material, Integer> itemReqs, String reqsToString, int itemAmt,
                                  int exp, int reqLevel, int durability, String itemStats, boolean cantFail,
                                  boolean canUpgrade, boolean isGlowing) {

        // grab the player's current profession level, progress toward that level
        int currentLvl = RunicProfessions.getInstance().getConfig().getInt(pl.getUniqueId() + ".info.prof.level");

        // determine the success rate, based on level
        int rate = (40+currentLvl);
        if (itemType == Material.POTION) {
            rate = (50+currentLvl);
        }
        String rateToStr;
        if (rate < 50) {
            rateToStr = ChatColor.RED + "" + rate;
        } else if (rate < 80) {
            rateToStr = ChatColor.YELLOW + "" + rate;
        } else {
            rateToStr = ChatColor.GREEN + "" + rate;
        }

        if (cantFail) {
            rateToStr = ChatColor.GREEN + "100";
        }

        // build the menu display
        StringBuilder desc = new StringBuilder();
        if (canUpgrade) {
            if (currentLvl < 30) {
                desc.append("&eNext upgrade at lv. 30!\n");
            } else if (currentLvl < 50) {
                desc.append("&eNext upgrade at lv. 50!\n");
            }
        }

        if (currentLvl < reqLevel) {
            desc.append("&cUnlock by reaching lv. ").append(reqLevel).append("!\n");
        }

        if (!itemStats.equals("")) {
            desc.append("\n&7Item Stats:\n").append(itemStats).append("\n");
        }
        desc.append("\n&7Material(s) Required:\n");

        String[] reqsAsList = reqsToString.split("\n");

        // add every item in the reagents keyset with its associated amount.
        // if there is only one reagent in the keyset, it uses the 'itemAmt' field instead.
        int i = 0;

            for (Material reagent : itemReqs.keySet()) {
                int amt = itemReqs.get(reagent);
                if (reqsAsList.length <= 1) {
                    amt = itemAmt;
                }
                if (pl.getInventory().contains(reagent, amt)) {
                    desc.append("&a").append(reqsAsList[i]).append("&7, &f").append(amt).append("\n");
                } else {
                    desc.append("&c").append(reqsAsList[i]).append("&7, &f").append(amt).append("\n");
                }
                i += 1;
            }


            desc.append("\n&7Success Rate:\n")
                    .append(rateToStr).append("%\n\n")
                    .append(ChatColor.WHITE).append("Left Click ")
                    .append(ChatColor.DARK_GRAY).append("to craft\n")
                    .append(ChatColor.WHITE).append("Right Click ")
                    .append(ChatColor.DARK_GRAY).append("to craft 5");
            if (exp > 0) {
                desc.append("\n\n&7&oRewards &f&o").append(exp).append(" &7&oExperience");
            }

        desc = new StringBuilder(ColorUtil.format(desc.toString()));

        if (itemType == Material.POTION) {
            Color color = Color.WHITE;
            if (name.toLowerCase().contains("healing")) {
                color = Color.RED;
            } else if (name.toLowerCase().contains("mana")) {
                color = Color.AQUA;
            } else if (name.toLowerCase().contains("slaying")) {
                color = Color.BLACK;
            } else if (name.toLowerCase().contains("looting")) {
                color = Color.ORANGE;
            }
            gui.setOption(slot,
                    potionItem(color, name, desc.toString()),
                    name, desc.toString(), durability, isGlowing);
        } else {
            gui.setOption(slot, new ItemStack(itemType),
                    name, desc.toString(), durability, isGlowing);
        }
    }

    protected void startCrafting(Player pl, LinkedHashMap<Material, Integer> itemReqs, int itemAmt, int reqLevel,
                                 Material craftedItemType, String itemName, int currentLvl, int exp, int durability,
                                 Particle particle, Sound soundCraft, Sound soundDone, int health, int multiplier) {

        if (RunicProfessions.getProfManager().getCurrentCrafters().contains(pl)) return;

        // grab the location of the anvil
        Location stationLoc = WorkstationListener.getStationLocation().get(pl.getUniqueId());

        // --------------------------------------
        // fix for tutorial island
        int rate;
        if (currentLvl == 0 || currentLvl == 1) {
            rate = 100;
        } else {
            rate = (40 + currentLvl);
        }
        // --------------------------------------

        // check that the player has reached the req. lv
        if (currentLvl < reqLevel) {
            pl.playSound(pl.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
            pl.sendMessage(ChatColor.RED + "You haven't learned to craft this yet!");
            return;
        }

        // check that the player has the reagents
        for (Material reagent : itemReqs.keySet()) {
            int amt = itemReqs.get(reagent)*multiplier;
            if (itemReqs.size() <= 1) {
                amt = itemAmt*multiplier;
            }
            if (!pl.getInventory().contains(reagent, amt)) {
                pl.playSound(pl.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
                pl.sendMessage(ChatColor.RED + "You don't have the items to craft this!");
                return;
            }
        }

        // check that the player has an open inventory space
        if (pl.getInventory().firstEmpty() == -1 && craftedItemType.getMaxStackSize() == 1) {
            pl.playSound(pl.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
            pl.sendMessage(ChatColor.RED + "You don't have any inventory space!");
            return;
        }

        // check that the player has an open inventory space (if the item is stackable)
        ItemStack[] inv = pl.getInventory().getContents();
        if (pl.getInventory().firstEmpty() == -1 && craftedItemType.getMaxStackSize() != 1) {
            for (int i = 0; i < inv.length; i++) {
                if (pl.getInventory().getItem(i) == null) continue;
                if (Objects.requireNonNull(pl.getInventory().getItem(i)).getType() == craftedItemType
                        && Objects.requireNonNull(pl.getInventory().getItem(i)).getAmount() + 1 > craftedItemType.getMaxStackSize()) {
                    pl.playSound(pl.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
                    pl.sendMessage(ChatColor.RED + "You don't have any inventory space!");
                    return;
                }
            }
        }

        // if player has everything, take player's items, display first reagent visually
        // add player to currently crafting ArrayList
        RunicProfessions.getProfManager().getCurrentCrafters().add(pl);
        pl.sendMessage(ChatColor.GRAY + "Crafting...");
        for (Material reagent : itemReqs.keySet()) {
            int amt = itemReqs.get(reagent)*multiplier;
            if (itemReqs.size() <= 1) {
                amt = itemAmt*multiplier;
            }
            takeItem(pl, reagent, amt);
        }

        // spawn item on workstation for visual
        FloatingItemUtil.spawnFloatingItem(pl, stationLoc, craftedItemType, 4, durability);

        // start the crafting process
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count > 3) {
                    this.cancel();
                    RunicProfessions.getProfManager().getCurrentCrafters().remove(pl);
                    pl.playSound(pl.getLocation(), soundDone, 0.5f, 1.0f);
                    pl.sendMessage(ChatColor.GREEN + "Done!");
                    if (exp > 0) {
                        ProfExpUtil.giveExperience(pl, exp * multiplier);
                    }

                    produceResult(pl, craftedItemType, itemName, currentLvl, multiplier, rate, durability, health);
                } else {
                    pl.playSound(pl.getLocation(), soundCraft, 0.5f, 2.0f);
                    pl.spawnParticle(particle, stationLoc, 5, 0.25, 0.25, 0.25, 0.01);
                    count = count + 1;
                }
            }
        }.runTaskTimer(RunicProfessions.getInstance(), 0, 20);
    }

    protected void produceResult(Player pl, Material material, String dispName,
                            int currentLvl, int amt, int rate, int durability, int someVar) {

        // set our minimum level
        int reqLv = 0;
        if (currentLvl >= 30 && currentLvl < 50) {
            reqLv = 25;
        } else if (currentLvl >= 50) {
            reqLv = 50;
        }

        // create a new item up to the amount
        int failCount = 0;
        for (int i = 0; i < amt; i++) {

            ItemStack craftedItem = new ItemStack(material);
            ItemMeta meta = craftedItem.getItemMeta();
            ((Damageable) Objects.requireNonNull(meta)).setDamage(durability);
            craftedItem.setItemMeta(meta);

            String itemSlot = "";
            ArmorSlotEnum armorType = ArmorSlotEnum.matchSlot(craftedItem);
            switch (armorType) {
                case HELMET:
                    itemSlot = "head";
                    break;
                case CHESTPLATE:
                    itemSlot = "chest";
                    break;
                case LEGGINGS:
                    itemSlot = "legs";
                    break;
                case BOOTS:
                    itemSlot = "feet";
                    break;
            }

            craftedItem = AttributeUtil.addCustomStat(craftedItem, "required.level", reqLv);

            craftedItem = AttributeUtil.addGenericStat
                    (craftedItem, "generic.maxHealth", someVar, itemSlot);
            craftedItem = AttributeUtil.addCustomStat
                    (craftedItem, "custom.manaBoost", someVar);

            // item can be socket-ed ONCE
            craftedItem = AttributeUtil.addCustomStat(craftedItem, "custom.socketCount", 1);

            LoreGenerator.generateItemLore(craftedItem, ChatColor.WHITE, dispName, "");

            double chance = ThreadLocalRandom.current().nextDouble(0, 100);
            if (chance <= rate) {
                // check that the player has an open inventory space
                // this method prevents items from stacking if the player crafts 5
                // todo: let players stack certain items
                if (pl.getInventory().firstEmpty() != -1) {
                    int firstEmpty = pl.getInventory().firstEmpty();
                    pl.getInventory().setItem(firstEmpty, craftedItem);
                } else {
                    pl.getWorld().dropItem(pl.getLocation(), craftedItem);
                }
            } else {
                failCount = failCount + 1;
            }
        }

        // display fail message
        if (failCount == 0) return;
        pl.sendMessage(ChatColor.RED + "You fail to craft this item. [x" + failCount + "]");
    }


    private ItemStack potionItem(Color color, String displayName, String description) {

        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta pMeta = (PotionMeta) potion.getItemMeta();
        Objects.requireNonNull(pMeta).setColor(color);//Color.fromRGB(255,0,180)

        pMeta.setDisplayName(ColorUtil.format(displayName));
        String[] desc = description.split("\n");
        ArrayList<String> lore = new ArrayList<>();
        for (String line : desc) {
            lore.add(ColorUtil.format(line));
        }
        pMeta.setLore(lore);

        pMeta.setUnbreakable(true);
        pMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        pMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        pMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        pMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        potion.setItemMeta(pMeta);
        return potion;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String s) {
        this.title = s;
    }

    public ItemGUI getItemGUI() {
        return this.itemGUI;
    }

    public void setItemGUI(ItemGUI itemGUI) {
        this.itemGUI = itemGUI;
    }

    // todo: move to core. too important
    public static void takeItem(Player pl, Material material, int amount) {

        // todo: add gold pouch calculator
        int to_take = amount;
        for (ItemStack player_item : pl.getInventory().getContents()) {
            if (player_item != null) {

                if (player_item.getType() == material) {
                    int take_next = Math.min(to_take, player_item.getAmount());
                    remove(pl, player_item, take_next);
                    to_take -= take_next;
                    if (to_take <= 0) { //Reached amount. Can stop!
                        break;
                    }
                }
            }
        }
    }
    private static void remove(Player p, ItemStack toR, int amount){
        ItemStack i = toR.clone();
        i.setAmount(amount);
        p.getInventory().removeItem(i);
    }
}
