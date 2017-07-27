package fr.xephi.authmebungee.bungeecord.services;

import fr.xephi.authmebungee.bungeecord.AuthMeBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import javax.inject.Inject;

public class BungeeMessageSender {

    private AuthMeBungee plugin;
    private TaskScheduler scheduler;

    @Inject
    BungeeMessageSender(AuthMeBungee plugin, TaskScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    public void sendData(ServerInfo server, byte[] bytes, boolean async) {
        SendMessage sendMessage = new SendMessage(server, bytes);
        if (async) {
            scheduler.runAsync(plugin, sendMessage);
        } else {
            sendMessage.run();
        }
    }

    public void sendData(Server server, byte[] bytes, boolean async) {
        sendData(server.getInfo(), bytes, async);
    }

    private class SendMessage implements Runnable {

        private ServerInfo server;
        private byte[] bytes;

        public SendMessage(ServerInfo server, byte[] bytes) {
            this.server = server;
            this.bytes = bytes;
        }

        @Override
        public void run() {
            server.sendData("BungeeCord", bytes);
        }
    }
}
