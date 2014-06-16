package com.crylegend.xauthbridge;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import de.luricos.bukkit.xAuth.xAuth;

public class xAuthBridge extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	String prefix = "[xAuthBridge] ";
	String outgoingChannel = "AuthMeBridge";
	xAuth xauth;

	public void onEnable() {
		log.info(prefix + "Hello world");
		if (!getServer().getPluginManager().isPluginEnabled("xAuth")) {
			log.info(prefix + "xAuth not found, disabling");
			getServer().getPluginManager().disablePlugin(this);
		}
		xauth = (xAuth) getServer().getPluginManager().getPlugin("xAuth");
		getServer().getPluginManager().registerEvents(new xAuthBridgeListener(this), this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, outgoingChannel);

	}

	public void onDisable() {
		log.info(prefix + "Goodbye world");
	}
}
