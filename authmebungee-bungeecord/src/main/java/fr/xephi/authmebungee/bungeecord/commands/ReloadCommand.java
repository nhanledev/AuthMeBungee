package fr.xephi.authmebungee.bungeecord.commands;

import fr.xephi.authmebungee.bungeecord.config.Settings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

import javax.inject.Inject;

public class ReloadCommand extends Command {

    @Inject
    private Settings settings;

    public ReloadCommand() {
        super("abreload", "authmebungee.reload");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        settings.reload();
        commandSender.sendMessage(
            new ComponentBuilder("AuthMeBungee configuration reloaded!").color(ChatColor.GREEN).create()
        );
    }
}
