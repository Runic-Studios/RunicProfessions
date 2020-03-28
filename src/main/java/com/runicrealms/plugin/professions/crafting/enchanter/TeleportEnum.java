package com.runicrealms.plugin.professions.crafting.enchanter;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public enum TeleportEnum {

    AZANA("Azana", azanaWarp()),
    WINTERVALE("Wintervale", wintervaleWarp()),
    ZENYTH("Zenyth", zenythWarp()),
    FROSTS_END("Frost's End", frostsEndWarp());

    private String name;
    private Location location;

    TeleportEnum(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public static TeleportEnum getEnum(String value) {
        for(TeleportEnum teleportEnum : values()) {
            if (teleportEnum.getName().equalsIgnoreCase(value)) {
                return teleportEnum;
            }
        }
        throw new IllegalArgumentException();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    private static Location azanaWarp() {
        return new Location(Bukkit.getWorld("Alterra"), -825.5, 38, 167.5, 180, 0);
    }

    private static Location wintervaleWarp() {
        return new Location(Bukkit.getWorld("Alterra"), -1672.5, 37, -2639.5, 90, 0);
    }

    private static Location zenythWarp() {
        return new Location(Bukkit.getWorld("Alterra"), 1564.5, 38, -158.5, 180, 0);
    }

    private static Location frostsEndWarp() {
        return new Location(Bukkit.getWorld("Alterra"), 1027.5, 32, 2558.5, 0, 0);
    }
}
