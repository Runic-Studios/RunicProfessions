package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.item.shops.RunicItemShopManager;
import com.runicrealms.plugin.item.util.ItemRemover;
import com.runicrealms.plugin.professions.api.RunicProfessionsAPI;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.listeners.WorkstationListener;
import com.runicrealms.plugin.professions.utilities.ProfExpUtil;
import com.runicrealms.plugin.utilities.ColorUtil;
import com.runicrealms.plugin.utilities.FloatingItemUtil;
import com.runicrealms.runicitems.RunicItemsAPI;
import com.runicrealms.runicitems.Stat;
import com.runicrealms.runicitems.item.*;
import com.runicrealms.runicitems.item.stats.GemBonus;
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
        }, RunicProfessions.getInstance());
    }

    /**
     * Creates the visual menu item inside a crafting menu
     *
     * @param gui             the associated gui to place the menu item inside
     * @param player          the player who opened the menu
     * @param craftedResource the crafting resource associated w/ the menu item
     * @param slot            the slot to place the menu item in
     * @param durability      the durability of the menu item
     */
    protected void createMenuItem(ItemGUI gui, Player player, CraftedResource craftedResource, int slot, int... durability) {

        // grab the player's current profession level, progress toward that level
        int currentLvl;
        if (craftedResource.getProfession() == Profession.COOKING)
            currentLvl = RunicProfessionsAPI.getGatherPlayer(player.getUniqueId()).getCookingLevel();
        else
            currentLvl = RunicCoreAPI.getPlayerCache(player).getProfLevel();
        int reqLevel = craftedResource.getRequiredLevel();
        int exp = craftedResource.getExperience();
        LinkedHashMap<ItemStack, Integer> reagents = craftedResource.getReagents();
        String itemStats = generateItemLore(craftedResource.getRunicItem());

        // build the menu display
        StringBuilder desc = new StringBuilder();

        if (currentLvl < reqLevel) {
            desc.append("&cLv. Min ").append(reqLevel).append("\n");
        }

        if (!itemStats.equals("")) {
            desc.append("\n&7Item Info:\n").append(itemStats);
        }
        desc.append("\n&7Material(s) Required:\n");

        // add every item in the reagent's key set with its associated amount.
        // if there is only one reagent in the key set, it uses the 'itemAmt' field instead.
        for (ItemStack itemStack : reagents.keySet()) {
            assert itemStack.getItemMeta() != null;
            int amt = reagents.get(itemStack);
            if (RunicItemShopManager.hasItems(player, itemStack, amt)) {
                desc.append("&a").append(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName())).append("&7, &f").append(amt).append("\n");
            } else {
                desc.append("&c").append(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName())).append("&7, &f").append(amt).append("\n");
            }
        }

        desc.append("\n")
                .append(ChatColor.WHITE).append("Left Click ")
                .append(ChatColor.DARK_GRAY).append("to craft\n")
                .append(ChatColor.WHITE).append("Right Click ")
                .append(ChatColor.DARK_GRAY).append("to craft 5");

        if (exp > 0) {
            desc.append("\n\n&7&oRewards &f&o").append(exp).append(" &7&oexperience");
        }

        desc = new StringBuilder(ColorUtil.format(desc.toString()));

        RunicItem runicItem = craftedResource.getRunicItem();
        ChatColor prefix = ChatColor.WHITE;
        if (runicItem instanceof RunicItemArmor) {
            prefix = ((RunicItemArmor) runicItem).getRarity().getChatColor();
        } else if (runicItem instanceof RunicItemOffhand) {
            prefix = ((RunicItemOffhand) runicItem).getRarity().getChatColor();
        } else if (runicItem instanceof RunicItemWeapon) {
            prefix = ((RunicItemWeapon) runicItem).getRarity().getChatColor();
        }
        String name = prefix + runicItem.getDisplayableItem().getDisplayName();
        if (craftedResource.getItemStack().getType() == Material.POTION) {
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
                    name, desc.toString(), durability.length > 0 ? durability[0] : 0, false);
        } else {
            gui.setOption(slot, new ItemStack(craftedResource.getItemStack().getType()),
                    name, desc.toString(), durability.length > 0 ? durability[0] : 0, false);
        }
    }

    /**
     * Begins the crafting process to create a CraftedResource
     *
     * @param player          who initiated crafting
     * @param craftedResource to be created
     * @param durability      the durability of the menu item
     * @param particle        the particle to display while crafting
     * @param soundCraft      the sound to display while crafting
     * @param soundDone       the sound to display when done crafting
     * @param numOfItems      the number of items to craft
     * @param isCooking       whether the activity is cooking
     */
    protected void startCrafting(Player player, CraftedResource craftedResource, int durability, Particle particle,
                                 Sound soundCraft, Sound soundDone, int numOfItems, boolean isCooking) {

        if (RunicProfessions.getProfManager().getCurrentCrafters().contains(player)) return;
        int currentLvl;
        if (craftedResource.getProfession() == Profession.COOKING)
            currentLvl = RunicProfessionsAPI.getGatherPlayer(player.getUniqueId()).getCookingLevel();
        else
            currentLvl = RunicCoreAPI.getPlayerCache(player).getProfLevel();
        int reqLevel = craftedResource.getRequiredLevel();
        int exp = craftedResource.getExperience();
        LinkedHashMap<ItemStack, Integer> reagents = craftedResource.getReagents();
        Material craftedItemType = craftedResource.getItemStack().getType();

        // grab the location of the anvil
        Location stationLoc = WorkstationListener.getCurrentWorkstation().get(player.getUniqueId());

        // check that the player has reached the req. lv
        if (currentLvl < reqLevel) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
            player.sendMessage(ChatColor.RED + "You haven't learned to craft this yet!");
            return;
        }

        // check that the player has the reagents
        for (ItemStack itemStack : reagents.keySet()) {
            int amt = reagents.get(itemStack) * numOfItems;
            if (!RunicItemShopManager.hasItems(player, itemStack, amt)) {
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
                player.sendMessage(ChatColor.RED + "You don't have the items to craft this!");
                return;
            }
        }

        // check that the player has an open inventory space
        if (player.getInventory().firstEmpty() == -1 && craftedItemType.getMaxStackSize() == 1) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
            player.sendMessage(ChatColor.RED + "You don't have any inventory space!");
            return;
        }

        // check that the player has an open inventory space (if the item is stackable)
        ItemStack[] inv = player.getInventory().getContents();
        if (player.getInventory().firstEmpty() == -1 && craftedItemType.getMaxStackSize() != 1) {
            for (int i = 0; i < inv.length; i++) {
                if (player.getInventory().getItem(i) == null) continue;
                if (Objects.requireNonNull(player.getInventory().getItem(i)).getType() == craftedItemType
                        && Objects.requireNonNull(player.getInventory().getItem(i)).getAmount() + 1 > craftedItemType.getMaxStackSize()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
                    player.sendMessage(ChatColor.RED + "You don't have any inventory space!");
                    return;
                }
            }
        }

        // if player has everything, take player's items, display first reagent visually
        // add player to currently crafting ArrayList
        RunicProfessions.getProfManager().getCurrentCrafters().add(player);
        player.sendMessage(ChatColor.GRAY + "Crafting...");
        for (ItemStack itemStack : reagents.keySet()) {
            int amt = reagents.get(itemStack) * numOfItems;
            ItemRemover.takeItem(player, itemStack, amt);
        }

        // spawn item on workstation for visual
        FloatingItemUtil.spawnFloatingItem(player, stationLoc, craftedItemType, 4, durability);

        // start the crafting process
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count > 3) {
                    this.cancel();
                    RunicProfessions.getProfManager().getCurrentCrafters().remove(player);
                    player.playSound(player.getLocation(), soundDone, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.GREEN + "Done!");
                    if (exp > 0) {
                        if (!isCooking)
                            ProfExpUtil.giveCraftingExperience(player, exp * numOfItems);
                        else
                            ProfExpUtil.giveGatheringExperience(player, GatheringSkill.COOKING, exp * numOfItems);
                    }
                    produceResult(player, numOfItems, craftedResource.getItemStack());
                } else {
                    player.playSound(player.getLocation(), soundCraft, 0.5f, 2.0f);
                    player.spawnParticle(particle, stationLoc, 5, 0.25, 0.25, 0.25, 0.01);
                    count = count + 1;
                }
            }
        }.runTaskTimer(RunicProfessions.getInstance(), 0, 20);
    }


    /**
     * Generic produce result method which generates an ItemStack
     * Outdated and should be replaced, as it does not play well with RunicItems
     *
     * @param player        the player in the workstation
     * @param numberOfItems the number of items they're crafting
     * @param itemStack     the item to be built and dropped
     */
    protected void produceResult(Player player, int numberOfItems, ItemStack itemStack) {
        for (int i = 0; i < numberOfItems; i++) {
            RunicItemsAPI.addItem(player.getInventory(), itemStack, true); // prevents anti-dupe from triggering
        }
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

    /**
     * Generates correct item lore to display in crafting menu based on type of runic item
     *
     * @param item the parent class runic item
     * @return a string that has correct fields based on child class
     */
    public String generateItemLore(RunicItem item) {
        if (item instanceof RunicItemGeneric) {
            return this.generateGenericItemLore((RunicItemGeneric) item);
        }

        if (item instanceof RunicItemGem) {
            return this.generateGemItemLore((RunicItemGem) item);
        }

        if (item instanceof RunicItemArmor) {
            return this.generateArmorItemLore((RunicItemArmor) item);
        }

        if (item instanceof RunicItemWeapon) {
            return this.generateWeaponItemLore((RunicItemWeapon) item);
        }

        if (item instanceof RunicItemOffhand) {
            return this.generateOffhandItemLore((RunicItemOffhand) item);
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

    private String generateGemItemLore(RunicItemGem item) {
        GemBonus gemBonus = item.getBonus();
        return
                ChatColor.YELLOW + "Primary Stat: " +
                        gemBonus.getMainStat().getChatColor() +
                        gemBonus.getMainStat().getPrefix() +
                        gemBonus.getMainStat().getIcon() + "\n";
    }

    private String generateArmorItemLore(RunicItemArmor item) {
        String stats = "";

        stats = stats.concat(ChatColor.RED + "" + item.getHealth() + "❤\n");
        for (Stat stat : item.getStats().keySet()) {
            stats = stats.concat
                    (
                            stat.getChatColor() + "+" + item.getStats().get(stat).getRange().getMin() +
                                    "-" + item.getStats().get(stat).getRange().getMax() + stat.getIcon() + "\n"
                    );
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
            stats = stats.concat
                    (
                            stat.getChatColor() + "+" + item.getStats().get(stat).getRange().getMin() +
                                    "-" + item.getStats().get(stat).getRange().getMax() + stat.getIcon() + "\n"
                    );
        }

        return stats;
    }
}
