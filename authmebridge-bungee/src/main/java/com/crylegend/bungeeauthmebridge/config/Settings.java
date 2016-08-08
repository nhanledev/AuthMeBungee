/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Settings.java                                      :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: CryLegend <crylegend95@gmail.com>          +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2016/03/25 18:31:11 by CryLegend         #+#    #+#             */
/*   Updated: 2016/03/25 18:31:13 by CryLegend        ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package com.crylegend.bungeeauthmebridge.config;

import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

import com.crylegend.bungeeauthmebridge.BridgeAPI;

/*
 * Settings class - from AuthMeBungee
 * @authors Xephi, CryLegend
 */
public final class Settings {
    protected YamlConfig configFile = null;
    public static List<String> commandsWhitelist = new ArrayList<>();
    public static List<String> serversList = new ArrayList<>();
    public static boolean commandsRequiresAuth = true;
    public static boolean chatRequiresAuth = true;
    public static boolean serverSwitchRequiresAuth = false;
    public static String requiresAuthKickMessage = "Authentication required.";
    public static boolean autoLogin = true;
    private Configuration config;

    public Settings() {
        try {
            this.configFile = new YamlConfig("config.yml", BridgeAPI.getPlugin());
            this.configFile.saveDefaultConfig();
            this.configFile.loadConfig();
            this.config = configFile.getConfig();
            loadConfigOptions();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadConfigOptions() {
        if (configFile == null) {
            return;
        }

        commandsWhitelist = config.getStringList("commandsWhitelist");
        serversList = config.getStringList("serversList");
        commandsRequiresAuth = config.getBoolean("commandsRequiresAuth", true);
        chatRequiresAuth = config.getBoolean("chatRequiresAuth", true);
        serverSwitchRequiresAuth = config.getBoolean("serverSwitchRequiresAuth", false);
        requiresAuthKickMessage = config.getString("serverSwitchKickMessage", "Authentication required.");
        autoLogin = config.getBoolean("autoLogin", true);
    }
}
