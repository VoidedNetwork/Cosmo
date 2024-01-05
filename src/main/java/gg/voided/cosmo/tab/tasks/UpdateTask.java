package gg.voided.cosmo.tab.tasks;

import gg.voided.cosmo.tab.TabHandler;
import gg.voided.cosmo.tab.adapter.TabAdapter;
import gg.voided.cosmo.tab.layout.Layout;
import gg.voided.cosmo.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class UpdateTask extends BukkitRunnable {
    private final TabHandler handler;

    @Override
    public void run() {
        TabAdapter adapter = handler.getAdapter();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Layout layout = handler.getLayouts().get(player);
            if (layout == null) continue;

            try {
                layout.update(
                    adapter.getHeader(player),
                    adapter.getFooter(player),
                    adapter.getEntries(player)
                );
            } catch (Exception exception) {
                String message = "Failed to update tab for " + player.getName() + ": ";
                handler.getPlugin().getLogger().severe(message + ExceptionUtils.getStackTrace(exception));
            }
        }
    }
}
