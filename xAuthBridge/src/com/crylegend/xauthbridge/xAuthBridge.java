package com.crylegend.xauthbridge;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import de.luricos.bukkit.xAuth.xAuth;
import de.luricos.bukkit.xAuth.xAuthPlayer.Status;

public class xAuthBridge extends JavaPlugin implements PluginMessageListener {
	Logger log = Logger.getLogger("Minecraft");
	String prefix = "[xAuthBridge] ";
	String incomingChannel = "BAuthMeBridge";
	String outgoingChannel = "AuthMeBridge";
	String autoLoginMessage = "§aYour session has been resumed by the bridge.";
	xAuth xauth;

	public void onEnable() {
		log.info(prefix + "Hello world");
		
		if (!getServer().getPluginManager().isPluginEnabled("xAuth")) {
			log.info(prefix + "xAuth not found, disabling");
			getServer().getPluginManager().disablePlugin(this);
		}
		
		xauth = (xAuth) getServer().getPluginManager().getPlugin("xAuth");
		
		getServer().getPluginManager().registerEvents(new xAuthBridgeListener(this), this);
		getServer().getMessenger().registerIncomingPluginChannel(this, incomingChannel, this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, outgoingChannel);
		
		autoLoginMessage = getConfig().getString("autoLoginMessage", autoLoginMessage);
		getConfig().set("autoLoginMessage", autoLoginMessage);
		
		saveConfig();
	}

	public void onDisable() {
		log.info(prefix + "Goodbye world");
	}

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		if (channel.equals(incomingChannel)) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();

			if (subchannel.equals("AutoLogin")) {
				Player player = Bukkit.getPlayer(in.readUTF());

				if (player != null) {
					xauth.getPlayerManager().doLogin(xauth.getPlayerManager().getPlayer(player));
					if (!autoLoginMessage.isEmpty())
						player.sendMessage(autoLoginMessage);
				}
			}
		}
	}
}
