package fr.xephi.authmebungee.bukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.xephi.authme.api.NewAPI;
import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AuthMeBungee extends JavaPlugin implements Listener, PluginMessageListener {
    private final static String INCOMING_CHANNEL = "AuthMeBungeeProxy";
    private final static String OUTGOING_CHANNEL = "AuthMeBungee";

    private static AuthMeBungee instance;
    private NewAPI authme;

    // Settings
    private String autoLoginMessage;

    @Override
    public void onEnable() {
        instance = this;
        authme = NewAPI.getInstance();

        // Config
        saveDefaultConfig();
        autoLoginMessage = getConfig().getString("autoLoginMessage", "&2Your session has been resumed by the bridge.");

        // Register event listener
        getServer().getPluginManager().registerEvents(instance, instance);

        // Register message listener
        Messenger messenger = getServer().getMessenger();
        messenger.registerIncomingPluginChannel(instance, INCOMING_CHANNEL, instance);
        messenger.registerOutgoingPluginChannel(instance, OUTGOING_CHANNEL);
    }

    @EventHandler
    public void onAuthMeLogin(LoginEvent event) {
        final Player player = event.getPlayer();
        if (event.getPlayer() == null) {
            return;
        }

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);

        try {
            out.writeUTF("LOGIN:");
            out.writeUTF(player.getName());
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendPluginMessage(instance, OUTGOING_CHANNEL, bout.toByteArray());
                }
            }.runTaskAsynchronously(instance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onAuthMeLogout(LogoutEvent event) {
        final Player player = event.getPlayer();
        if (event.getPlayer() == null) {
            return;
        }

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);

        try {
            out.writeUTF("LOGOUT:");
            out.writeUTF(player.getName());
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendPluginMessage(instance, OUTGOING_CHANNEL, bout.toByteArray());
                }
            }.runTaskAsynchronously(instance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player ignored, byte[] message) {
        if (!channel.equals(INCOMING_CHANNEL)) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        String type = in.readUTF();
        if (!type.equals("AUTOLOGIN:")) {
            return;
        }

        String name = in.readUTF();
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) {
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
