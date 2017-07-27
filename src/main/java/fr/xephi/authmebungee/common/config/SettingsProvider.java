package fr.xephi.authmebungee.common.config;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyResource;
import ch.jalu.configme.resource.YamlFileResource;
import fr.xephi.authmebungee.common.annotations.DataFolder;
import fr.xephi.authmebungee.common.utils.FileUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;

/**
 * Initializes the settings.
 */
public abstract class SettingsProvider implements Provider<SettingsManager> {

    @Inject
    @DataFolder
    private File dataFolder;

    private Class<? extends SettingsHolder> properties;

    protected SettingsProvider(Class<? extends SettingsHolder> properties) {
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
