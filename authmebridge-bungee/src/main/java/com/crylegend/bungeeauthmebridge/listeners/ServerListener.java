/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   ServerListener.java                                :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: CryLegend <crylegend95@gmail.com>          +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2016/03/25 18:31:36 by CryLegend         #+#    #+#             */
/*   Updated: 2016/03/25 18:31:37 by CryLegend        ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package com.crylegend.bungeeauthmebridge.listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.crylegend.bungeeauthmebridge.BridgeAPI;
import com.crylegend.bungeeauthmebridge.Constants;
import com.crylegend.bungeeauthmebridge.types.AuthPlayer;

import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/*
 * Server listener - listen to all actions that are related to server, especially plugin messages
 */
public class ServerListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.isCancelled())
            return;

        // Check if the message is for us
        if (!event.getTag().equalsIgnoreCase(Constants.incomingChannel))
            return;

        // Check if a player is not trying to rip us off sending a fake message
        if (!(event.getSender() instanceof Server))
            return;

        // Now that's sure, it's for us, so let's go
        event.setCancelled(true);

        try {
            // Read the plugin message
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

            // For now that's the only type of message the server is able to
            // receive
            String task = in.readUTF();
            if (!task.equals("PlayerLogin"))
                return;

            // Gather informations from the plugin message
            String name = in.readUTF();
            AuthPlayer player = BridgeAPI.getPlayersManager().getPlayer(name);

            // Set the player status to logged in
            player.setLoggedIn();
        } catch (IOException ex) {
            // Something nasty happened
            ex.printStackTrace();
        }
    }
}
