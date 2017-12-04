package fr.xephi.authmebungee.config;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import fr.xephi.authmebungee.utils.FileUtils;

import javax.inject.Provider;
import java.io.File;

/**
 * Initializes the settings.
 */
public abstract class SettingsProvider implements Provider<SettingsManager> {

    private File dataFolder;

    private Class<? extends SettingsHolder> properties;

    protected SettingsProvider(File dataFolder, Class<? extends SettingsHolder> properties) {
        this.dataFolder = dataFolder;
        this.properties = properties;
    }

    /**
     * Loads the plugin's settings.
     *
     * @return the settings instance, or null if it could not be constructed
     */
    @Override
    public SettingsManager get() {
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            FileUtils.create(configFile);
        }
        PropertyResource resource = new YamlFileResource(configFile);
        return new SettingsManager(resource, new PlainMigrationService(), properties);
    }

}
