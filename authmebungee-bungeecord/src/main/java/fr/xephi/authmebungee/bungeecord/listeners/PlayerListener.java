package fr.xephi.authmebungee.bungeecord.listeners;

import fr.xephi.authmebungee.bungeecord.services.PluginMessageSender;
import fr.xephi.authmebungee.bungeecord.config.ConfigProperties;
import fr.xephi.authmebungee.bungeecord.config.Settings;
import fr.xephi.authmebungee.bungeecord.config.SettingsDependent;
import fr.xephi.authmebungee.bungeecord.data.AuthPlayer;
import fr.xephi.authmebungee.bungeecord.services.AuthPlayerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class PlayerListener implements Listener, SettingsDependent {

    // Services
    private AuthPlayerManager authPlayerManager;
    private PluginMessageSender pluginMessageSender;

    // Settings
    private boolean isAutoLogin;
    private boolean isServerSwitchRequiresAuth;
    private String requiresAuthKickMessage;
    private List<String> authServers;
    private boolean isCommandsRequireAuth;
    private List<String> commandWhitelist;
    private boolean chatRequiresAuth;

    @Inject
    PlayerListener(Settings settings, AuthPlayerManager authPlayerManager, PluginMessageSender pluginMessageSender) {
        this.authPlayerManager = authPlayerManager;
        this.pluginMessageSender = pluginMessageSender;
        reload(settings);
    }

    @Override
    public void reload(Settings settings) {
        isAutoLogin = settings.getProperty(ConfigProperties.AUTOLOGIN);
        isServerSwitchRequiresAuth = settings.getProperty(ConfigProperties.SERVER_SWITCH_REQUIRES_AUTH);
        requiresAuthKickMessage = settings.getProperty(ConfigProperties.SERVER_SWITCH_KICK_MESSAGE);
        authServers = settings.getProperty(ConfigProperties.AUTH_SERVERS);
        isCommandsRequireAuth = settings.getProperty(ConfigProperties.COMMANDS_REQUIRE_AUTH);
        commandWhitelist = settings.getProperty(ConfigProperties.COMMANDS_WHITELIST);
        chatRequiresAuth = settings.getProperty(ConfigProperties.CHAT_REQUIRES_AUTH);
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

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(player);

        // Player is trying to switch server (also called on first server player connection)
        if (authPlayer.isLogged()) {
            // If player is logged in and autoLogin is enabled, send login signal to the bukkit side
            if (isAutoLogin) {
                try {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(bout);

                    out.writeUTF("AUTOLOGIN:");
                    out.writeUTF(authPlayer.getName());

                    // Not using async as bungeecord already use multiple threads for player connections
                    pluginMessageSender.sendData(event.getPlayer().getServer(), bout.toByteArray(), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // If player is not logged in and serverSwitchRequiresAuth is enabled, kick player
            if (!isServerSwitchRequiresAuth) {
                return;
            }

            String server = event.getPlayer().getServer().getInfo().getName();
            if (!authServers.contains(server)) {
                TextComponent kickReason = new TextComponent(requiresAuthKickMessage);
                kickReason.setColor(ChatColor.RED);
                event.getPlayer().disconnect(kickReason);
            }
        }
    }
}
