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
	//HashMap<String,Server> servers = new HashMap<String,Server>();
	boolean serverSwitchRequiresAuth;

	public void onEnable() {
	    saveDefaultConfig();
		/*for (String server : getConfig().getConfigurationSection("servers").getKeys(false))
			servers.put(server, new Server(getConfig().getString("servers." + server + ".host"),
							getConfig().getInt("servers." + server + ".port"),
							getConfig().getString("servers." + server + ".username"),
							getConfig().getString("servers." + server + ".password"),
							getConfig().getString("servers." + server + ".salt")));*/
	    mainServer = getConfig().getString("mainServer");
	    serverSwitchRequiresAuth = getConfig().getBoolean("serverSwitchRequiresAuth");
	    getProxy().registerChannel(incomingChannel);
		getProxy().getPluginManager().registerListener(this, new BungeeAuthMeBridgeListener(this));
	}

	/*public void updateList() {
	    for (Entry<String,Server> entry: servers.entrySet())
	    	authList.put(entry.getKey(), getLoggedInPlayers(entry.getValue()));
	}

	public LinkedList<String> getLoggedInPlayers(Server server) {
		LinkedList<String> list = new LinkedList<String>();
		JsonParser parser = new JsonParser();
		JsonElement tradeElement;
		try {
			tradeElement = parser.parse(server.getURL("authmebridge.getAuthenticatedPlayers"));
			JsonObject object = tradeElement.getAsJsonObject();
			if (object.get("success") == null)
				return new LinkedList<String>();
			JsonArray trade = object.get("success").getAsJsonArray();
			for (JsonElement element: trade) {
				list.add(element.getAsString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}*/
	
}
