package fr.xephi.authmebungee.bungeecord.config;

import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import fr.xephi.authmebungee.bungeecord.annotations.DataFolder;
import fr.xephi.authmebungee.bungeecord.utils.FileUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;

/**
 * Initializes the settings.
 */
public class SettingsProvider implements Provider<Settings> {

    @Inject
    @DataFolder
    private File dataFolder;

    SettingsProvider() {
    }

    /**
     * Loads the plugin's settings.
     *
     * @return the settings instance, or null if it could not be constructed
     */
    @Override
    public Settings get() {
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            FileUtils.create(configFile);
        }
        PropertyResource resource = new YamlFileResource(configFile);
        return new Settings(resource, new PlainMigrationService(), ConfigProperties.class);
    }
}
