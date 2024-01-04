package gg.voided.cosmo.tablist.adapter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Bars {
    FIVE(149),
    FOUR(299),
    THREE(599),
    TWO(999),
    ONE(1001),
    NONE(-1);

    private final int ping;
}
