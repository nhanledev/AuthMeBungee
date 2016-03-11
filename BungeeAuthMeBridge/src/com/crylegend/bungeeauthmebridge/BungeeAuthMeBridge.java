package com.crylegend.bungeeauthmebridge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeAuthMeBridge extends Plugin{
	String incomingChannel = "AuthMeBridge";
	String outgoingChannel = "BAuthMeBridge";
	List<String> serversList;
	List<String> commandsWhitelist;
	HashMap<String,LinkedList<String>> authList = new HashMap<String,LinkedList<String>>();
	boolean commandsRequiresAuth = true;
	boolean chatRequiresAuth = true;
	boolean serverSwitchRequiresAuth = false;
	boolean autoLogin = true;

	public void onEnable() {
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				try (InputStream is = getResourceAsStream("config.yml");
						OutputStream os = new FileOutputStream(configFile)) {
					ByteStreams.copy(is, os);
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to create configuration file", e);
			}
		}
		try {
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
			serversList = configuration.getStringList("serversList");
			commandsWhitelist = configuration.getStringList("commandsWhitelist");
			commandsRequiresAuth = configuration.getBoolean("commandsRequiresAuth", true);
			chatRequiresAuth = configuration.getBoolean("chatRequiresAuth", true);
			serverSwitchRequiresAuth = configuration.getBoolean("serverSwitchRequiresAuth", false);
			autoLogin = configuration.getBoolean("autoLogin", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getProxy().registerChannel(incomingChannel);
		getProxy().registerChannel(outgoingChannel);
		getProxy().getPluginManager().registerListener(this, new BungeeAuthMeBridgeListener(this));
	}
}
