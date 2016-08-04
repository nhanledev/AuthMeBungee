/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   PlayerListener.java                                :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: CryLegend <crylegend95@gmail.com>          +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2016/03/25 18:31:32 by CryLegend         #+#    #+#             */
/*   Updated: 2016/03/25 18:31:34 by CryLegend        ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package com.crylegend.bungeeauthmebridge.listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.crylegend.bungeeauthmebridge.BridgeAPI;
import com.crylegend.bungeeauthmebridge.Constants;
import com.crylegend.bungeeauthmebridge.config.Settings;
import com.crylegend.bungeeauthmebridge.types.AuthPlayer;
import com.crylegend.bungeeauthmebridge.utils.PluginMessageTask;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/*
 * Player listener - listen to all actions that are related to players
 */
public class PlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PostLoginEvent event) {
		// Register player in our list
		AuthPlayer player = new AuthPlayer(event.getPlayer().getName());
		BridgeAPI.getPlayersManager().addPlayer(player);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerDisconnectEvent event) {
		// Remove player from out list
		BridgeAPI.getPlayersManager().removePlayer(event.getPlayer().getName());
	}

	// Priority is set to lowest to keep compatibility with some chat plugins
	@EventHandler(priority=EventPriority.LOWEST)
	public void onChat(ChatEvent event) {
		if (event.isCancelled())
			return;
		
		// Check if it's a player
		if (!(event.getSender() instanceof ProxiedPlayer))
			return;
		
		if (event.isCommand()) {
			String command = event.getMessage().split(" ")[0];
			
			// Check if command is an AuthMe command
			if (Constants.authMeCommands.contains(command))
				return;

			// Check if command is a whitelisted command
			if (Settings.commandsWhitelist.contains(command))
				return;
		}
		
		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();
		AuthPlayer player = BridgeAPI.getPlayersManager().getPlayer(proxiedPlayer);
		
		// If player is not logged in, cancel the event
		if (!player.isLoggedIn())
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {
		AuthPlayer player = BridgeAPI.getPlayersManager().getPlayer(event.getPlayer());
		
		// Check if player exists (causing NPE, maybe the event is fired on player disconnect?)
		if (player == null)
			return;
		
		// Player is trying to switch server (also called on first server player connection)
		if (player.isLoggedIn()) {
			// If player is logged in and autoLogin is enabled, send login signal to the bukkit side
			if (Settings.autoLogin) {
				try {
					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);

					out.writeUTF("AutoLogin");

					out.writeUTF(player.getName());

					ProxyServer.getInstance().getScheduler().runAsync(BridgeAPI.getPlugin(),
							new PluginMessageTask(event.getPlayer().getServer().getInfo(), b));
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			// If player is not logged in and serverSwitchRequiresAuth is enabled, kick player
			if (Settings.serverSwitchRequiresAuth) {
				String server = event.getPlayer().getServer().getInfo().getName();
				
				if (!Settings.serversList.contains(server)) {
					TextComponent kickReason = new TextComponent("Authentication required.");
					kickReason.setColor(ChatColor.RED);
					event.getPlayer().disconnect(kickReason);
				}
			}
		}
	}

	// I don't remember what it stood for, but I put it again "just in case"
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerKick(ServerKickEvent event) {
		if (event.isCancelled())
			return;
		
		if (event.getKickReason().equals("You logged in from another location"))
			event.setCancelled(true);
	}
}
