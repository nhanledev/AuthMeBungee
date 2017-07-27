package fr.xephi.authmebungee.spigot.commands;

import ch.jalu.configme.SettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class SpigotReloadCommand implements CommandExecutor {

    private SettingsManager settings;

    @Inject
    SpigotReloadCommand(SettingsManager settings) {
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        settings.reload();
        sender.sendMessage(ChatColor.GREEN + "AuthMeBungee configuration reloaded!");
        return true;
    }
}
