package fr.xephi.authmebungee.bungeecord.services;

import fr.xephi.authmebungee.bungeecord.data.AuthPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

/*
 * Players manager - store all references to AuthPlayer objects through an HashMap
 */
public class AuthPlayerManager {

    private HashMap<String, AuthPlayer> players;

    public AuthPlayerManager() {
        players = new HashMap<>();
    }

    public void addAuthPlayer(AuthPlayer player) {
        players.put(player.getName(), player);
    }

    public void addAuthPlayer(ProxiedPlayer player) {
        addAuthPlayer(new AuthPlayer(player.getName()));
    }

    public void removeAuthPlayer(String name) {
        players.remove(name);
    }

    public void removeAuthPlayer(ProxiedPlayer player) {
        removeAuthPlayer(player.getName());
    }

    public AuthPlayer getAuthPlayer(String name) {
        return players.get(name);
    }

    public AuthPlayer getAuthPlayer(ProxiedPlayer player) {
        return getAuthPlayer(player.getName());
    }
}
