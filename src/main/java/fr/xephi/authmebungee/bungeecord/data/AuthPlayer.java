package fr.xephi.authmebungee.bungeecord.data;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.inject.Inject;

public class AuthPlayer {

    @Inject
    private ProxyServer proxy;

    private String name;
    private boolean isLogged;

    public AuthPlayer(String name, boolean isLogged) {
        this.name = name;
        this.isLogged = isLogged;
    }

    public AuthPlayer(String name) {
        this(name, false);
    }

    public String getName() {
        return name;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public ProxiedPlayer getPlayer() {
        return proxy.getPlayer(getName());
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }
}
