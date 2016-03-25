package com.crylegend.xauthbridge;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class xAuthBridgeListener implements Listener{
	xAuthBridge plugin;
	
	public xAuthBridgeListener(xAuthBridge plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onxAuthSession(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				playerLogin(player);
			}
		}, 10L);
	}

	@EventHandler
	public void onxAuthLogin(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;
		String cmd = event.getMessage().split(" ")[0];
		if (!cmd.equalsIgnoreCase("/register") && !cmd.equalsIgnoreCase("/login"))
			return;
		final Player player = event.getPlayer();
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				playerLogin(player);
			}
		}, 10L);
	}
	
	public void playerLogin(Player player) {
		if (!plugin.xauth.getPlayerManager().getPlayer(player.getName()).isAuthenticated())
			return;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("PlayerLogin");
			out.writeUTF(player.getName());
			new PluginMessageTask(plugin, player, b).runTaskAsynchronously(plugin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
