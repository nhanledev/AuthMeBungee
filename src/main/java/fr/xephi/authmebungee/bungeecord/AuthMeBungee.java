package fr.xephi.authmebungee.bungeecord;

import ch.jalu.configme.SettingsManager;
import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import fr.xephi.authmebungee.bungeecord.commands.BungeeReloadCommand;
import fr.xephi.authmebungee.bungeecord.config.BungeeSettingsProvider;
import fr.xephi.authmebungee.bungeecord.listeners.BungeeMessageListener;
import fr.xephi.authmebungee.bungeecord.listeners.BungeePlayerListener;
import fr.xephi.authmebungee.bungeecord.services.AuthPlayerManager;
import fr.xephi.authmebungee.bungeecord.services.BungeeMessageSender;
import fr.xephi.authmebungee.common.annotations.DataFolder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class AuthMeBungee extends Plugin {

    // Instances
    private Injector injector;
    private SettingsManager settings;
    private AuthPlayerManager authPlayerManager;
    private BungeeMessageSender pluginMessageSender;

    public AuthMeBungee() {
    }

    @Override
    public void onEnable() {

        // Prepare the injector and register stuff
        setupInjector();

        // Get singletons from the injector
        settings = injector.getSingleton(SettingsManager.class);
        authPlayerManager = injector.getSingleton(AuthPlayerManager.class);
        pluginMessageSender = injector.getSingleton(BungeeMessageSender.class);

        for(ProxiedPlayer player : getProxy().getPlayers()) {
            authPlayerManager.addAuthPlayer(player);
        }

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new BungeeReloadCommand());

        // Registering event listeners
        getProxy().getPluginManager().registerListener(this, injector.getSingleton(BungeeMessageListener.class));
        getProxy().getPluginManager().registerListener(this, injector.getSingleton(BungeePlayerListener.class));
    }

    @Override
    public void onDisable() {
        // Prevent plugin unload
        getProxy().stop();
    }

    private void setupInjector() {
        // Setup injector
        injector = new InjectorBuilder().addDefaultHandlers("fr.xephi.authmebungee.bungee").create();
        injector.register(AuthMeBungee.class, this);
        injector.register(ProxyServer.class, getProxy());
        injector.register(PluginManager.class, getProxy().getPluginManager());
        injector.register(TaskScheduler.class, getProxy().getScheduler());
        injector.provide(DataFolder.class, getDataFolder());
        injector.registerProvider(SettingsManager.class, BungeeSettingsProvider.class);
    }
}
