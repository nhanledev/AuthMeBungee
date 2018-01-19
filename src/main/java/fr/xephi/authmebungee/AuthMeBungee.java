package fr.xephi.authmebungee;

import ch.jalu.configme.SettingsManager;
import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import fr.xephi.authmebungee.annotations.DataFolder;
import fr.xephi.authmebungee.commands.BungeeReloadCommand;
import fr.xephi.authmebungee.config.BungeeConfigProperties;
import fr.xephi.authmebungee.config.BungeeSettingsProvider;
import fr.xephi.authmebungee.listeners.BungeeMessageListener;
import fr.xephi.authmebungee.listeners.BungeePlayerListener;
import fr.xephi.authmebungee.services.AuthPlayerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.bstats.bungeecord.Metrics;

import java.util.logging.Logger;

public class AuthMeBungee extends Plugin {

    // Instances
    private Injector injector;
    private SettingsManager settings;
    private AuthPlayerManager authPlayerManager;

    public AuthMeBungee() {
    }

    @Override
    public void onEnable() {
        // Prepare the injector and register stuff
        setupInjector();

        // Get singletons from the injector
        settings = injector.getSingleton(SettingsManager.class);
        authPlayerManager = injector.getSingleton(AuthPlayerManager.class);

        // Print some config information
        getLogger().info("Current auth servers:");
        for (String authServer : settings.getProperty(BungeeConfigProperties.AUTH_SERVERS)) {
            getLogger().info("> " + authServer.toLowerCase());
        }

        // Add online players (plugin hotswap, just in case)
        for (ProxiedPlayer player : getProxy().getPlayers()) {
            authPlayerManager.addAuthPlayer(player);
        }

        // Register commands
        getProxy().getPluginManager().registerCommand(this, injector.getSingleton(BungeeReloadCommand.class));

        // Registering event listeners
        getProxy().getPluginManager().registerListener(this, injector.getSingleton(BungeeMessageListener.class));
        getProxy().getPluginManager().registerListener(this, injector.getSingleton(BungeePlayerListener.class));

        // Send metrics data
        new Metrics(this);
    }

    private void setupInjector() {
        // Setup injector
        injector = new InjectorBuilder().addDefaultHandlers("").create();
        injector.register(Logger.class, getLogger());
        injector.register(AuthMeBungee.class, this);
        injector.register(ProxyServer.class, getProxy());
        injector.register(PluginManager.class, getProxy().getPluginManager());
        injector.register(TaskScheduler.class, getProxy().getScheduler());
        injector.provide(DataFolder.class, getDataFolder());
        injector.registerProvider(SettingsManager.class, BungeeSettingsProvider.class);
    }

}
