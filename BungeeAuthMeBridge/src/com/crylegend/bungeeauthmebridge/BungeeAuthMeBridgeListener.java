package com.crylegend.bungeeauthmebridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BungeeAuthMeBridgeListener implements Listener{
    BungeeAuthMeBridge plugin;
	
	public BungeeAuthMeBridgeListener(BungeeAuthMeBridge plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {
	    if (!event.getTag().equals("BungeeCord"))
	    	return;
	    if (!(event.getSender() instanceof Server))
	    	return;
	    event.setCancelled(true);
	    ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
	    String subchannel = in.readUTF();
	    if (subchannel.equals("loggedInPlayersList")) {
	    	String[] array = in.readUTF().split(", ");
	    	LinkedList<String> list = new LinkedList<String>(Arrays.asList(array));
	    	list.remove(array[0]);
	    	plugin.authList.put(array[0], list);
	   }
	}
	
	@EventHandler
	public void onChat(ChatEvent event) {
		if (!(event.getSender() instanceof ProxiedPlayer)) {
	      return;
	    }
		String cmd = event.getMessage().split(" ")[0];
        if (cmd.equalsIgnoreCase("/login") || cmd.equalsIgnoreCase("/register") || cmd.equalsIgnoreCase("/passpartu") || cmd.equalsIgnoreCase("/l") || cmd.equalsIgnoreCase("/reg") || cmd.equalsIgnoreCase("/email") || cmd.equalsIgnoreCase("/captcha"))
            return;
	    ProxiedPlayer player = (ProxiedPlayer)event.getSender();
		if (!plugin.authList.containsKey("lobby") || plugin.authList.get("lobby").isEmpty() || !plugin.authList
				.get(player.getServer().getInfo().getName())
				.contains(player.getName()))
			event.setCancelled(true);
	}
}