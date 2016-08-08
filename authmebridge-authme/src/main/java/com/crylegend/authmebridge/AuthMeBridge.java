package com.crylegend.authmebridge;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import fr.xephi.authme.api.NewAPI;

public class AuthMeBridge extends JavaPlugin implements PluginMessageListener {
    private final static String incomingChannel = "BAuthMeBridge";
    private final static String outgoingChannel = "AuthMeBridge";

    private Logger log;
    private String autoLoginMessage;
    private NewAPI authme;

    protected String getOutgoingChannel() {
        return outgoingChannel;
    }

    @Override
    public void onEnable() {
        // Setup logger
        log = getLogger();

        // Get server instances
        Server server = getServer();
        PluginManager pm = server.getPluginManager();
        Messenger messenger = server.getMessenger();

        // Check authme
        if (!pm.isPluginEnabled("AuthMe")) {
            log.warning("AuthMe not found, disabling");
            setEnabled(false);
            return;
        }
        authme = NewAPI.getInstance();

        // Config
        saveDefaultConfig();
        autoLoginMessage = getConfig().getString("autoLoginMessage", "&2Your session has been resumed by the bridge.");

        pm.registerEvents(new AuthMeBridgeListener(this), this);
        messenger.registerIncomingPluginChannel(this, incomingChannel, this);
        messenger.registerOutgoingPluginChannel(this, outgoingChannel);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] message) {
        if(!channel.equals(incomingChannel)) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        String subchannel = in.readUTF();
        if (!subchannel.equals("AutoLogin")) {
            return;
        }

        String name = in.readUTF();
        Player player = Bukkit.getPlayerExact(name);
        if(player == null) {
            return;
        }

        if (authme.isAuthenticated(player)) {
            return;
        }
        authme.forceLogin(player);
        if (!autoLoginMessage.isEmpty()) {
            player.sendMessage(autoLoginMessage);
        }
    }
}
