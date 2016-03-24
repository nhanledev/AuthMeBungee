package com.crylegend.bungeeauthmebridge;

/*
 * API class - acts like shortcuts
 */
public class BridgeAPI {
	
	private BridgeAPI() { }
	
	public static BungeeAuthMeBridge getPlugin() {
		return BungeeAuthMeBridge.getInstance();
	}
	
	public static PlayersManager getPlayersManager() {
		return BungeeAuthMeBridge.getInstance().getPlayersManager();
	}
}
