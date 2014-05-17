package com.crylegend.bungeeauthmebridge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.md_5.bungee.api.ProxyServer;
import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;

public class BungeeAuthMeBridge extends ConfigurablePlugin{
	String incomingChannel = "AuthMeBridge";
	String outcomingChannel = "BAuthMeBridge";
	String mainServer;
	HashMap<String,LinkedList<UUID>> authList = new HashMap<String,LinkedList<UUID>>();
	boolean serverSwitchRequiresAuth;

	public void onEnable() {
	    saveDefaultConfig();
	    mainServer = getConfig().getString("mainServer");
	    serverSwitchRequiresAuth = getConfig().getBoolean("serverSwitchRequiresAuth");
	    getProxy().registerChannel(incomingChannel);
		getProxy().getPluginManager().registerListener(this, new BungeeAuthMeBridgeListener(this));
	}
}
