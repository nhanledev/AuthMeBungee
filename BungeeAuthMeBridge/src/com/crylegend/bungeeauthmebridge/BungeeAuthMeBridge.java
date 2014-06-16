package com.crylegend.bungeeauthmebridge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;

public class BungeeAuthMeBridge extends ConfigurablePlugin{
	String incomingChannel = "AuthMeBridge";
	List<String> serversList;
	List<String> commandsWhitelist;
	HashMap<String,LinkedList<String>> authList = new HashMap<String,LinkedList<String>>();
	boolean serverSwitchRequiresAuth;

	public void onEnable() {
	    saveDefaultConfig();
	    serversList = getConfig().getStringList("serversList");
	    commandsWhitelist = getConfig().getStringList("commandsWhitelist");
	    serverSwitchRequiresAuth = getConfig().getBoolean("serverSwitchRequiresAuth");
	    getProxy().registerChannel(incomingChannel);
		getProxy().getPluginManager().registerListener(this, new BungeeAuthMeBridgeListener(this));
	}
}
