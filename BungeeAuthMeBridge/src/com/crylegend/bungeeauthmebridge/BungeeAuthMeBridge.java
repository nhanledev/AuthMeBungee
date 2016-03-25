/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BungeeAuthMeBridge.java                            :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: CryLegend <crylegend95@gmail.com>          +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2016/03/25 18:30:49 by CryLegend         #+#    #+#             */
/*   Updated: 2016/03/25 18:30:50 by CryLegend        ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package com.crylegend.bungeeauthmebridge;

import com.crylegend.bungeeauthmebridge.config.Settings;
import com.crylegend.bungeeauthmebridge.listeners.*;

import net.md_5.bungee.api.plugin.Plugin;

/*
 * Main class
 */
public class BungeeAuthMeBridge extends Plugin {
	private static BungeeAuthMeBridge instance;
	private PlayersManager playersManager;

	public static BungeeAuthMeBridge getInstance() {
		return instance;
	}
	
	public PlayersManager getPlayersManager() {
		return playersManager;
	}
	
	public void log(String message) {
		getLogger().info(message);
	}
	
	public void onLoad() {
		instance = this;
	}

	public void onEnable() {
		// Registering channels to be able to communicate with bukkit-side
		getProxy().registerChannel(Constants.incomingChannel);
		getProxy().registerChannel(Constants.outgoingChannel);
		
		// Registering listeners
		getProxy().getPluginManager().registerListener(this, new PlayerListener());
		getProxy().getPluginManager().registerListener(this, new ServerListener());
		
		playersManager = new PlayersManager();
		new Settings();
	}
}
