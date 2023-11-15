package gg.voided.cosmo.database;

import gg.voided.cosmo.database.mongo.Mongo;
import gg.voided.cosmo.database.redis.Redis;
import lombok.Getter;

@Getter
public class DatabaseManager {
    /**
     * The mongo client.
     */
    private Mongo mongo;

    /**
     * The redis client.
     */
    private Redis redis;

    /**
     * Connects using a mongo client.
     *
     * @param mongo The mongo client.
     */
    public void connect(Mongo mongo) {
        this.mongo = mongo;
    }

    /**
     * Connects using a redis client.
     *
     * @param redis The redis client;
     */
    public void connect(Redis redis) {
        this.redis = redis;
    }

    /**
     * Closes the database connections.
     */
    public void close() {
        if (mongo != null) mongo.close();
        if (redis != null) redis.close();
    }
}
