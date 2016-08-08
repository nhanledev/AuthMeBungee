package com.crylegend.authmebridge;

import java.io.ByteArrayOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PluginMessageTask extends BukkitRunnable {
    private final AuthMeBridge plugin;
    private final Player player;
    private final ByteArrayOutputStream bytes;

    public PluginMessageTask(AuthMeBridge plugin, Player player, ByteArrayOutputStream bytes) {
        this.plugin = plugin;
        this.player = player;
        this.bytes = bytes;
    }

    public void run() {
        player.sendPluginMessage(plugin, plugin.getOutgoingChannel(), bytes.toByteArray());
    }
}
