package gg.voided.cosmo.tablist.listeners;

import gg.voided.cosmo.tablist.TabHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SkinCacheListener implements Listener {
    private final TabHandler handler = TabHandler.getInstance();

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        handler.getSkinCache().register(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handler.getSkinCache().remove(event.getPlayer());
    }
}
