package fr.xephi.authmebungee.spigot.listeners;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import fr.xephi.authmebungee.spigot.services.SpigotMessageSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SpigotAuthMeListener implements Listener {

    private SpigotMessageSender messageSender;

    @Inject
    public SpigotAuthMeListener(SpigotMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAuthMeLogin(LoginEvent event) {
        final Player player = event.getPlayer();
        try(ByteArrayOutputStream bout = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(bout)) {
            out.writeUTF("LOGIN:");
            out.writeUTF(player.getName());
            messageSender.sendData(bout.toByteArray(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAuthMeLogout(LogoutEvent event) {
        final Player player = event.getPlayer();
        try(ByteArrayOutputStream bout = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(bout)) {
            out.writeUTF("LOGOUT:");
            out.writeUTF(player.getName());
            messageSender.sendData(bout.toByteArray(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
