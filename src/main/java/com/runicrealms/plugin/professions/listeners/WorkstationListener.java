package com.runicrealms.plugin.professions.listeners;

import com.runicrealms.api.event.ChatChannelMessageEvent;
import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.plugin.professions.Profession;
import com.runicrealms.plugin.professions.crafting.WoodworkingMenu;
import com.runicrealms.plugin.professions.crafting.WoodworkingMenuTutorial;
import com.runicrealms.plugin.professions.crafting.alchemist.CauldronMenu;
import com.runicrealms.plugin.professions.crafting.blacksmith.AnvilMenu;
import com.runicrealms.plugin.professions.crafting.blacksmith.FurnaceMenu;
import com.runicrealms.plugin.professions.crafting.cooking.CookingMenu;
import com.runicrealms.plugin.professions.crafting.jeweler.JewelerMenu;
import com.runicrealms.plugin.rdb.RunicDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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

    public WorkstationListener() {
        chatters = new HashMap<>();
    }

    public static HashMap<UUID, Location> getCurrentWorkstation() {
        return CURRENT_WORKSTATION;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatChannelMessageEvent event) {

        Player player = event.getMessageSender();
        if (!chatters.containsKey(player.getUniqueId())) return;
        event.setCancelled(true);

        // retrieve chat message
        String stationType = event.getChatMessage().toLowerCase();

        // verify input
        if (!(stationType.equals("anvil")
                || stationType.equals("cauldron")
                || stationType.equals("cooking fire")
                || stationType.equals("furnace")
                || stationType.equals("gemcutting bench")
                || stationType.equals("woodworking table")
                || stationType.equals("tutorial woodworking table"))) {
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
        RunicProfessions.getAPI().getStoredStationLocations().put(chatters.get(player.getUniqueId()), stationType);
        chatters.remove(player.getUniqueId());

        // save data file
        try {
            stationConfig.save(workstations);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This event adds a new workstation to the file, so long as the player is opped and holding a green wool.
     * The event then listens for the player's chat response, and adds the block to the file accordingly.
     */
    @EventHandler
    public void onLocationAdd(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        if (!player.isOp()) return;

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) return;
        Material heldItemType = player.getInventory().getItemInMainHand().getType();
        if (heldItemType != Material.GREEN_WOOL) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.hasBlock()) return;
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
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
                + "Anvil, cauldron, cooking fire, furnace, gemcutting bench, woodworking table?");
        chatters.put(player.getUniqueId(), blockLocation);

        // save data file
        try {
            stationConfig.save(workstations);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks for a workstation whenever a player interacts with a block
     */
    @EventHandler(priority = EventPriority.LOWEST) // first
    public void onOpenInventory(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.hasBlock()) return;
        if (event.getClickedBlock() == null) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Location blockLocation = block.getLocation();
        if (RunicProfessions.getAPI().getStoredStationLocations().containsKey(blockLocation)) {
            event.setCancelled(true);
            tryOpenGUI(player, block, RunicProfessions.getAPI().getStoredStationLocations().get(blockLocation));
        }
    }

    /**
     * This attempts to open the ui for a workstation
     *
     * @param player      trying to open the ui
     * @param block       the actual block being clicked
     * @param stationType what kind of workstation to be opened
     */
    private void tryOpenGUI(Player player, Block block, String stationType) {

        UUID uuid = player.getUniqueId();

        // determine the player's profession
        int slot = RunicDatabase.getAPI().getCharacterAPI().getCharacterSlot(player.getUniqueId());
        Profession profession = RunicProfessions.getAPI().getPlayerProfession(player.getUniqueId(), slot);

        // stop the listener if the player is already crafting
        if (RunicProfessions.getAPI().getCurrentCrafters().contains(player)) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
            player.sendMessage(ChatColor.RED + "You are currently crafting.");
            return;
        }

        // check which stationType is being called
        switch (stationType.toLowerCase()) {
            case "anvil" -> {
                if (profession == Profession.BLACKSMITH) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 1.0f);
                    RunicProfessions.getAPI().setPlayerWorkstation(player, new AnvilMenu(player));
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "A blacksmith would know how to use this.");
                }
            }
            case "cauldron" -> {
                if (profession == Profession.ALCHEMIST) {
                    player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 0.5f, 0.25f);
                    RunicProfessions.getAPI().setPlayerWorkstation(player, new CauldronMenu(player));
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "An alchemist would know how to use this.");
                }
            }
            case "cooking fire" -> {
                player.playSound(player.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5f, 0.5f);
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.5f);
                RunicProfessions.getAPI().setPlayerWorkstation(player, new CookingMenu(player));
            }
            case "furnace" -> {
                if (profession == Profession.BLACKSMITH) {
                    player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.5f);
                    player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL_LAVA, 0.5f, 1.0f);
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 0.5f, 1.0f);
                    RunicProfessions.getAPI().setPlayerWorkstation(player, new FurnaceMenu(player));
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "A blacksmith would know how to use this.");
                }
            }
            case "gemcutting bench" -> {
                if (profession == Profession.JEWELER) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5f, 2.0f);
                    RunicProfessions.getAPI().setPlayerWorkstation(player, new JewelerMenu(player));
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                    player.sendMessage(ChatColor.RED + "A jeweler would know how to use this.");
                }
            }
            case "woodworking table" -> {
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.5f, 0.5f);
                player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_WOOD_BREAK, 0.5f, 1.5f);
                RunicProfessions.getAPI().setPlayerWorkstation(player, new WoodworkingMenu(player));
            }
            case "tutorial woodworking table" -> {
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.5f, 0.5f);
                player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_WOOD_BREAK, 0.5f, 1.5f);
                RunicProfessions.getAPI().setPlayerWorkstation(player, new WoodworkingMenuTutorial(player));
            }
        }

        // add station location to the hashmap for item display purposes
        Location loc = block.getLocation().add(0.5, 1.0, 0.5);
        CURRENT_WORKSTATION.remove(uuid);
        CURRENT_WORKSTATION.put(uuid, loc);
    }
}