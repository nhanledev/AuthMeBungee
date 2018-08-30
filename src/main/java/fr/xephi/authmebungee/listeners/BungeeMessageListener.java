package fr.xephi.authmebungee.listeners;

import ch.jalu.configme.SettingsManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.xephi.authmebungee.config.BungeeConfigProperties;
import fr.xephi.authmebungee.config.SettingsDependent;
import fr.xephi.authmebungee.data.AuthPlayer;
import fr.xephi.authmebungee.services.AuthPlayerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.inject.Inject;

public class BungeeMessageListener implements Listener, SettingsDependent {

    // Services
    private AuthPlayerManager authPlayerManager;

    // Settings
    private boolean isSendOnLogoutEnabled;
    private String sendOnLogoutTarget;

    @Inject
    public BungeeMessageListener(SettingsManager settings, AuthPlayerManager authPlayerManager) {
        this.authPlayerManager = authPlayerManager;
        reload(settings);
    }

    @Override
    public void reload(SettingsManager settings) {
        isSendOnLogoutEnabled = settings.getProperty(BungeeConfigProperties.ENABLE_SEND_ON_LOGOUT);
        sendOnLogoutTarget = settings.getProperty(BungeeConfigProperties.SEND_ON_LOGOUT_TARGET);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        // Check if the message is for a server (ignore client messages)
        if (!event.getTag().equals("BungeeCord")) {
            return;
        }

        // Check if a player is not trying to send us a fake message
        if (!(event.getSender() instanceof Server)) {
            return;
        }

        // Read the plugin message
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

        // We expect only broadcast messages
        if (!in.readUTF().equals("FORWARD")) {
            return;
        }
        if (!in.readUTF().equals("ALL")) {
            return;
        }
        // Let's check the subchannel
        if (!in.readUTF().equals("AuthMe")) {
            return;
        }
        // Now that's sure, it's for us, so let's go
        event.setCancelled(true);

        // For now that's the only type of message the server is able to receive
        String type = in.readUTF();
        switch (type) {
            case "login":
                handleOnLogin(in);
                break;
            case "logout":
                handleOnLogout(in);
                break;
        }
    }

    private void handleOnLogin(ByteArrayDataInput in) {
        String name = in.readUTF();
        AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(name);
        if (authPlayer != null) {
            authPlayer.setLogged(true);
        }
    }

    private void handleOnLogout(ByteArrayDataInput in) {
        String name = in.readUTF();
        AuthPlayer authPlayer = authPlayerManager.getAuthPlayer(name);
        if (authPlayer != null) {
            authPlayer.setLogged(false);
            if (isSendOnLogoutEnabled) {
                ProxiedPlayer player = authPlayer.getPlayer();
                if (player != null) {
                    player.connect(ProxyServer.getInstance().getServerInfo(sendOnLogoutTarget));
                }
            }
        }
    }

}
