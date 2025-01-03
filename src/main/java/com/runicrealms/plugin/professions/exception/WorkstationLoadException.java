package com.runicrealms.plugin.professions.exception;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * This exception helps to debug when shops fail to initialize from yml files on startup
 */
public class WorkstationLoadException extends Exception {

    private final List<String> messages = new ArrayList<>();
    private String error = null;

    public WorkstationLoadException(String... messages) {
        Collections.addAll(this.messages, messages);
    }

    public void addMessage(String... messages) {
        Collections.addAll(this.messages, messages);
    }

    public void displayToConsole() {
        Bukkit.getLogger().log(Level.SEVERE, this.getMessage());
        if (this.error != null) {
            Bukkit.getLogger().log(Level.WARNING, this.error);
        }
    }

    public void displayToOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.RED + this.getMessage());
            if (this.error != null) {
                player.sendMessage(ChatColor.RED + "Check the console for a detailed WorkstationLoadException error trace.");
            }
        }
    }

    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        for (int i = this.messages.size() - 1; i >= 0; i--) {
            builder.append(this.messages.get(i));
            if (i != 0) {
                builder.append(" -> ");
            }
        }
        return builder.toString();
    }

    public WorkstationLoadException setErrorMessage(String message) {
        error = message;
        return this;
    }

}
