package fr.xephi.authmebungee.bungeecord.listeners;

import ch.jalu.configme.SettingsManager;
import fr.xephi.authmebungee.bungeecord.config.BungeeConfigProperties;
import fr.xephi.authmebungee.bungeecord.data.AuthPlayer;
import fr.xephi.authmebungee.bungeecord.services.AuthPlayerManager;
import fr.xephi.authmebungee.bungeecord.services.BungeeMessageSender;
import fr.xephi.authmebungee.common.config.SettingsDependent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class BungeePlayerListener implements Listener, SettingsDependent {

    // Services
    private AuthPlayerManager authPlayerManager;
    private BungeeMessageSender pluginMessageSender;

    // Settings
    private boolean isAutoLogin;
    private boolean isServerSwitchRequiresAuth;
    private String requiresAuthKickMessage;
    private List<String> authServers;
    private boolean isCommandsRequireAuth;
    private List<String> commandWhitelist;
    private boolean chatRequiresAuth;

    @Inject
    BungeePlayerListener(SettingsManager settings, AuthPlayerManager authPlayerManager, BungeeMessageSender pluginMessageSender) {
        this.authPlayerManager = authPlayerManager;
        this.pluginMessageSender = pluginMessageSender;
        reload(settings);
    }

    @Override
    public void reload(SettingsManager settings) {
        isAutoLogin = settings.getProperty(BungeeConfigProperties.AUTOLOGIN);
        isServerSwitchRequiresAuth = settings.getProperty(BungeeConfigProperties.SERVER_SWITCH_REQUIRES_AUTH);
        requiresAuthKickMessage = settings.getProperty(BungeeConfigProperties.SERVER_SWITCH_KICK_MESSAGE);
        authServers = settings.getProperty(BungeeConfigProperties.AUTH_SERVERS);
        isCommandsRequireAuth = settings.getProperty(BungeeConfigProperties.COMMANDS_REQUIRE_AUTH);
        commandWhitelist = settings.getProperty(BungeeConfigProperties.COMMANDS_WHITELIST);
        chatRequiresAuth = settings.getProperty(BungeeConfigProperties.CHAT_REQUIRES_AUTH);
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        // Register player in our list
        authPlayerManager.addAuthPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        // Remove player from out list
        authPlayerManager.removeAuthPlayer(event.getPlayer());
    }

    // Priority is set to lowest to keep compatibility with some chat plugins
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        // Check if it's a player
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        if (event.isCommand()) {
            if (!isCommandsRequireAuth) {
                return;
            }

            // Check if command is a whitelisted command
            String label = event.getMessage().split(" ")[0];
            if (commandWhitelist.contains(label)) {
                return;
            }
        } else if (!chatRequiresAuth) {
            return;
        }

        // If player is not logged in, cancel the event
        if (authPlayerManager.getAuthPlayer((ProxiedPlayer) event.getSender()).isLogged()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnect(ServerConnectEvent event) {
        if (!isServerSwitchRequiresAuth || event.isCancelled()) {
            return;
        }

        ProxiedPlayer player = event.getPlayer();
        AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        if (authPlayer.isLogged()) {
            return;
        }

        // If player is not logged in and serverSwitchRequiresAuth is enabled, cancel the connection
        String server = event.getTarget().getName();
        if (!authServers.contains(server)) {
            event.setCancelled(true);
            TextComponent reasonMessage = new TextComponent(requiresAuthKickMessage);
            reasonMessage.setColor(ChatColor.RED);
            player.sendMessage(reasonMessage);
        }
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        if(!isAutoLogin) {
            return;
        }

        ProxiedPlayer player = event.getPlayer();
        AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);

        // Player is trying to switch server (also called on first server player connection)
        if (authPlayer.isLogged()) {
            // If player is logged in and autoLogin is enabled, send login signal to the spigot side
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bout);

                out.writeUTF("AuthMeBungee");
                out.writeUTF("AutoLogin");
                out.writeUTF(authPlayer.getName());

                // Not using async as bungeecord already use multiple threads for player connections
                pluginMessageSender.sendData(event.getPlayer().getServer(), bout.toByteArray(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
