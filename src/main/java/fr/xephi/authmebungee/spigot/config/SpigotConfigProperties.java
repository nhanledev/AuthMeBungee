package fr.xephi.authmebungee.spigot.config;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class SpigotConfigProperties implements SettingsHolder {

    private SpigotConfigProperties() {
    }

    @Comment("The message that is sent to the player after a successful auto-login")
    public static final Property<String> AUTOLOGIN_MESSAGE =
        newProperty("autologin.message", "&2Your session has been resumed by the bridge.");
}
