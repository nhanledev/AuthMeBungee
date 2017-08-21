package fr.xephi.authmebungee.spigot.config;

import fr.xephi.authmebungee.common.annotations.DataFolder;
import fr.xephi.authmebungee.common.config.SettingsProvider;
import java.io.File;
import javax.inject.Inject;

public class SpigotSettingsProvider extends SettingsProvider {

    @Inject
    SpigotSettingsProvider(@DataFolder File dataFolder) {
        super(dataFolder, SpigotConfigProperties.class);
    }
}
