package gg.voided.cosmo.reload;

import lombok.Getter;

@Getter
public class ReloadResult {
    private final long duration;
    private final boolean success;

    /**
     * Creates a new reload result.
     *
     * @param start The {@link System#currentTimeMillis()} from before the reload.
     * @param success If the reload was successful.
     */
    public ReloadResult(long start, boolean success) {
        this.duration = System.currentTimeMillis() - start;
        this.success = success;
    }
}
