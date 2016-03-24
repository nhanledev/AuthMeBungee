package com.crylegend.bungeeauthmebridge;

import java.util.Arrays;
import java.util.List;

/*
 * Plugin constants
 */
public class Constants {
	public static String incomingChannel = "AuthMeBridge";
	public static String outgoingChannel = "BAuthMeBridge";
	public static List<String> authMeCommands = Arrays.asList(
			"/login", "/register", "/passpartu", "/l", "/reg", "/email", "/captcha");
	
	private Constants() { }
}
