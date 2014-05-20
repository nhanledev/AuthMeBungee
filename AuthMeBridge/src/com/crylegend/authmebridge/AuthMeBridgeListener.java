package com.crylegend.authmebridge;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

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
		playerLogin(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onAuthMeSession(SessionEvent event) {
		if (!event.isLogin())
			return;
		final UUID uuid = plugin.getServer().getPlayerExact(event.getPlayerAuth().getNickname()).getUniqueId();
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				playerLogin(uuid);
			}
		}, 10L);
	}
	
	public void playerLogin(UUID uuid) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("PlayerLogin");
			out.writeUTF(uuid.toString());
			new PluginMessageTask(plugin, b).runTaskAsynchronously(plugin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
