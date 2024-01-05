package gg.voided.cosmo.tab.skin;

import gg.voided.cosmo.tab.TabHandler;
import gg.voided.cosmo.tab.skin.cache.CachedSkin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @Getter
public class Skin {
    public static final Skin DEFAULT = new Skin(SkinColor.GRAY.getValue(), SkinColor.GRAY.getSignature());

    private final String value;
    private final String signature;

    public static Skin get(Player player) {
        CachedSkin skin = TabHandler.getInstance().getSkinCache().get(player);
        return new Skin(skin.getValue(), skin.getSignature());
    }
}
