package fr.xephi.authmebungee.spigot;

import ch.jalu.configme.SettingsManager;
import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authmebungee.common.annotations.DataFolder;
import fr.xephi.authmebungee.spigot.commands.SpigotReloadCommand;
import fr.xephi.authmebungee.spigot.config.SpigotSettingsProvider;
import fr.xephi.authmebungee.spigot.listeners.SpigotAuthMeListener;
import fr.xephi.authmebungee.spigot.listeners.SpigotMessageListener;
import fr.xephi.authmebungee.spigot.services.SpigotMessageSender;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AuthMeBungeeSpigot extends JavaPlugin {

    // Instances
    private Injector injector;
    private SettingsManager settings;
    private SpigotMessageSender pluginMessageSender;

    public AuthMeBungeeSpigot() {
    }

    @Override
    public void onEnable() {

        // Prepare the injector and register stuff
        setupInjector();

        // Get singletons from the injector
        settings = injector.getSingleton(SettingsManager.class);
        pluginMessageSender = injector.getSingleton(SpigotMessageSender.class);

        // Register commands
        getCommand("abreload").setExecutor(injector.getSingleton(SpigotReloadCommand.class));

        // Registering event listeners
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", injector.getSingleton(SpigotMessageListener.class));
        getServer().getPluginManager().registerEvents(injector.getSingleton(SpigotAuthMeListener.class), this);
    }

    private void setupInjector() {
        // Setup injector
        injector = new InjectorBuilder().addDefaultHandlers("fr.xephi.authmebungee.spigot").create();
        injector.register(AuthMeBungeeSpigot.class, this);
        injector.register(Server.class, getServer());
        injector.register(PluginManager.class, getServer().getPluginManager());
        injector.provide(DataFolder.class, getDataFolder());
        injector.registerProvider(SettingsManager.class, SpigotSettingsProvider.class);
        injector.register(AuthMeApi.class, AuthMeApi.getInstance());
    }
}
