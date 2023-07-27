package com.runicrealms.plugin.professions;

import com.runicrealms.plugin.RunicCore;
import com.runicrealms.plugin.common.util.ColorUtil;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.crafting.CraftedResource;
import com.runicrealms.plugin.professions.gathering.GatheringSkill;
import com.runicrealms.plugin.professions.listeners.WorkstationListener;
import com.runicrealms.plugin.professions.model.GatheringData;
import com.runicrealms.plugin.professions.config.WorkstationLoader;
import com.runicrealms.plugin.professions.event.RunicCraftingExpEvent;
import com.runicrealms.plugin.professions.event.RunicGatheringExpEvent;
import com.runicrealms.plugin.rdb.RunicDatabase;
import com.runicrealms.plugin.utilities.FloatingItemUtil;
import com.runicrealms.plugin.runicitems.RunicItemsAPI;
import com.runicrealms.plugin.runicitems.Stat;
import com.runicrealms.plugin.runicitems.item.RunicItem;
import com.runicrealms.plugin.runicitems.item.RunicItemArmor;
import com.runicrealms.plugin.runicitems.item.RunicItemGem;
import com.runicrealms.plugin.runicitems.item.RunicItemGeneric;
import com.runicrealms.plugin.runicitems.item.RunicItemOffhand;
import com.runicrealms.plugin.runicitems.item.RunicItemWeapon;
import com.runicrealms.plugin.runicitems.item.stats.GemBonus;
import com.runicrealms.plugin.runicitems.item.stats.RunicItemStatRange;
import com.runicrealms.plugin.runicitems.item.stats.RunicItemTag;
import com.runicrealms.plugin.runicitems.util.DataUtil;
import com.runicrealms.plugin.runicitems.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Basic workstation class with some handy methods
 */
public abstract class Workstation implements Listener {
    private final int maxPages;
    private String title;
    private ItemGUI itemGUI;
    private int currentPage;

    public Workstation(int maxPages) {
        this.maxPages = maxPages;
        this.title = "";
        this.itemGUI = new ItemGUI();
        this.currentPage = 1;
    }

    public static ItemStack potionItem(Color color, String displayName, String description) {

        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta pMeta = (PotionMeta) potion.getItemMeta();
        Objects.requireNonNull(pMeta).setColor(color);

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

    protected ItemGUI craftingMenu(Player player, int size) {
        return new ItemGUI("&f&l" + player.getName() + "'s Workstation", size, event -> {
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
        int characterSlot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(player.getUniqueId());
        GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(player.getUniqueId());
        if (craftedResource.getProfession() == Profession.COOKING) {
            currentLvl = gatheringData.getCookingLevel();
        } else {
            currentLvl = RunicProfessions.getAPI().getPlayerProfessionLevel(player.getUniqueId(), characterSlot);
        }
        int reqLevel = craftedResource.getRequiredLevel();
        int exp = craftedResource.getExperience();
        LinkedHashMap<ItemStack, Integer> reagents = craftedResource.getReagents();
        RunicItem runicItem = RunicItemsAPI.generateItemFromTemplate(craftedResource.getTemplateId());
        String itemStats = generateItemLore(runicItem); // RunicItemsAPI.generateItemFromTemplate(craftedResource.getTemplateId()

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
            if (RunicCore.getShopAPI().hasItem(player, itemStack, amt)) {
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
            desc.append("\n\n&7&oRewards &f&o").append(exp).append(" &7&ocrafting experience");
        }

        desc = new StringBuilder(ColorUtil.format(desc.toString()));


        ChatColor prefix = ChatColor.WHITE;
        if (runicItem instanceof RunicItemArmor) {
            prefix = ((RunicItemArmor) runicItem).getRarity().getChatColor();
        } else if (runicItem instanceof RunicItemOffhand) {
            prefix = ((RunicItemOffhand) runicItem).getRarity().getChatColor();
        } else if (runicItem instanceof RunicItemWeapon) {
            prefix = ((RunicItemWeapon) runicItem).getRarity().getChatColor();
        }
        String name = prefix + runicItem.getDisplayableItem().getDisplayName();
        if (runicItem.getTags().contains(RunicItemTag.POTION)) {
            Color color = DataUtil.getColorFromData(runicItem);
            gui.setOption(slot,
                    potionItem(color, name, desc.toString()),
                    name, desc.toString(), durability.length > 0 ? durability[0] : 0, false);
        } else {
            gui.setOption(slot, new ItemStack(runicItem.generateGUIItem()),
                    name, desc.toString(), durability.length > 0 ? durability[0] : 0, false);
        }
    }

    /**
     * Used in a crafting inventory to determine which CraftedResource to make based on item display name
     *
     * @param workstationType type of workstation (Anvil)
     * @param slot            of the item in the ui
     * @return a CraftedResource item wrapper
     */
    public CraftedResource determineCraftedResource(WorkstationType workstationType, int slot) {
        List<CraftedResource> craftedResources = WorkstationLoader.getCraftedResources().get(workstationType);
        for (CraftedResource craftedResource : craftedResources) {
            if (craftedResource.getPage() != this.currentPage) continue;
            if (craftedResource.getSlot() == slot)
                return craftedResource;
        }
        return null;
    }

    private String generateArmorItemLore(RunicItemArmor item) {
        String stats = "";

        stats = stats.concat(ChatColor.RED + String.valueOf(item.getHealth()) + "❤\n");
        for (Stat stat : item.getStats().keySet()) {
            stats = stats.concat
                    (
                            stat.getChatColor() + "+" + item.getStats().get(stat).getRange().getMin() +
                                    "-" + item.getStats().get(stat).getRange().getMax() + stat.getIcon() + "\n"
                    );
        }

        return stats;
    }

    private String generateGemItemLore(RunicItemGem item) {
        GemBonus gemBonus = item.getBonus();
        return
                ChatColor.YELLOW + "Primary Stat: " +
                        gemBonus.getMainStat().getChatColor() +
                        gemBonus.getMainStat().getPrefix() +
                        gemBonus.getMainStat().getIcon() + "\n";
    }

    private String generateGenericItemLore(RunicItemGeneric item) {
        String lore = "";

        for (String s : item.getLore()) {
            lore = lore.concat(s) + "\n";
        }

        return lore;
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

    private String generateWeaponItemLore(RunicItemWeapon item) {
        String stats = "";

        RunicItemStatRange range = item.getWeaponDamage();
        stats = stats.concat(ChatColor.RED + String.valueOf(range.getMin()) + "-" + range.getMax() + " DMG\n");
        for (Stat stat : item.getStats().keySet()) {
            stats = stats.concat(stat.getChatColor() + "+" + item.getStats().get(stat).getValue() + stat.getIcon() + "\n");
        }

        return stats;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public ItemGUI getItemGUI() {
        return this.itemGUI;
    }

    public void setItemGUI(ItemGUI itemGUI) {
        this.itemGUI = itemGUI;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the internal current page to 1, updates the inventory, and opens it
     */
    public void openFirstPage(Player player) {
        this.setCurrentPage(1);
        setupWorkstation(player);
    }

    /**
     * Sets the internal current page to 1 + current, updates the inventory, and opens it
     */
    public void openNextPage(Player player) {
        if ((currentPage + 1) > maxPages) return;
        this.setCurrentPage(currentPage + 1);
        setupWorkstation(player);
    }

    /**
     * Generic produce result method which generates an ItemStack
     * Outdated and should be replaced, as it does not play well with RunicItems
     *
     * @param player          the player in the workstation
     * @param numberOfItems   the number of items they're crafting
     * @param craftedResource the item to be crafted
     */
    protected void produceResult(Player player, int numberOfItems, CraftedResource craftedResource) {
        for (int i = 0; i < numberOfItems; i++) {
            RunicItemsAPI.addItem(player.getInventory(), RunicItemsAPI.generateItemFromTemplate(craftedResource.getTemplateId()).generateItem()); // true prevents anti-dupe from triggering
        }
    }

    /**
     * Creates the contents for the workstation from the yaml loaded resources
     *
     * @param player          to pass in for level checks, etc.
     * @param itemGUI         the ui menu
     * @param workstationType of the ui menu
     */
    protected void setupItems(Player player, ItemGUI itemGUI, WorkstationType workstationType) {
        // Get a collection of crafted resources associated with this ui menu
        Collection<CraftedResource> craftedResources = WorkstationLoader.getCraftedResources().get(workstationType);

        for (CraftedResource craftedResource : craftedResources) {
            if (craftedResource.getPage() != this.currentPage) continue;
            createMenuItem
                    (
                            itemGUI,
                            player,
                            craftedResource,
                            craftedResource.getSlot(),
                            RunicItemsAPI.generateItemFromTemplate(craftedResource.getTemplateId()).getDisplayableItem().getDamage()
                    );
        }
    }

    protected void setupWorkstation(Player player) {
        this.title = "&f&l" + player.getName() + "'s &e&lWorkstation";
        this.itemGUI = new ItemGUI(this.title, 9, event -> {
        }, RunicProfessions.getInstance());
    }

    /**
     * Begins the crafting process to create a ListenerResource
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
        if (RunicProfessions.getAPI().getCurrentCrafters().contains(player)) return;
        GatheringData gatheringData = RunicProfessions.getDataAPI().loadGatheringData(player.getUniqueId());
        int currentLvl;
        int characterSlot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(player.getUniqueId());
        if (craftedResource.getProfession() == Profession.COOKING) {
            currentLvl = gatheringData.getCookingLevel();
        } else {
            currentLvl = RunicProfessions.getAPI().getPlayerProfessionLevel(player.getUniqueId(), characterSlot);
        }
        int reqLevel = craftedResource.getRequiredLevel();
        int exp = craftedResource.getExperience();
        LinkedHashMap<ItemStack, Integer> reagents = craftedResource.getReagents();
        Material craftedItemType = RunicItemsAPI.generateItemFromTemplate(craftedResource.getTemplateId()).getDisplayableItem().getMaterial();

        // Grab the location of the anvil
        Location stationLoc = WorkstationListener.getCurrentWorkstation().get(player.getUniqueId());

        // Check that the player has reached the req. lv
        if (currentLvl < reqLevel) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
            player.sendMessage(ChatColor.RED + "You haven't learned to craft this yet!");
            return;
        }

        // Check that the player has the reagents
        boolean hasReagents = checkReagents(player, reagents, numOfItems);
        if (!hasReagents) return;

        // Check that the player has an open inventory space
        if (player.getInventory().firstEmpty() == -1 && craftedItemType.getMaxStackSize() == 1) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
            player.sendMessage(ChatColor.RED + "You don't have any inventory space!");
            return;
        }

        // Check that the player has an open inventory space (if the item is stackable)
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

        // If player has everything, display first reagent visually
        // Add player to currently crafting ArrayList
        RunicProfessions.getAPI().getCurrentCrafters().add(player);
        player.sendMessage(ChatColor.GRAY + "Crafting...");

        // Spawn item on workstation for visual
        FloatingItemUtil.spawnFloatingItem(player, stationLoc, craftedItemType, 4, durability);

        // Start the crafting process
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count > 3) {
                    this.cancel();
                    // Unblock them from crafting
                    RunicProfessions.getAPI().getCurrentCrafters().remove(player);
                    // Check reagents again
                    boolean hasReagents = checkReagents(player, reagents, numOfItems);
                    if (!hasReagents) return;
                    // Take reagents
                    for (ItemStack itemStack : reagents.keySet()) {
                        int amt = reagents.get(itemStack) * numOfItems;
                        ItemUtils.takeItem(player, itemStack, amt);
                    }
                    // Grant exp and produce result
                    player.playSound(player.getLocation(), soundDone, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.GREEN + "Done!");
                    if (exp > 0) {
                        if (!isCooking)
                            Bukkit.getPluginManager().callEvent(new RunicCraftingExpEvent(exp * numOfItems, true, player));
                        else
                            Bukkit.getPluginManager().callEvent(new RunicGatheringExpEvent(exp * numOfItems, true, player, GatheringSkill.COOKING));
                    }
                    produceResult(player, numOfItems, craftedResource);
                } else {
                    player.playSound(player.getLocation(), soundCraft, 0.5f, 2.0f);
                    player.spawnParticle(particle, stationLoc, 5, 0.25, 0.25, 0.25, 0.01);
                    count = count + 1;
                }
            }
        }.runTaskTimer(RunicProfessions.getInstance(), 0, 20);
    }

    /**
     * Check if the player has all necessary crafting reagents
     *
     * @param player     who is crafting
     * @param reagents   a map of the reagents and their required amounts
     * @param numOfItems a multiplier for how many items to be crafted at once (1 or 5)
     * @return true if they have all items, else false
     */
    private boolean checkReagents(Player player, LinkedHashMap<ItemStack, Integer> reagents, int numOfItems) {
        for (ItemStack itemStack : reagents.keySet()) {
            int amt = reagents.get(itemStack) * numOfItems;
            if (!RunicCore.getShopAPI().hasItem(player, itemStack, amt)) {
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1);
                player.sendMessage(ChatColor.RED + "You don't have the items to craft this!");
                return false;
            }
        }
        return true;
    }
}
