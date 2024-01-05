package gg.voided.cosmo.tab.adapter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@Getter @RequiredArgsConstructor
public abstract class TabAdapter {
    private final int updateTicks;

    public abstract String getHeader(Player player);
    public abstract String getFooter(Player player);
    public abstract List<TabEntry> getEntries(Player player);
}
