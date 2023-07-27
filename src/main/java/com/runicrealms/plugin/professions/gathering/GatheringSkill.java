package com.runicrealms.plugin.professions.gathering;

import com.runicrealms.plugin.runicitems.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public enum GatheringSkill {
    COOKING(20, 10, "cooking", "", cookingItem()),
    FARMING(22, 2, "farming", "You need a farming hoe to do that!", farmingItem()),
    FISHING(24, 10, "fishing", "You need a fishing rod to do that!", fishingItem()),
    HARVESTING(29, 10, "harvesting", "", harvestingItem()),
    MINING(31, 10, "mining", "You need a mining pickaxe to do that!", miningItem()),
    WOODCUTTING(33, 10, "woodcutting", "You need a woodcutting axe to do that!", woodcuttingItem());

    private final int menuSlot;
    private final double combatExpMultiplier;
    private final String identifier;
    private final String noToolMessage;
    private final ItemStack menuItem;

    GatheringSkill(int menuSlot, double combatExpMultiplier, String identifier, String noToolMessage, ItemStack menuItem) {
        this.menuSlot = menuSlot;
        this.combatExpMultiplier = combatExpMultiplier;
        this.identifier = identifier;
        this.noToolMessage = noToolMessage;
        this.menuItem = menuItem;
    }

    public static GatheringSkill getFromIdentifier(String identifier) {
        for (GatheringSkill gatheringSkill : GatheringSkill.values()) {
            if (gatheringSkill.getIdentifier().equals(identifier))
                return gatheringSkill;
        }
        return null;
    }

    /**
     * Obtains a GatheringSkill enum value based on the given inventory slot
     *
     * @param menuSlot the e.getRawSlot or e.getSlot from the GUI menu
     * @return a GatheringSkill enum value
     */
    public static GatheringSkill getFromMenuSlot(int menuSlot) {
        for (GatheringSkill gatheringSkill : GatheringSkill.values()) {
            if (gatheringSkill.getMenuSlot() == menuSlot)
                return gatheringSkill;
        }
        return null;
    }

    /**
     * Creates a static set of slots to populate GUI menus
     *
     * @return a set of Integer wrappers
     */
    public static Set<Integer> getGatheringSkillMenuSlots() {
        Set<Integer> gatheringSkillMenuSlots = new HashSet<>();
        for (GatheringSkill gatheringSkill : GatheringSkill.values()) {
            gatheringSkillMenuSlots.add(gatheringSkill.getMenuSlot());
        }
        return gatheringSkillMenuSlots;
    }

    private static ItemStack cookingItem() {
        return ItemUtils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3R" +
                "leHR1cmUvZDNlMjBhMjZjYmI1NzQwYTE1OGRhOTkxZWY5NGRjZDMyZDQ0N2U5YWMwM2FhMGU4ZjgyOWE0OTgzMDYxOWExMCJ9fX0=");
    }

    private static ItemStack farmingItem() {
        return ItemUtils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3R" +
                "leHR1cmUvMzVmNzViYWUxMzQ5NmI5N2Y1NWRjNDJmYzM3MjQyYTg4MTU4OTUyYjZjY2U5M2MwNTdhYjAyOGFjYmE4MGIyMCJ9fX0=");
    }

    private static ItemStack fishingItem() {
        return ItemUtils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3R" +
                "leHR1cmUvODVlYWY4N2NmYmMyM2NjZmNkYWUwZmM4ZGY4NDc3MTFhNmJlMDRiYjNjZWFmODBlMjcxZmRlZGZkMjUzNWU4In19fQ==");
    }

    private static ItemStack harvestingItem() {
        return ItemUtils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3R" +
                "leHR1cmUvYmYxOTI2OTZiZjRjNzkwMWIzNjE2MjQ1MTA0NzEzNjhlMDE2NDI3NTk3NTY3MTgzMjdmNDhhYjI1YzUwMjY1In19fQ==");
    }

    private static ItemStack miningItem() {
        return ItemUtils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3R" +
                "leHR1cmUvMjNkNjM2YjA0Zjk1N2ExZGVhMmJhYzRhNzgzOTVmNzFhNTM4ZmJlMTMxMDgyNDdiMWU4YWI4YmQwYmE0YTlkNyJ9fX0=");
    }

    private static ItemStack woodcuttingItem() {
        return ItemUtils.getHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3R" +
                "leHR1cmUvNWFlODI2ZTdkYjg0NDdmYmQ2Mjk4OGZlZTBlODNiYmRkNjk0Mzc4YWVmMTJkMjU3MmU5NzVmMDU5YTU0OTkwIn19fQ==");
    }

    public double getCombatExpMultiplier() {
        return combatExpMultiplier;
    }

    public String getFormattedIdentifier() {
        return identifier.substring(0, 1).toUpperCase() + identifier.substring(1);
    }

    public String getIdentifier() {
        return identifier;
    }

    public ItemStack getMenuItem() {
        return menuItem;
    }

    public int getMenuSlot() {
        return menuSlot;
    }

    public String getNoToolMessage() {
        return ChatColor.RED + noToolMessage;
    }
}
