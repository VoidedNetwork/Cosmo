package gg.voided.cosmo.tablist.tasks;

import gg.voided.cosmo.tablist.TabHandler;
import gg.voided.cosmo.tablist.adapter.TabEntry;
import gg.voided.cosmo.tablist.layout.Layout;
import gg.voided.cosmo.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@RequiredArgsConstructor
public class UpdateTask extends BukkitRunnable {
    private final TabHandler handler;

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Layout layout = handler.getLayouts().get(player);
            if (layout == null) continue;

            try {
                List<TabEntry> entries = handler.getAdapter().getEntries(player);
                layout.update(entries);
            } catch (Exception exception) {
                String message = "Couldn't update tab for " + player.getName() + ": ";
                handler.getPlugin().getLogger().severe(message + ExceptionUtils.getStackTrace(exception));
            }
        }
    }
}
