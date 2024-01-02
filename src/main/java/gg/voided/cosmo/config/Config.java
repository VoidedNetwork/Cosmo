package gg.voided.cosmo.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@UtilityClass
public class Config {

    /**
     * Creates and loads a {@link YamlDocument} which ignores default values.
     *
     * @param filename The file name of the config file.
     * @param plugin The plugin the config file belongs to.
     * @return The loaded {@link YamlDocument}.
     * @throws IOException If the config file couldn't be loaded.
     */
    @NotNull
    public YamlDocument create(@NotNull String filename, @NotNull JavaPlugin plugin) throws IOException {
        return YamlDocument.create(
            new File(plugin.getDataFolder(), filename),
            Objects.requireNonNull(plugin.getResource(filename), "Couldn't find " + filename + " in jar."),
            GeneralSettings.builder().setUseDefaults(false).build(),
            LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT
        );
    }

    /**
     * Creates and loads a {@link YamlDocument} which ignores default values.
     *
     * @param filename The file name of the config file.
     * @param folder The folder to create the config in.
     * @param resource The default config input stream.
     * @return The loaded {@link YamlDocument}.
     * @throws IOException If the config file couldn't be loaded.
     */
    @NotNull
    public YamlDocument create(@NotNull String filename, @NotNull File folder, InputStream resource) throws IOException {
        return YamlDocument.create(
            new File(folder, filename),
            Objects.requireNonNull(resource, "Couldn't find " + filename + " in jar."),
            GeneralSettings.builder().setUseDefaults(false).build(),
            LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT
        );
    }
}
