package gg.voided.cosmo.tablist;

import com.github.retrooper.packetevents.PacketEventsAPI;
import gg.voided.cosmo.tablist.adapter.TabAdapter;
import gg.voided.cosmo.tablist.layout.Layout;
import gg.voided.cosmo.tablist.listeners.ConnectionListener;
import gg.voided.cosmo.tablist.listeners.SkinCacheListener;
import gg.voided.cosmo.tablist.skin.cache.SkinCache;
import gg.voided.cosmo.tablist.tasks.UpdateTask;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TabHandler {
    @Getter private static TabHandler instance;

    private final JavaPlugin plugin;
    private final PacketEventsAPI<?> packetEvents;
    private final Map<Player, Layout> layouts = new ConcurrentHashMap<>();
    private final SkinCache skinCache = new SkinCache();

    private final ConnectionListener listener;

    private UpdateTask task;
    private TabAdapter adapter;

    public TabHandler(PacketEventsAPI<?> packetEvents, JavaPlugin plugin) {
        instance = this;

        this.plugin = plugin;
        this.packetEvents = packetEvents;

        this.listener = new ConnectionListener();

        Bukkit.getPluginManager().registerEvents(listener, plugin);
        Bukkit.getPluginManager().registerEvents(new SkinCacheListener(), plugin);
    }

    public void setAdapter(TabAdapter adapter) {
        int update = adapter.getUpdateTicks();

        if (adapter.getUpdateTicks() < 20) {
            plugin.getLogger().info("Tab adapter update ticks is too low, setting to 20 ticks.");
            update = 20;
        }

        this.adapter = adapter;

        if (this.task != null) this.task.cancel();
        this.task = new UpdateTask(this);
        this.task.runTaskTimerAsynchronously(plugin, 0, update);
    }

    public void clean() {
        HandlerList.unregisterAll(this.listener);

        for (Player player : layouts.keySet()) {
            Team team = player.getScoreboard().getTeam("tab");
            if (team != null) team.unregister();

            layouts.remove(player);
        }

        task.cancel();
    }
}
