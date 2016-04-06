/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   BridgeAPI.java                                     :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: CryLegend <crylegend95@gmail.com>          +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2016/03/25 18:30:37 by CryLegend         #+#    #+#             */
/*   Updated: 2016/03/25 18:30:40 by CryLegend        ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package com.crylegend.bungeeauthmebridge;

/*
 * API class - acts like shortcuts
 */
public class BridgeAPI {
	
	private BridgeAPI() { }
	
	public static BungeeAuthMeBridge getPlugin() {
		return BungeeAuthMeBridge.getInstance();
	}
	
	public static PlayersManager getPlayersManager() {
		return BungeeAuthMeBridge.getInstance().getPlayersManager();
	}
}
