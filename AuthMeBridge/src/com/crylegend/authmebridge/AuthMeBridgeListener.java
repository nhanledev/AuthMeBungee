package com.crylegend.authmebridge;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.xephi.authme.events.LoginEvent;

public class AuthMeBridgeListener implements Listener{
	AuthMeBridge plugin;
	
	public AuthMeBridgeListener(AuthMeBridge plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.sendData();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.sendData();
	}

	@EventHandler
	public void onLogin(LoginEvent event) {
		plugin.sendData();
	}

}
