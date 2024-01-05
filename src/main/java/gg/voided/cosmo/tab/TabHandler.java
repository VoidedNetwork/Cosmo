package gg.voided.cosmo.tab;

import com.github.retrooper.packetevents.PacketEventsAPI;
import gg.voided.cosmo.tab.adapter.TabAdapter;
import gg.voided.cosmo.tab.layout.Layout;
import gg.voided.cosmo.tab.listeners.ConnectionListener;
import gg.voided.cosmo.tab.listeners.SkinCacheListener;
import gg.voided.cosmo.tab.skin.cache.SkinCache;
import gg.voided.cosmo.tab.tasks.UpdateTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class TabHandler {
    @Getter private static TabHandler instance;

    @Getter private final JavaPlugin plugin;
    @Getter private final PacketEventsAPI<?> packetEvents;
    @Getter private final HashMap<Player, Layout> layouts = new HashMap<>();
    @Getter private final SkinCache skinCache = new SkinCache();
    @Getter private TabAdapter adapter;

    private final ConnectionListener connectionListener;
    private final SkinCacheListener skinCacheListener;
    private UpdateTask task;

    public TabHandler(PacketEventsAPI<?> packetEvents, JavaPlugin plugin) {
        instance = this;

        this.plugin = plugin;
        this.packetEvents = packetEvents;

        this.connectionListener = new ConnectionListener();
        this.skinCacheListener = new SkinCacheListener();

        Bukkit.getPluginManager().registerEvents(connectionListener, plugin);
        Bukkit.getPluginManager().registerEvents(skinCacheListener, plugin);
    }

    public void setAdapter(TabAdapter adapter) {
        int update = adapter.getUpdateTicks();

        if (adapter.getUpdateTicks() < 20) {
            plugin.getLogger().warning("Tab adapter update ticks is too low, setting to 20 ticks.");
            update = 20;
        }

        this.adapter = adapter;

        if (this.task != null) this.task.cancel();
        this.task = new UpdateTask(this);
        this.task.runTaskTimerAsynchronously(plugin, 0, update);
    }

    public void clean() {
        HandlerList.unregisterAll(this.connectionListener);
        HandlerList.unregisterAll(this.skinCacheListener);
        task.cancel();
    }
}
