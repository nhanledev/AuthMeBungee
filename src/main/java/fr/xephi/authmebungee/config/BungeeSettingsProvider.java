package fr.xephi.authmebungee.config;

import fr.xephi.authmebungee.annotations.DataFolder;

import javax.inject.Inject;
import java.io.File;

public class BungeeSettingsProvider extends SettingsProvider {

    @Inject
    public BungeeSettingsProvider(@DataFolder File dataFolder) {
        super(dataFolder, BungeeConfigProperties.class);
    }

}
