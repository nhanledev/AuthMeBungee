package com.crylegend.bungeeauthmebridge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeAuthMeBridge extends Plugin{
    
	public HashMap<String,LinkedList<String>> authList = new HashMap<String,LinkedList<String>>();

	public void onEnable() {
		ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeAuthMeBridgeListener(this));
	}
}