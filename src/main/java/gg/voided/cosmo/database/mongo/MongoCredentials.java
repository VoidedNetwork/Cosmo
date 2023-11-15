package gg.voided.cosmo.database.mongo;

import com.mongodb.ConnectionString;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter @AllArgsConstructor
public class MongoCredentials {
    /**
     * The mongo connection uri.
     */
    @NotNull private final String uri;

    /**
     * The mongo database name.
     */
    @NotNull private final String database;

    /**
     * Gets mongo credentials from a yaml section.
     *
     * @param section The yaml section.
     * @return The mongo credentials.
     */
    @NotNull
    public static MongoCredentials fromSection(@NotNull Section section) {
        Optional<String> uri = section.getOptionalString("uri");
        String database = section.getString("database");

        if (uri.isPresent()) {
            ConnectionString connection = new ConnectionString(uri.get());

            return new MongoCredentials(
                connection.getConnectionString(),
                Optional.ofNullable(connection.getDatabase()).orElse(database)
            );
        }

        String host = section.getString("host");
        Integer port = section.getOptionalInt("port").orElse(27017);
        Optional<String> username = section.getOptionalString("username");
        Optional<String> password = section.getOptionalString("password");

        StringBuilder connection = new StringBuilder("mongodb://");

        if (username.isPresent()) {
            connection.append(username.get());
            password.ifPresent(value -> connection.append(":").append(value));
            connection.append("@");
        }

        connection.append(host).append(":").append(port);
        return new MongoCredentials(connection.toString(), database);
    }
}
