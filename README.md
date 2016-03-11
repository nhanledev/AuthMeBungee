# AuthMeBridge

![AuthMeBridge](http://www.craftfriends.fr/images/AuthMeBridge.png "AuthMeBridge")

This plugin acts like a bridge between your bukkit servers and your BungeeCord instance. To explain simple how it works, bukkit-side plugins send a message to bungee-side on user authentication. If bungee-side doesn't receive this message, the player won't be able to talk in chat and to perform commands, including BungeeCord commands.

Session resume: If you have AuthMe on multiple servers, you can enable autoLogin option that allow you to switch between your servers without having to perform login command again.

## Requirements

None

## Installation

1. Download AuthMeBridge package
2. Place BungeeAuthMeBridge.jar into your BungeeCord's plugin folder
3. Place AuthMeBridge.jar (or xAuthBridge.jar if you're using xAuth) into your servers' plugin folders
4. Restart everything
5. Configure the bungee-side plugin (don't forget to config serversList)
6. Enjoy!

**Please follow these steps and configure the plugin before saying it doesn't work!**
