package gg.voided.cosmo.tab.listeners;

import gg.voided.cosmo.tab.TabHandler;
import gg.voided.cosmo.tab.adapter.TabAdapter;
import gg.voided.cosmo.tab.layout.Layout;
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
        TabAdapter adapter = handler.getAdapter();

        layout.initialize();

        layout.update(
            adapter.getHeader(player),
            adapter.getFooter(player),
            adapter.getEntries(player)
        );

        handler.getLayouts().put(player, new Layout(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handler.getLayouts().remove(event.getPlayer());
    }
}
