package fr.xephi.authmebungee.listeners;

import fr.xephi.authmebungee.services.AuthPlayerManager;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

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

        // Check if the message is for us
        if (!event.getTag().equals("BungeeCord")) {
            return;
        }

        // Check if a player is not trying to rip us off sending a fake message
        if (!(event.getSender() instanceof Server)) {
            return;
        }

        try {
            // Read the plugin message
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

            if (!in.readUTF().equals("AuthMe")) {
                return;
            }

            // Now that's sure, it's for us, so let's go
            event.setCancelled(true);

            // For now that's the only type of message the server is able to receive
            String task = in.readUTF();

            switch (task) {
                case "login":
                    authPlayerManager.getAuthPlayer(in.readUTF()).setLogged(true);
                    break;
                case "logout":
                    authPlayerManager.getAuthPlayer(in.readUTF()).setLogged(false);
                    break;
            }
        } catch (IOException ex) {
            // Something nasty happened
            ex.printStackTrace();
        }
    }
}
