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
		playerLogin(plugin.getServer().getPlayer(event.getPlayerAuth().getNickname()));
	}
	
	public void playerLogin(final Player player) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("PlayerLogin");
			out.writeUTF(player.getUniqueId().toString());
			new PluginMessageTask(plugin, b).runTaskAsynchronously(plugin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
