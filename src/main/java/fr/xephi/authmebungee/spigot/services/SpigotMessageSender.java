package fr.xephi.authmebungee.spigot.services;

import fr.xephi.authmebungee.spigot.AuthMeBungeeSpigot;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;

public class SpigotMessageSender {

    private AuthMeBungeeSpigot plugin;
    private Server server;

    @Inject
    public SpigotMessageSender(AuthMeBungeeSpigot plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    public void sendData(byte[] bytes, boolean async) {
        SendMessage sendMessage = new SendMessage(bytes);
        if (async) {
            sendMessage.runTaskAsynchronously(plugin);
        } else {
            sendMessage.run();
        }
    }

    private class SendMessage extends BukkitRunnable {

        private byte[] bytes;

        public SendMessage(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public void run() {
            server.sendPluginMessage(plugin, "BungeeCord", bytes);
        }
    }
}
