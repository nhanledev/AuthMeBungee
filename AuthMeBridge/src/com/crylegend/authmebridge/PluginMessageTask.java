package com.crylegend.authmebridge;

import java.io.ByteArrayOutputStream;

import org.bukkit.scheduler.BukkitRunnable;

public class PluginMessageTask extends BukkitRunnable {
	private final AuthMeBridge plugin;
	private final ByteArrayOutputStream bytes;

	public PluginMessageTask(AuthMeBridge plugin, ByteArrayOutputStream bytes)
	{
		this.plugin = plugin;
		this.bytes = bytes;
	}

	public void run() {
		org.bukkit.Bukkit.getOnlinePlayers()[0].sendPluginMessage(plugin, plugin.outcomingChannel, bytes.toByteArray());
	}
}
