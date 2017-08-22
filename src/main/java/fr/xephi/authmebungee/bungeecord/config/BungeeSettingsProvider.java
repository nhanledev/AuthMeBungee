package fr.xephi.authmebungee.bungeecord.config;

import fr.xephi.authmebungee.common.annotations.DataFolder;
import fr.xephi.authmebungee.common.config.SettingsProvider;
import java.io.File;
import javax.inject.Inject;

public class BungeeSettingsProvider extends SettingsProvider {

    @Inject
    public BungeeSettingsProvider(@DataFolder File dataFolder) {
        super(dataFolder, BungeeConfigProperties.class);
    }
}
