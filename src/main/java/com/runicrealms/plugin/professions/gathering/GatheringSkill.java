package com.runicrealms.plugin.professions.gathering;

import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

public enum GatheringSkill {

    COOKING(20, "cooking", ""),
    FARMING(22, "farming", "You need a farming hoe to do that!"),
    FISHING(24, "fishing", "You need a fishing rod to do that!"),
    HARVESTING(29, "harvesting", ""),
    MINING(31, "mining", "You need a mining pickaxe to do that!"),
    WOODCUTTING(33, "woodcutting", "You need a woodcutting axe to do that!");

    private final int menuSlot;
    private final String identifier;
    private final String noToolMessage;

    GatheringSkill(int menuSlot, String identifier, String noToolMessage) {
        this.menuSlot = menuSlot;
        this.identifier = identifier;
        this.noToolMessage = noToolMessage;
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

    public int getMenuSlot() {
        return menuSlot;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFormattedIdentifier() {
        return identifier.substring(0, 1).toUpperCase() + identifier.substring(1);
    }

    public String getNoToolMessage() {
        return ChatColor.RED + noToolMessage;
    }
}
