package com.crylegend.bungeeauthmebridge;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeAuthMeBridgeListener implements Listener{
	BungeeAuthMeBridge plugin;

	public BungeeAuthMeBridgeListener(BungeeAuthMeBridge plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onChat(ChatEvent event) {
		if (!(event.getSender() instanceof ProxiedPlayer))
			return;
		String cmd = event.getMessage().split(" ")[0];
		if (cmd.equalsIgnoreCase("/login") || cmd.equalsIgnoreCase("/register") || cmd.equalsIgnoreCase("/passpartu") || cmd.equalsIgnoreCase("/l") || cmd.equalsIgnoreCase("/reg") || cmd.equalsIgnoreCase("/email") || cmd.equalsIgnoreCase("/captcha"))
			return;
		ProxiedPlayer player = (ProxiedPlayer)event.getSender();
		String server = player.getServer().getInfo().getName();
	    if (!plugin.authList.containsKey(server))
	    	plugin.authList.put(server, new LinkedList<UUID>());
		if (plugin.authList.get(server).isEmpty() || !plugin.authList.get(server).contains(player.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) throws IOException {
		if (!event.getTag().equalsIgnoreCase(plugin.incomingChannel))
			return;
		if (!(event.getSender() instanceof Server))
			return;
		event.setCancelled(true);
		String server = ((Server) event.getSender()).getInfo().getName();
	    DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
	    String task = in.readUTF();
	    if (!task.equals("PlayerLogin"))
	    	return;
	    event.setCancelled(true);
	    if (!plugin.authList.containsKey(server))
	    	plugin.authList.put(server, new LinkedList<UUID>());
	    UUID uuid = UUID.fromString(in.readUTF());
	    plugin.authList.get(server).add(uuid);
	}

	@EventHandler
	public void onLeave(PlayerDisconnectEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (event.getPlayer().getServer() == null)
			return;
		String server = event.getPlayer().getServer().getInfo().getName();
	    if (plugin.authList.containsKey(server))
	    	if (plugin.authList.get(server).contains(uuid))
	    		plugin.authList.get(server).remove(uuid);
	}
	
	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {
		if (!plugin.serverSwitchRequiresAuth)
			return;
		ProxiedPlayer player = event.getPlayer();
	    for (LinkedList<UUID> list: plugin.authList.values())
	    	if (list.contains(player.getUniqueId()))
	    		return;
	    if (player.getServer().getInfo().getName().equalsIgnoreCase(plugin.mainServer))
	    	return;
	    TextComponent kickReason = new TextComponent("Authentification requise.");
	    kickReason.setColor(ChatColor.RED);
	    player.disconnect(kickReason);
	}
}
