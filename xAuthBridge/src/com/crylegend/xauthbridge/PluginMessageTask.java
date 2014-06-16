package com.crylegend.xauthbridge;

import java.io.ByteArrayOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PluginMessageTask extends BukkitRunnable {
	private final xAuthBridge plugin;
	private final Player player;
	private final ByteArrayOutputStream bytes;

	public PluginMessageTask(xAuthBridge plugin, Player player, ByteArrayOutputStream bytes)
	{
		this.plugin = plugin;
		this.player = player;
		this.bytes = bytes;
	}

	public void run() {
		player.sendPluginMessage(plugin, plugin.outgoingChannel, bytes.toByteArray());
	}
}
