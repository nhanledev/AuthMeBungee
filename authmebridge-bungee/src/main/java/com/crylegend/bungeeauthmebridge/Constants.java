/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   Constants.java                                     :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: CryLegend <crylegend95@gmail.com>          +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2016/03/25 18:30:53 by CryLegend         #+#    #+#             */
/*   Updated: 2016/03/25 18:30:54 by CryLegend        ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package com.crylegend.bungeeauthmebridge;

import java.util.Arrays;
import java.util.List;

/*
 * Plugin constants
 */
public class Constants {
    public static String incomingChannel = "AuthMeBridge";
    public static String outgoingChannel = "BAuthMeBridge";
    public static List<String> authMeCommands = Arrays.asList("/login", "/register", "/passpartu", "/l", "/reg",
            "/email", "/captcha");
}
