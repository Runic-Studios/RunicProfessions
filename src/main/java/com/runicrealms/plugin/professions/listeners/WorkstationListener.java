package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.api.event.ChatChannelMessageEvent;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.api.RunicCoreAPI;
import com.runicrealms.plugin.item.GUIMenu.ItemGUI;
import com.runicrealms.plugin.professions.crafting.alchemist.CauldronMenu;
import com.runicrealms.plugin.professions.crafting.blacksmith.AnvilMenu;
import com.runicrealms.plugin.professions.crafting.blacksmith.FurnaceMenu;
import com.runicrealms.plugin.professions.crafting.cooking.CookingMenu;
import com.runicrealms.plugin.professions.crafting.enchanter.EnchantingTableMenu;
import com.runicrealms.plugin.professions.crafting.enchanter.ShrineMenu;
import com.runicrealms.plugin.professions.crafting.hunter.HunterMenu;
import com.runicrealms.plugin.professions.crafting.jeweler.JewelerMenu;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class WorkstationListener implements Listener {

    private static final HashMap<UUID, Location> CURRENT_WORKSTATION = new HashMap<>();
    private final HashMap<UUID, Location> chatters;
    private final HashMap<Location, String> storedStationLocations;

    public WorkstationListener() {
        chatters = new HashMap<>();
        storedStationLocations = new HashMap<>();

        // retrieve the data file
        File workstations = new File(Bukkit.getServer().getPluginManager().getPlugin("RunicProfessions").getDataFolder(),
                "workstations.yml");
        FileConfiguration stationConfig = YamlConfiguration.loadConfiguration(workstations);
        ConfigurationSection stationLocs = stationConfig.getConfigurationSection("Workstations.Locations");

        if (stationLocs == null) return;

        /*
        Iterate through all workstations and add them to memory
         */
        for (String stationID : stationLocs.getKeys(false)) {
            World savedWorld = Bukkit.getServer().getWorld(stationLocs.getString(stationID + ".world"));
            double savedX = stationLocs.getDouble(stationID + ".x");
            double savedY = stationLocs.getDouble(stationID + ".y");
            double savedZ = stationLocs.getDouble(stationID + ".z");
            Location stationLocation = new Location(savedWorld, savedX, savedY, savedZ);
            String stationType = stationLocs.getString(stationID + ".type");
            storedStationLocations.put(stationLocation, stationType);
        }
    }

    public static HashMap<UUID, Location> getCurrentWorkstation() {
        return CURRENT_WORKSTATION;
    }

    @EventHandler(priority = EventPriority.LOWEST) // first
    public void onOpenInventory(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!e.hasBlock()) return;
        if (e.getClickedBlock() == null) return;
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        Location blockLocation = block.getLocation();
        if (storedStationLocations.containsKey(blockLocation)) {
            e.setCancelled(true);
            tryOpenGUI(player, block, storedStationLocations.get(blockLocation));
        }
    }

    /**
     * This...
     *
     * @param player
     * @param block
     * @param stationType
     */
    private void tryOpenGUI(Player player, Block block, String stationType) {

        UUID uuid = player.getUniqueId();

        // determine the player's profession
        String className = RunicCoreAPI.getPlayerCache(player).getProfName();

        // stop the listener if the player is already crafting
        if (RunicProfessions.getProfManager().getCurrentCrafters().contains(player)) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
            player.sendMessage(ChatColor.RED + "You are currently crafting.");
            return;
        }

        // check which stationType is being called
        switch (stationType) {
            case "anvil":
                if (className.equals("Blacksmith")) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 1.0f);
                    RunicProfessions.getProfManager().setPlayerWorkstation(player, new AnvilMenu(player));
                    ItemGUI bMenu = ((RunicProfessions.getProfManager().getPlayerWorkstation(player))).getItemGUI();
                    bMenu.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "A blacksmith would know how to use this.");
                }
                break;
            case "cauldron":
                if (className.equals("Alchemist")) {
                    player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 0.5f, 0.25f);
                    RunicProfessions.getProfManager().setPlayerWorkstation(player, new CauldronMenu(player));
                    ItemGUI cMenu = ((RunicProfessions.getProfManager().getPlayerWorkstation(player))).getItemGUI();
                    cMenu.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "An alchemist would know how to use this.");
                }
                break;
            case "cooking fire":
                player.playSound(player.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5f, 0.5f);
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.5f);
                RunicProfessions.getProfManager().setPlayerWorkstation(player, new CookingMenu(player));
                ItemGUI cMenu = ((RunicProfessions.getProfManager().getPlayerWorkstation(player))).getItemGUI();
                cMenu.open(player);
                break;
            case "furnace":
                if (className.equals("Blacksmith")) {
                    player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.5f);
                    player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL_LAVA, 0.5f, 1.0f);
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 0.5f, 1.0f);
                    RunicProfessions.getProfManager().setPlayerWorkstation(player, new FurnaceMenu(player));
                    ItemGUI fMenu = ((RunicProfessions.getProfManager().getPlayerWorkstation(player))).getItemGUI();
                    fMenu.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "A blacksmith would know how to use this.");
                }
                break;
            case "enchanting table":
            case "spinning wheel":
                if (className.equals("Enchanter")) {
                    player.playSound(player.getLocation(), Sound.BLOCK_WET_GRASS_BREAK, 2.0f, 1.2f);
                    RunicProfessions.getProfManager().setPlayerWorkstation(player, new EnchantingTableMenu(player));
                    ItemGUI eMenu = ((RunicProfessions.getProfManager().getPlayerWorkstation(player))).getItemGUI();
                    eMenu.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "An enchanter would know how to use this.");
                }
                break;
            case "gemcutting bench":
                if (className.equals("Jeweler")) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5f, 2.0f);
                    RunicProfessions.getProfManager().setPlayerWorkstation(player, new JewelerMenu(player));
                    ItemGUI jMenu = ((RunicProfessions.getProfManager().getPlayerWorkstation(player))).getItemGUI();
                    jMenu.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "A jeweler would know how to use this.");
                }
                break;
            case "hunting board":
            case "tanning rack":
                if (className.equals("Hunter")) {
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 2.0f, 0.8f);
                    RunicProfessions.getProfManager().setPlayerWorkstation(player, new HunterMenu(player));
                    ItemGUI hMenu = ((RunicProfessions.getProfManager().getPlayerWorkstation(player))).getItemGUI();
                    hMenu.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "A hunter would know how to use this.");
                }
                break;
            case "shrine":
                if (className.equals("Enchanter")) {
                    //play sound
                    RunicProfessions.getProfManager().setPlayerWorkstation(player, new ShrineMenu(player));
                    ItemGUI eMenu = ((RunicProfessions.getProfManager().getPlayerWorkstation(player))).getItemGUI();
                    eMenu.open(player);
                }
                break;
        }

        // add station location to the hashmap for item display purposes
        Location loc = block.getLocation().add(0.5, 1.0, 0.5);
        CURRENT_WORKSTATION.remove(uuid);
        CURRENT_WORKSTATION.put(uuid, loc);
    }

    /**
     * This event adds a new workstation to the file, so long as the player is opped and holding a green wool.
     * The event then listens for the player's chat response, and adds the block to the file accordingly.
     */
    @EventHandler
    public void onLocationAdd(PlayerInteractEvent e) {

        Player player = e.getPlayer();
        if (!player.isOp()) return;

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) return;
        Material heldItemType = player.getInventory().getItemInMainHand().getType();
        if (heldItemType != Material.GREEN_WOOL) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!e.hasBlock()) return;
        if (e.getClickedBlock() == null) return;
        Block block = e.getClickedBlock();
        Location blockLocation = block.getLocation();

        // retrieve the data file
        File workstations = new File(Bukkit.getServer().getPluginManager().getPlugin("RunicProfessions").getDataFolder(),
                "workstations.yml");
        FileConfiguration stationConfig = YamlConfiguration.loadConfiguration(workstations);

        // save data file
        try {
            stationConfig.save(workstations);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (!stationConfig.isSet("Workstations.NEXT_ID")) {
            stationConfig.set("Workstations.NEXT_ID", 0);
        }
        int nextID = stationConfig.getInt("Workstations.NEXT_ID");
        stationConfig.set("Workstations.Locations." + nextID + ".world", block.getWorld().getName());
        stationConfig.set("Workstations.Locations." + nextID + ".x", blockLocation.getBlockX());
        stationConfig.set("Workstations.Locations." + nextID + ".y", blockLocation.getBlockY());
        stationConfig.set("Workstations.Locations." + nextID + ".z", blockLocation.getBlockZ());
        player.sendMessage(ChatColor.GREEN + "Workstation saved! Now please specify the type of this workstation:\n"
                + "Anvil, cauldron, cooking fire, furnace, gemcutting bench, enchanting table, shrine, or hunting board?");
        chatters.put(player.getUniqueId(), blockLocation);

        // save data file
        try {
            stationConfig.save(workstations);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatChannelMessageEvent e) {

        Player player = e.getMessageSender();
        if (!chatters.containsKey(player.getUniqueId())) return;
        e.setCancelled(true);

        // retrieve chat message
        String stationType = e.getChatMessage().toLowerCase();

        // verify input
        if (!(stationType.equals("anvil")
                || stationType.equals("cauldron")
                || stationType.equals("cooking fire")
                || stationType.equals("furnace")
                || stationType.equals("gemcutting bench")
                || stationType.equals("enchanting table")
                || stationType.equals("hunting board")
                || stationType.equals("shrine"))) {
            player.sendMessage(ChatColor.RED + "Please specify a correct input.");
            return;
        }

        // retrieve the data file
        File workstations = new File(Bukkit.getServer().getPluginManager().getPlugin("RunicProfessions").getDataFolder(),
                "workstations.yml");
        FileConfiguration stationConfig = YamlConfiguration.loadConfiguration(workstations);

        if (!stationConfig.isSet("Workstations.NEXT_ID")) {
            stationConfig.set("Workstations.NEXT_ID", 0);
        }
        int nextID = stationConfig.getInt("Workstations.NEXT_ID");
        stationConfig.set("Workstations.Locations." + nextID + ".type", stationType);
        stationConfig.set("Workstations.NEXT_ID", nextID + 1);
        player.sendMessage(ChatColor.GREEN + "Workstation type set to: " + ChatColor.YELLOW + stationType);

        // add workstation to memory
        storedStationLocations.put(chatters.get(player.getUniqueId()), stationType);
        chatters.remove(player.getUniqueId());

        // save data file
        try {
            stationConfig.save(workstations);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}