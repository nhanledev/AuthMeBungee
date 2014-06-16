package com.crylegend.authmebridge;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.SessionEvent;

public class AuthMeBridgeListener implements Listener{
	AuthMeBridge plugin;
	
	public AuthMeBridgeListener(AuthMeBridge plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onAuthMeLogin(LoginEvent event) {
		if (!event.isLogin())
			return;
		playerLogin(event.getPlayer());
	}

	@EventHandler
	public void onAuthMeSession(SessionEvent event) {
		if (!event.isLogin())
			return;
		final Player player = plugin.getServer().getPlayerExact(event.getPlayerAuth().getNickname());
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				playerLogin(player);
			}
		}, 10L);
	}
	
	public void playerLogin(Player player) {
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
