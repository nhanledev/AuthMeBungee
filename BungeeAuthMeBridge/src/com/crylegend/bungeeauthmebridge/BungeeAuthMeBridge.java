package com.crylegend.bungeeauthmebridge;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeAuthMeBridge extends Plugin{
	
	public List<String> authList = new ArrayList<String>();

	public void onEnable() {
		ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeAuthMeBridgeListener(this));
	}
}
