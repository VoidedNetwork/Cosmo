package gg.voided.cosmo.tablist.adapter;

import gg.voided.cosmo.tablist.skin.Skin;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

@Getter @Setter @Accessors(chain = true)
public class TabEntry {
    private String content = "";
    private int ping = Bars.NONE.getPing();
    private Skin skin = Skin.DEFAULT;
    private Player player = null;
}
