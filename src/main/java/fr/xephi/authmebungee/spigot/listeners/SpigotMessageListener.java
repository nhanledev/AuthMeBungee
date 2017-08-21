package fr.xephi.authmebungee.spigot.listeners;

import ch.jalu.configme.SettingsManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authmebungee.common.config.SettingsDependent;
import fr.xephi.authmebungee.spigot.config.SpigotConfigProperties;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.inject.Inject;

public class SpigotMessageListener implements PluginMessageListener, SettingsDependent {

    private Server server;
    private AuthMeApi authMeApi;

    private String autoLoginMessage;

    @Inject
    public SpigotMessageListener(Server server, SettingsManager settings, AuthMeApi authMeApi) {
        this.server = server;
        this.authMeApi = authMeApi;
        reload(settings);
    }

    @Override
    public void reload(SettingsManager settings) {
        autoLoginMessage = ChatColor.translateAlternateColorCodes('&', settings.getProperty(SpigotConfigProperties.AUTOLOGIN_MESSAGE));
    }

    @Override
    public void onPluginMessageReceived(String channel, Player connection, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        if (!(in.readUTF().equals("AuthMeBungee") && in.readUTF().equals("AutoLogin"))) {
            return;
        }

        String name = in.readUTF();
        Player player = server.getPlayerExact(name);
        if (player == null) {
            return;
        }

        if (authMeApi.isAuthenticated(player)) {
            return;
        }

        authMeApi.forceLogin(player);
        if (!autoLoginMessage.isEmpty()) {
            player.sendMessage(autoLoginMessage);
        }
    }
}
