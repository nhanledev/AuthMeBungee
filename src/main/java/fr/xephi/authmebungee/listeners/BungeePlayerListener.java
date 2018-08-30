package fr.xephi.authmebungee.listeners;

import ch.jalu.configme.SettingsManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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
    private final AuthPlayerManager authPlayerManager;

    // Settings
    private boolean isAutoLoginEnabled;
    private boolean isServerSwitchRequiresAuth;
    private String requiresAuthKickMessage;
    private List<String> authServers;
    private boolean isCommandsRequireAuth;
    private List<String> commandWhitelist;
    private boolean chatRequiresAuth;

    @Inject
    public BungeePlayerListener(final SettingsManager settings, final AuthPlayerManager authPlayerManager) {
        this.authPlayerManager = authPlayerManager;
        reload(settings);
    }

    @Override
    public void reload(final SettingsManager settings) {
        isAutoLoginEnabled = settings.getProperty(BungeeConfigProperties.AUTOLOGIN);
        isServerSwitchRequiresAuth = settings.getProperty(BungeeConfigProperties.SERVER_SWITCH_REQUIRES_AUTH);
        requiresAuthKickMessage = settings.getProperty(BungeeConfigProperties.SERVER_SWITCH_KICK_MESSAGE);
        authServers = new ArrayList<>();
        for (final String server : settings.getProperty(BungeeConfigProperties.AUTH_SERVERS)) {
            authServers.add(server.toLowerCase());
        }
        isCommandsRequireAuth = settings.getProperty(BungeeConfigProperties.COMMANDS_REQUIRE_AUTH);
        commandWhitelist = new ArrayList<>();
        for (final String command : settings.getProperty(BungeeConfigProperties.COMMANDS_WHITELIST)) {
            commandWhitelist.add(command.toLowerCase());
        }
        chatRequiresAuth = settings.getProperty(BungeeConfigProperties.CHAT_REQUIRES_AUTH);
    }

    @EventHandler
    public void onPlayerJoin(final PostLoginEvent event) {
        // Register player in our list
        authPlayerManager.addAuthPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        // Remove player from out list
        authPlayerManager.removeAuthPlayer(event.getPlayer());
    }

    private boolean notInAuthServer(final ProxiedPlayer player) {
        return !authServers.contains(player.getServer().getInfo().getName().toLowerCase());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(final ChatEvent event) {
        if (event.isCancelled() || !event.isCommand() || !isCommandsRequireAuth) {
            return;
        }

        // Check if it's a player
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        // filter only unauthenticated players
        final AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        if (authPlayer != null && authPlayer.isLogged()) {
            return;
        }
        // in auth servers
        if (notInAuthServer(player)) {
            return;
        }
        // Check if command is whitelisted command
        if (commandWhitelist.contains(event.getMessage().split(" ")[0].toLowerCase())) {
            return;
        }
        event.setCancelled(true);
    }

    // Priority is set to lowest to keep compatibility with some chat plugins
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(final ChatEvent event) {
        if (event.isCancelled() || event.isCommand()) {
            return;
        }

        // Check if it's a player
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        // filter only unauthenticated players
        final AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        if (authPlayer != null && authPlayer.isLogged()) {
            return;
        }
        // in auth servers
        if (notInAuthServer(player)) {
            return;
        }

        if (!chatRequiresAuth) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConnectToServer(final ServerConnectEvent event) {
        if (!isServerSwitchRequiresAuth || event.isCancelled()) {
            return;
        }

        final ProxiedPlayer player = event.getPlayer();
        final AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);
        final boolean isAuthenticated = authPlayer != null && authPlayer.isLogged();

        // Only check non auth servers
        if (authServers.contains(event.getTarget().getName())) {
            // If AutoLogin enabled, notify the server if this player is authenticated
            if (isAutoLoginEnabled && isAuthenticated) {
                final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("AuthMe.v2");
                out.writeUTF("perform.login");
                out.writeUTF(authPlayer.getName());
                event.getTarget().sendData("BungeeCord", out.toByteArray());
            }
        } else {
            // Skip logged users
            if (isAuthenticated) {
                return;
            }

            // If the player is not logged in and serverSwitchRequiresAuth is enabled, cancel the connection
            event.setCancelled(true);

            final TextComponent reasonMessage = new TextComponent(requiresAuthKickMessage);
            reasonMessage.setColor(ChatColor.RED);

            // Handle race condition on player join on a misconfigured network
            if (player.getServer() == null) {
                player.disconnect(reasonMessage);
            } else {
                player.sendMessage(reasonMessage);
            }
        }
    }

}
