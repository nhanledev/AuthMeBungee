/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   PlayersManager.java                                :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: CryLegend <crylegend95@gmail.com>          +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2016/03/25 18:30:58 by CryLegend         #+#    #+#             */
/*   Updated: 2016/03/25 18:30:59 by CryLegend        ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package com.crylegend.bungeeauthmebridge;

import java.util.HashMap;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import com.crylegend.bungeeauthmebridge.types.AuthPlayer;

/*
 * Players manager - store all references to AuthPlayer objects through an HashMap
 */
public class PlayersManager {
	private HashMap<String, AuthPlayer> players;
	
	public PlayersManager() {
		players = new HashMap<String, AuthPlayer>();
	}
	
	public void addPlayer(AuthPlayer player) {
		players.put(player.getName(), player);
	}
	
	public void removePlayer(String name) {
		players.remove(name);
	}
	
	public AuthPlayer getPlayer(ProxiedPlayer player) {
		return getPlayer(player.getName());
	}
	
	public AuthPlayer getPlayer(String name) {
		return players.get(name);
	}
}
