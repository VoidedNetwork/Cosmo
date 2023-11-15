package gg.voided.cosmo.database.redis;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.JedisPool;

@Getter
public class Redis {

    /**
     * The jedis pool.
     */
    @NotNull private final JedisPool pool;

    /**
     * The redis credentials.
     */
    @NotNull private final RedisCredentials credentials;

    /**
     * Creates a jedis pool using the provided credentials.
     *
     * @param credentials The redis credentials.
     */
    public Redis(@NotNull RedisCredentials credentials) {
        this.credentials = credentials;
        this.pool = new JedisPool(credentials.getUri());
    }

    /**
     * Closes the jedis pool.
     */
    public void close() {
        pool.close();
    }
}
