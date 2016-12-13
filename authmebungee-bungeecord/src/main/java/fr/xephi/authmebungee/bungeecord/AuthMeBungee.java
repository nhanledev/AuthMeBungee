package fr.xephi.authmebungee.bungeecord;

import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import fr.xephi.authmebungee.bungeecord.annotations.DataFolder;
import fr.xephi.authmebungee.bungeecord.annotations.IncomingChannel;
import fr.xephi.authmebungee.bungeecord.annotations.OutgoingChannel;
import fr.xephi.authmebungee.bungeecord.commands.ReloadCommand;
import fr.xephi.authmebungee.bungeecord.config.Settings;
import fr.xephi.authmebungee.bungeecord.config.SettingsProvider;
import fr.xephi.authmebungee.bungeecord.services.AuthPlayerManager;
import fr.xephi.authmebungee.bungeecord.listeners.PlayerListener;
import fr.xephi.authmebungee.bungeecord.listeners.ServerListener;
import fr.xephi.authmebungee.bungeecord.services.PluginMessageSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class AuthMeBungee extends Plugin {

    // Constants
    private final static String INCOMING_CHANNEL = "AuthMeBungee";
    private final static String OUTGOING_CHANNEL = "AuthMeBungeeProxy";

    // Instances
    private Injector injector;
    private Settings settings;
    private AuthPlayerManager authPlayerManager;
    private PluginMessageSender pluginMessageSender;

    public AuthMeBungee() {
    }

    @Override
    public void onEnable() {

        // Prepare the injector and register stuff
        setupInjector();

        // Get singletons from the injector
        settings = injector.getSingleton(Settings.class);
        authPlayerManager = injector.getSingleton(AuthPlayerManager.class);
        pluginMessageSender = injector.getSingleton(PluginMessageSender.class);

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand());

        // Register plugin channels
        getProxy().registerChannel(INCOMING_CHANNEL);
        getProxy().registerChannel(OUTGOING_CHANNEL);

        // Registering event listeners
        getProxy().getPluginManager().registerListener(this, injector.getSingleton(ServerListener.class));
        getProxy().getPluginManager().registerListener(this, injector.getSingleton(PlayerListener.class));
    }

    private void setupInjector() {
        // Setup injector
        injector = new InjectorBuilder().addDefaultHandlers("fr.xephi.authmebungee.bungee").create();
        injector.register(AuthMeBungee.class, this);
        injector.register(ProxyServer.class, getProxy());
        injector.register(PluginManager.class, getProxy().getPluginManager());
        injector.register(TaskScheduler.class, getProxy().getScheduler());
        injector.provide(IncomingChannel.class, INCOMING_CHANNEL);
        injector.provide(OutgoingChannel.class, OUTGOING_CHANNEL);
        injector.provide(DataFolder.class, getDataFolder());
        injector.registerProvider(Settings.class, SettingsProvider.class);
    }
}
