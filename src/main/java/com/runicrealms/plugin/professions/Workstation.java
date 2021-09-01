package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.professions.listeners.WorkstationListener;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.utilities.ColorUtil;
import com.runicrealms.plugin.utilities.FloatingItemUtil;
import com.runicrealms.runicitems.Stat;
import com.runicrealms.runicitems.item.*;
import com.runicrealms.runicitems.item.stats.RunicItemStatRange;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
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

    public static ItemStack potionItem(Color color, String displayName, String description) {

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
        int currentLvl = RunicCoreAPI.getPlayerCache(pl).getProfLevel();

        // determine the success rate, based on level
        int rate = (40 + currentLvl);

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
                desc.append("&eNext upgrade: Lv. 30\n");
            } else if (currentLvl < 50) {
                desc.append("&eNext Upgrade: Lv. 50\n");
            } else if (currentLvl < 60) {
                desc.append("&eNext Upgrade: Lv. 60\n");
            }
        }

        if (currentLvl < reqLevel) {
            desc.append("&cReq. Lv: ").append(reqLevel).append("\n");
        }

        if (!itemStats.equals("")) {
            desc.append("\n&7Item Info:\n").append(itemStats);//.append("\n")
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
            Color color;
            if (name.toLowerCase().contains("healing")) {
                color = Color.RED;
            } else if (name.toLowerCase().contains("mana")) {
                color = Color.AQUA;
            } else if (name.toLowerCase().contains("slaying")) {
                color = Color.GREEN;
            } else if (name.toLowerCase().contains("looting")) {
                color = Color.FUCHSIA;
            } else {
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
                                 Material craftedItemType, int currentLvl, int exp, int durability,
                                 Particle particle, Sound soundCraft, Sound soundDone, int eventSlot, int numOfItems) {

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
            int amt = itemReqs.get(reagent) * numOfItems;
            if (itemReqs.size() <= 1) {
                amt = itemAmt * numOfItems;
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
            int amt = itemReqs.get(reagent) * numOfItems;
            if (itemReqs.size() <= 1) {
                amt = itemAmt * numOfItems;
            }
            ItemRemover.takeItem(pl, reagent, amt);
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
                        ProfExpUtil.giveExperience(pl, exp * numOfItems, true);
                    }
                    produceResult(pl, numOfItems, rate, eventSlot);
                } else {
                    pl.playSound(pl.getLocation(), soundCraft, 0.5f, 2.0f);
                    pl.spawnParticle(particle, stationLoc, 5, 0.25, 0.25, 0.25, 0.01);
                    count = count + 1;
                }
            }
        }.runTaskTimer(RunicProfessions.getInstance(), 0, 20);
    }

    /**
     * This..
     *
     * @param pl
     * @param amt
     * @param rate
     * @param itemStack
     */
    protected void produceResult(Player pl, int amt, int rate, ItemStack itemStack) {
        int failCount = 0;
        for (int i = 0; i < amt; i++) {
            double chance = ThreadLocalRandom.current().nextDouble(0.0D, 100.0D);
            if (chance <= rate) {
                if (pl.getInventory().firstEmpty() != -1) {
                    int firstEmpty = pl.getInventory().firstEmpty();
                    pl.getInventory().setItem(firstEmpty, itemStack);
                } else {
                    pl.getWorld().dropItem(pl.getLocation(), itemStack);
                }
            } else {
                failCount++;
            }
        }
        if (failCount == 0)
            return;
        pl.sendMessage(ChatColor.RED + "You fail to craft this item. [x" + failCount + "]");
    }

    /**
     * This...
     *
     * @param paramPlayer
     * @param paramInt1
     * @param paramInt2
     * @param paramInt3
     */
    protected abstract void produceResult(Player paramPlayer, int paramInt1, int paramInt2, int paramInt3);

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

    public String generateItemLore(RunicItem item) {
        if (item instanceof RunicItemGeneric) {
            return this.generateGenericItemLore((RunicItemGeneric) item);
        }

        if (item instanceof RunicItemArmor) {
            return this.generateArmorItemLore(((RunicItemArmor) item));
        }

        if (item instanceof RunicItemWeapon) {
            return this.generateWeaponItemLore(((RunicItemWeapon) item));
        }

        if (item instanceof RunicItemOffhand) {
            return this.generateOffhandItemLore(((RunicItemOffhand) item));
        }

        return null;
    }

    private String generateGenericItemLore(RunicItemGeneric item) {
        String lore = "";

        for (String s : item.getLore()) {
            lore = lore.concat(s) + "\n";
        }

        return lore;
    }

    private String generateArmorItemLore(RunicItemArmor item) {
        String stats = "";

        stats = stats.concat(ChatColor.RED + "" + item.getHealth() + "❤\n");
        for (Stat stat : item.getStats().keySet()) {
            stats = stats.concat(stat.getChatColor() + "+" + item.getStats().get(stat).getValue() + stat.getIcon() + "\n");
        }

        return stats;
    }

    private String generateWeaponItemLore(RunicItemWeapon item) {
        String stats = "";

        RunicItemStatRange range = item.getWeaponDamage();
        stats = stats.concat(ChatColor.RED + "" + range.getMin() + "-" + range.getMax() + " DMG\n");
        for (Stat stat : item.getStats().keySet()) {
            stats = stats.concat(stat.getChatColor() + "+" + item.getStats().get(stat).getValue() + stat.getIcon() + "\n");
        }

        return stats;
    }

    private String generateOffhandItemLore(RunicItemOffhand item) {
        String stats = "";

        for (Stat stat : item.getStats().keySet()) {
            stats = stats.concat(stat.getChatColor() + "+" + item.getStats().get(stat).getValue() + stat.getIcon() + "\n");
        }

        return stats;
    }
}
