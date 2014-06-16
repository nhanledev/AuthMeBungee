package com.crylegend.authmebridge;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class AuthMeBridge extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	String prefix = "[AuthMeBridge] ";
	String outgoingChannel = "AuthMeBridge";

	public void onEnable() {
		log.info(prefix + "Hello world");
		if (!getServer().getPluginManager().isPluginEnabled("AuthMe")) {
			log.info(prefix + "AuthMe not found, disabling");
			getServer().getPluginManager().disablePlugin(this);
		}
		getServer().getPluginManager().registerEvents(new AuthMeBridgeListener(this), this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, outgoingChannel);

	}

	public void onDisable() {
		log.info(prefix + "Goodbye world");
	}
}
