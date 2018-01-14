package fr.xephi.authmebungee.commands;

import ch.jalu.configme.SettingsManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

import javax.inject.Inject;

public class BungeeReloadCommand extends Command {

    private SettingsManager settings;

    @Inject
    public BungeeReloadCommand(SettingsManager settings) {
        super("abreloadproxy", "authmebungee.reload");
        this.settings = settings;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        settings.reload();
        commandSender.sendMessage(
            new ComponentBuilder("AuthMeBungee configuration reloaded!").color(ChatColor.GREEN).create()
        );
    }

}
