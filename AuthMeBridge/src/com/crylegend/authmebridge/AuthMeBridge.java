package com.crylegend.authmebridge;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.API;

public class AuthMeBridge extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	String prefix = "[AuthMeBridge] ";
	String incomingChannel = "BAuthMeBridge";
	String outcomingChannel = "AuthMeBridge";

	public void onEnable() {
		log.info(prefix + "Hello world");
		if (!getServer().getPluginManager().isPluginEnabled("AuthMe")) {
			log.info(prefix + "AuthMe not found, disabling");
			getServer().getPluginManager().disablePlugin(this);
		}
		getServer().getPluginManager().registerEvents(new AuthMeBridgeListener(this), this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, outcomingChannel);

	}

	public void onDisable() {
		log.info(prefix + "Goodbye world");
	}

	/*public String getAuthenticatedPlayers() {
		List<String> list = new ArrayList<String>();
		for (Player player: getServer().getOnlinePlayers()) {
			if (API.isAuthenticated(player))
				list.add(player.getName());
		}
		return StringUtils.join(list, ", ");
	}

	@Override
	public void onPluginMessageReceived(String channel, final Player player, byte[] message) {
		if (!channel.equals("BAuthMeBridge"))
			return;
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("AuthList")) {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("AuthList");
				out.writeUTF(getAuthenticatedPlayers());
				final byte[] byteArray = b.toByteArray();
				getServer().getScheduler().runTaskAsynchronously(this, new Runnable(){
					public void run(){
						sendPluginMessage(player, byteArray);
						}
					}
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendPluginMessage(Player player, byte[] byteArray) {
		player.sendPluginMessage(this, outcomingChannel, byteArray);
	}*/

}
