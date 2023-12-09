package gg.voided.cosmo.countdown;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public abstract class Countdown {
    private final JavaPlugin plugin;

    /**
     * Seconds remaining.
     */
    @Getter @Setter private int seconds;

    /**
     * If the countdown is running.
     */
    @Getter private boolean running;
    private BukkitTask task;

    /**
     * Starts the countdown.
     *
     * @param seconds How long the countdown should last.
     * @throws IllegalStateException If there is a running countdown.
     */
    public void start(int seconds) {    
        if (running) throw new IllegalStateException("Tried to start a running countdown.");
        this.seconds = seconds;
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0, 20);
        running = true;
        onStart();
    }

    /**
     * Ticks the countdown.
     */
    private void tick() {
        onTick(seconds);
        if (seconds-- == 0) finish();
    }

    /**
     * Resets the countdown.
     */
    private void reset() {
        task.cancel();
        running = false;
    }

    /**
     * Resets the countdown and calls onFinish.
     */
    private void finish() {
        reset();
        onFinish();
    }

    /**
     * Cancels the running timer.
     *
     * @throws IllegalStateException If there is no running timer.
     */
    public void cancel() {
        if (!running) throw new IllegalStateException("Tried to cancel a stopped countdown.");
        reset();
        onCancel();
    }

    /**
     * Called when the countdown starts.
     */
    protected abstract void onStart();

    /**
     * Called when the countdown ticks.
     *
     * @param seconds Remaining seconds until the countdown finishes.
     */
    protected abstract void onTick(int seconds);

    /**
     * Called when the countdown finishes.
     */
    protected abstract void onFinish();

    /**
     * Called if the countdown gets cancelled.
     */
    protected abstract void onCancel();
}
