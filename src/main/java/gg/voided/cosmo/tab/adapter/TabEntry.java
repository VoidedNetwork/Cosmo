package gg.voided.cosmo.tab.adapter;

import gg.voided.cosmo.tab.skin.Skin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @RequiredArgsConstructor @Accessors(chain = true)
public class TabEntry {
    private final int x;
    private final int y;
    private String content = "";
    private int ping = Bars.NONE.getPing();
    private Skin skin = Skin.DEFAULT;

    public int getIndex() {
        return y * 4 + x;
    }
}
