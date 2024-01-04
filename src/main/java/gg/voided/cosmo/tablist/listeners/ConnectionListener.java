package gg.voided.cosmo.tablist.listeners;

import gg.voided.cosmo.tablist.TabHandler;
import gg.voided.cosmo.tablist.layout.Layout;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    private final TabHandler handler = TabHandler.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Layout layout = new Layout(player);
        layout.create();

        handler.getLayouts().put(player, layout);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handler.getLayouts().remove(event.getPlayer());
    }
}
