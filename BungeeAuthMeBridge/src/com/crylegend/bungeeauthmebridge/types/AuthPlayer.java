package com.crylegend.bungeeauthmebridge.types;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/*
 * Auth player - contains name and login status of a player
 */
public class AuthPlayer {
	private String name;
	private boolean isLoggedIn;
	
	public AuthPlayer(String name) {
		this.name = name;
		isLoggedIn = false;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	public void setLoggedIn() {
		isLoggedIn = true;
	}
	
	public boolean isOnline() {
		return getProxiedPlayer() != null;
	}
	
	public ProxiedPlayer getProxiedPlayer() {
		return ProxyServer.getInstance().getPlayer(name);
	}
}
