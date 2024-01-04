package gg.voided.cosmo.tablist.skin.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor @Getter @Setter
public class CachedSkin {
    private final String name;
    private final String value;
    private final String signature;
}
