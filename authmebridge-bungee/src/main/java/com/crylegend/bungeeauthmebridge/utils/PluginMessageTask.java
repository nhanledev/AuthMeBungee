/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   PluginMessageTask.java                             :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: CryLegend <crylegend95@gmail.com>          +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2016/03/25 18:31:59 by CryLegend         #+#    #+#             */
/*   Updated: 2016/03/25 18:32:00 by CryLegend        ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package com.crylegend.bungeeauthmebridge.utils;

import java.io.ByteArrayOutputStream;

import com.crylegend.bungeeauthmebridge.Constants;

import net.md_5.bungee.api.config.ServerInfo;

/*
 * Plugin message task - used to send plugin messages to bukkit-side
 */
public class PluginMessageTask implements Runnable {
    private final ByteArrayOutputStream bytes;
    private final ServerInfo server;

    public PluginMessageTask(ServerInfo server, ByteArrayOutputStream bytes) {
        this.bytes = bytes;
        this.server = server;
    }

    public void run() {
        server.sendData(Constants.outgoingChannel, bytes.toByteArray());
    }
}
