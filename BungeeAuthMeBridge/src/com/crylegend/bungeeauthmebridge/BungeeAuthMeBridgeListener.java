package com.crylegend.bungeeauthmebridge;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
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
		if (!plugin.serversList.contains(server))
			return;
		if (plugin.commandsWhitelist.contains(cmd))
			return;
		if (!plugin.authList.containsKey(server))
			plugin.authList.put(server, new LinkedList<String>());
		if (plugin.authList.get(server).isEmpty() || !plugin.authList.get(server).contains(player.getName()))
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
		String name = in.readUTF();
		boolean online = false;
		for (ProxiedPlayer player: plugin.getProxy().getPlayers()) {
			if (player.getName().equals(name)) {
				online = true;
				break;
			}
		}
		if (!online)
			return;
		if (!plugin.authList.containsKey(server))
			plugin.authList.put(server, new LinkedList<String>());
		if (!plugin.authList.get(server).contains(name))
			plugin.authList.get(server).add(name);
	}

	@EventHandler
	public void onLeave(PlayerDisconnectEvent event) {
		String name = event.getPlayer().getName();
		if (event.getPlayer().getServer() == null) {
			for (LinkedList<String> list: plugin.authList.values())
				if (list.contains(name))
					list.remove(name);
		}
		else {
			String server = event.getPlayer().getServer().getInfo().getName();
			if (plugin.authList.containsKey(server))
				if (plugin.authList.get(server).contains(name))
					plugin.authList.get(server).remove(name);
		}
	}

	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {
		String name = event.getPlayer().getName();
		String server = event.getPlayer().getServer().getInfo().getName();
		if (plugin.authList.containsKey(server))
			if (plugin.authList.get(server).contains(name))
				plugin.authList.get(server).remove(name);
		if (!plugin.serverSwitchRequiresAuth)
			return;
		ProxiedPlayer player = event.getPlayer();
		for (String str: plugin.serversList)
			if (server.equalsIgnoreCase(str))
				return;
		for (LinkedList<String> list: plugin.authList.values())
			if (list.contains(player.getName()))
				return;
		TextComponent kickReason = new TextComponent("Authentication required.");
		kickReason.setColor(ChatColor.RED);
		player.disconnect(kickReason);
	}
}
