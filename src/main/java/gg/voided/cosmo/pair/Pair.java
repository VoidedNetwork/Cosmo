package gg.voided.cosmo.pair;

import lombok.Data;

@Data
public class Pair<A, B> {
    private final A first;
    private final B second;
}
