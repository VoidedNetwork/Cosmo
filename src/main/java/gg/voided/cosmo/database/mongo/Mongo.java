package gg.voided.cosmo.database.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

@Getter
public class Mongo {
    /**
     * The mongo client.
     */
    @NotNull private final MongoClient client;

    /**
     * The default database.
     */
    @NotNull private final MongoDatabase database;

    /**
     * The mongo credentials.
     */
    @NotNull private final MongoCredentials credentials;

    /**
     * Creates a mongo client and loads the default
     * database using the provided credentials.
     *
     * @param credentials The mongo credentials.
     */
    public Mongo(@NotNull MongoCredentials credentials) {
        this.credentials = credentials;
        this.client = MongoClients.create(credentials.getUri());
        this.database = this.client.getDatabase(credentials.getDatabase());
    }

    /**
     * Gets a non default mongo database by name.
     *
     * @param name The database name.
     * @return The database.
     */
    @NotNull
    public MongoDatabase getDatabase(@NotNull String name) {
        return this.client.getDatabase(name);
    }

    /**
     * Gets a mongo collection by name.
     *
     * @param name The collection name.
     * @return The collection.
     */
    @NotNull
    public MongoCollection<Document> getCollection(@NotNull String name) {
        return this.database.getCollection(name);
    }

    /**
     * Closes the mongo client.
     */
    public void close() {
        this.client.close();
    }
}
