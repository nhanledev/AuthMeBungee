package com.crylegend.authmebridge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.API;
import fr.xephi.authme.cache.auth.PlayerCache;

public class AuthMeBridge extends JavaPlugin {
    Logger log = Logger.getLogger("Minecraft");
	String prefix = "[AuthMeBridge] ";
	AuthMeBridgeListener listener;
	AuthMe authMePlugin;
	String server;
	
	public void onEnable() {
		log.info(prefix + "Hello world");
		authMePlugin = API.hookAuthMe();
		if (authMePlugin == null) {
			log.info(prefix + "Cannot hook into AuthMe, disabling");
			getServer().getPluginManager().disablePlugin(this);
		}
		getServer().getPluginManager().registerEvents(new AuthMeBridgeListener(this), this);
	    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    server = getConfig().getString("server");
	    saveDefaultConfig();
        /*getServer().getMessenger().registerIncomingPluginChannel(this, "AuthMeBridge", new PluginMessageListener() {
        	@Override
            public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        	getServer().broadcastMessage("Hey");
            if (!channel.equals("BungeeCord")) {
                return;
            }
            getServer().broadcastMessage("Hey");
     
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subchannel = null;
    		try {
    			subchannel = in.readUTF();
    	        if (subchannel.equals("hasPermission")) {
    	        	getServer().getPlayer("CryLegend").sendMessage("Hey:" + in.readUTF());
    	        }
    		} catch (IOException e) {
    			log.info(prefix + "Erreur IOException: " + e.getMessage());
    		}
        }
        });*/
        
	}
	
	public void onDisable() {
		log.info(prefix + "Goodbye world");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("testbridge")) {
			if (!sender.hasPermission("authmebridge.testbridge")) {
				sender.sendMessage(ChatColor.DARK_RED
						+ "Permission refus\u00e9e.");
				return true;
			}
			sendData();
			return true;
		}
		return false;
	}

	public void sendData() {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("loggedInPlayersList");
			out.writeUTF(getAuthenticatedPlayers());
			getServer().broadcastMessage(getAuthenticatedPlayers());
			
			Player p = getServer().getOnlinePlayers()[0];

			p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
		} catch (IOException e) {
			log.info(prefix + "Erreur IOException: " + e.getMessage());
		}
		
	}

	private String getAuthenticatedPlayers() {
		StringBuilder sb = new StringBuilder();
		sb.append(server);
		for (Player player: getServer().getOnlinePlayers()) {
			if (API.isAuthenticated(player))
					sb.append(", " + player.getName());
		}
		return sb.toString();
	}

}