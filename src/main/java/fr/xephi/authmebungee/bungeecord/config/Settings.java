package fr.xephi.authmebungee.bungeecord.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.migration.MigrationService;
import ch.jalu.configme.resource.PropertyResource;

/**
 * The AuthMeBungee settings manager.
 */
public class Settings extends SettingsManager {

    /**
     * Constructor.
     *
     * @param resource          the property resource to read and write properties to
     * @param migrationService  migration service to check the settings file with
     * @param configurationData configuration data (properties and comments)
     */
    public Settings(PropertyResource resource, MigrationService migrationService,
                    Class<ConfigProperties> configurationData) {
        super(resource, migrationService, configurationData);
    }
}
