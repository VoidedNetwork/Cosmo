package gg.voided.cosmo.utils;

import com.google.common.collect.ImmutableSet;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@UtilityClass
public class ClassUtils {
    /**
     * Gets all the classes in a package.
     *
     * @param name The package name.
     * @param relative A class in the same jar file.
     * @return The classes in the package.
     */
    @NotNull
    public Collection<Class<?>> inPackage(@NotNull String name, @NotNull Class<?> relative) {
        try (JarFile jar = getJar(relative)) {
            Enumeration<JarEntry> entries = jar.entries();
            Collection<Class<?>> classes = new ArrayList<>();
            String path = name.replace(".", "/");

            while (entries.hasMoreElements()) {
                String entry = entries.nextElement().getName();
                if (!entry.endsWith(".class") || !entry.startsWith(path)) continue;

                try {
                    Class<?> clazz = Class.forName(entry.replaceAll("[/\\\\]", ".").replace(".class", ""));
                    classes.add(clazz);
                } catch (ClassNotFoundException exception) {
                    throw new RuntimeException("Couldn't find class: " + exception);
                }
            }

            return ImmutableSet.copyOf(classes);
        } catch (IOException | SecurityException exception) {
            throw new RuntimeException("Couldn't read JAR file: " + exception);
        }
    }

    /**
     * Gets the {@link JarFile} a class is located in.
     *
     * @param clazz The class to get the {@link JarFile} from.
     * @return The {@link JarFile} the class is located in.
     * @throws IOException If an I/O error has occurred.
     * @throws SecurityException If access to the file was denied by a security manager.
     */
    @NotNull
    public JarFile getJar(@NotNull Class<?> clazz) throws IOException, SecurityException {
        CodeSource source = clazz.getProtectionDomain().getCodeSource();
        String resource = source.getLocation().getPath().replace("%20", " ");
        String path = resource.replaceFirst("[.]jar!.*", ".jar").replaceFirst("file:", "");
        return new JarFile(path);
    }

    /**
     * Instantiates a class using its default constructor.
     *
     * @param clazz The class to instantiate.
     * @return The instantiated class.
     * @throws RuntimeException If the constructor doesn't exist, isn't accessible, threw an exception, or the class is abstract.
     */
    @NotNull
    public <T> T instantiate(@NotNull Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Instantiates multiple classes using their default constructor.
     *
     * @param classes The classes to instantiate.
     * @return The instantiated classes.
     * @throws RuntimeException If a class's constructor doesn't exist, isn't accessible, threw an exception, or a class is abstract.
     */
    @NotNull
    public Collection<Object> instantiate(@NotNull Collection<Class<?>> classes) {
        return classes.stream().map(ClassUtils::instantiate).collect(Collectors.toList());
    }

    /**
     * Instantiates all class in a package.
     *
     * @param name The package name.
     * @param relative A class in the same jar file.
     * @return The instantiated classes in the package.
     */
    @NotNull
    public Collection<Object> instantiatePackage(@NotNull String name, @NotNull Class<?> relative) {
        return instantiate(inPackage(name, relative));
    }
}
