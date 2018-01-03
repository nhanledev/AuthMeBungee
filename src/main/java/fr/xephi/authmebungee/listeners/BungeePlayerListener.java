package fr.xephi.authmebungee.listeners;

import ch.jalu.configme.SettingsManager;
import fr.xephi.authmebungee.config.BungeeConfigProperties;
import fr.xephi.authmebungee.config.SettingsDependent;
import fr.xephi.authmebungee.data.AuthPlayer;
import fr.xephi.authmebungee.services.AuthPlayerManager;
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
import java.util.ArrayList;
import java.util.List;

public class BungeePlayerListener implements Listener, SettingsDependent {

    // Services
    private AuthPlayerManager authPlayerManager;

    // Settings
    private boolean isAutoLoginEnabled;
    private boolean isServerSwitchRequiresAuth;
    private String requiresAuthKickMessage;
    private List<String> authServers;
    private boolean isCommandsRequireAuth;
    private List<String> commandWhitelist;
    private boolean chatRequiresAuth;

    @Inject
    public BungeePlayerListener(SettingsManager settings, AuthPlayerManager authPlayerManager) {
        this.authPlayerManager = authPlayerManager;
        reload(settings);
    }

    @Override
    public void reload(SettingsManager settings) {
        isAutoLoginEnabled = settings.getProperty(BungeeConfigProperties.AUTOLOGIN);
        isServerSwitchRequiresAuth = settings.getProperty(BungeeConfigProperties.SERVER_SWITCH_REQUIRES_AUTH);
        requiresAuthKickMessage = settings.getProperty(BungeeConfigProperties.SERVER_SWITCH_KICK_MESSAGE);
        authServers = new ArrayList<>();
        for(String server : settings.getProperty(BungeeConfigProperties.AUTH_SERVERS)) {
            authServers.add(server.toLowerCase());
        }
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

    private boolean isInAuthServer(ProxiedPlayer player) {
        return authServers.contains(player.getServer().getInfo().getName().toLowerCase());
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
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        // Filter only auth servers
        if(!isInAuthServer(player)) {
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
        if (authPlayerManager.getAuthPlayer(player).isLogged()) {
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

        // Only check non auth servers
        if(authServers.contains(event.getTarget().getName())) {
            return;
        }

        // Skip logged users
        AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        if (authPlayer.isLogged()) {
            return;
        }

        // If the player is not logged in and serverSwitchRequiresAuth is enabled, cancel the connection
        String server = event.getTarget().getName();
        if (!authServers.contains(server.toLowerCase())) {
            event.setCancelled(true);

            TextComponent reasonMessage = new TextComponent(requiresAuthKickMessage);
            reasonMessage.setColor(ChatColor.RED);

            // Handle race condition on player join on a misconfigured network
            if(player.getServer() == null) {
                player.disconnect(reasonMessage);
            } else {
                player.sendMessage(reasonMessage);
            }
        }
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        if (!isAutoLoginEnabled) {
            return;
        }

        ProxiedPlayer player = event.getPlayer();
        AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        if (!authPlayer.isLogged()) {
            return;
        }

        // If player is logged in and autoLogin is enabled, send login signal to the spigot side
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bout);

            out.writeUTF("AuthMe");
            out.writeUTF("bungeelogin");
            out.writeUTF(authPlayer.getName());

            event.getPlayer().getServer().sendData("BungeeCord", bout.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
