package gg.voided.cosmo.database.redis;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


@Getter @AllArgsConstructor
public class RedisCredentials {
    /**
     * The redis connection uri.
     */
    @NotNull private final String uri;

    /**
     * Gets redis credentials from a yaml section.
     *
     * @param section The yaml section.
     * @return The redis credentials.
     */
    public static RedisCredentials fromSection(Section section) {
        Optional<String> uri = section.getOptionalString("uri");
        if (uri.isPresent()) return new RedisCredentials(uri.get());

        String host = section.getString("host");
        Integer port = section.getOptionalInt("port").orElse(6379);
        Optional<String> username = section.getOptionalString("username");
        Optional<String> password = section.getOptionalString("password");

        StringBuilder connection = new StringBuilder("redis://");

        username.ifPresent(value -> connection.append(value).append(":"));
        password.ifPresent(value -> connection.append(password));
        if (username.isPresent() || password.isPresent()) connection.append("@");

        connection.append(host).append(":").append(port);
        return new RedisCredentials(connection.toString());
    }
}
