package gg.voided.cosmo.metadata;

import lombok.experimental.UtilityClass;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@UtilityClass
public class Metadata {

    /**
     * Gets a metadata value from an object using a key and the owning plugin.
     *
     * @param object The object with metadata.
     * @param key The metadata key.
     * @param plugin The owning plugin.
     * @return An optional of the returned metadata value.
     */
    @NotNull
    public Optional<MetadataValue> get(@NotNull Metadatable object, @NotNull String key, @NotNull JavaPlugin plugin) throws ClassCastException {
        for (MetadataValue data : object.getMetadata(key)) {
            if (data.getOwningPlugin() != plugin) continue;
            return Optional.of(data);
        }

        return Optional.empty();
    }

    /**
     * Checks if the object has a metadata value using a key and the owning plugin.
     *
     * @param object The object with metadata.
     * @param key The metadata key.
     * @param plugin The owning plugin.
     * @return If the metadata value exists.
     */
    public boolean has(@NotNull Metadatable object, @NotNull String key, @NotNull JavaPlugin plugin) {
        return get(object, key, plugin).isPresent();
    }

    /**
     * Sets an objects metadata value using a key, value and owning plugin.
     *
     * @param object The object to set metadata.
     * @param key The metadata key.
     * @param value The metadata value.
     * @param plugin The owning plugin.
     */
    public void set(@NotNull Metadatable object, @NotNull String key, @NotNull Object value, @NotNull JavaPlugin plugin) {
        object.setMetadata(key, new FixedMetadataValue(plugin, value));
    }
}
