package gg.voided.cosmo.utils;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class MathUtils {

    @SuppressWarnings("SuspiciousNameCombination")
    public int gcd(int x, int y) {
        return (y == 0) ? x : gcd(y, x % y);
    }

    public int gcd(List<Integer> numbers) {
        return numbers.stream().reduce(0, MathUtils::gcd);
    }
}
