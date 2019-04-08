package fr.xephi.authmebungee.services;

import fr.xephi.authmebungee.AuthMeBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class ProxyService {

    private final ProxyServer proxy;
    private final AuthMeBungee plugin;

    @Inject
    ProxyService(ProxyServer proxy, AuthMeBungee plugin) {
        this.proxy = proxy;
        this.plugin = plugin;
    }

    public ScheduledTask schedule(Runnable runnable, long time, TimeUnit timeUnit) {
        return proxy.getScheduler().schedule(plugin, runnable, time, timeUnit);
    }
}
