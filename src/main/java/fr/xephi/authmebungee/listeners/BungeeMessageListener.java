package fr.xephi.authmebungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.xephi.authmebungee.services.AuthPlayerManager;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.inject.Inject;

public class BungeeMessageListener implements Listener {

    private AuthPlayerManager authPlayerManager;

    @Inject
    public BungeeMessageListener(AuthPlayerManager authPlayerManager) {
        this.authPlayerManager = authPlayerManager;
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

        if (!in.readUTF().equals("AuthMe")) {
            return;
        }
        // Now that's sure, it's for us, so let's go
        event.setCancelled(true);

        // For now that's the only type of message the server is able to receive
        String type = in.readUTF();
        switch (type) {
            case "login":
                authPlayerManager.getAuthPlayer(in.readUTF()).setLogged(true);
                break;
            case "logout":
                authPlayerManager.getAuthPlayer(in.readUTF()).setLogged(false);
                break;
        }
    }

}
