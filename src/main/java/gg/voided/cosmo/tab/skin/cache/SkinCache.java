package gg.voided.cosmo.tab.skin.cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.voided.cosmo.tab.skin.Skin;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("SpellCheckingInspection")
public class SkinCache {
    public static final CachedSkin DEFAULT = new CachedSkin("Default", Skin.DEFAULT.getValue(), Skin.DEFAULT.getSignature());

    private static final String MOJANG_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final String ASHCON_URL = "https://api.ashcon.app/mojang/v2/user/%s";

    private final Map<String, CachedSkin> cache = new ConcurrentHashMap<>();

    public void register(Player player) {
        CompletableFuture<CachedSkin> future = CompletableFuture.supplyAsync(() -> fetch(player, true));

        future.whenComplete((skin, throwable) -> {
            if (skin != null) cache.put(player.getName(), skin);
        });
    }

    public CachedSkin get(Player player) {
        CachedSkin skin = cache.get(player.getName());
        if (skin != null) return skin;

        register(player);
        return DEFAULT;
    }

    public void remove(Player player) {
        cache.remove(player.getName());
    }

    public CachedSkin fetch(Player player, boolean uuid) {
        try {
            return uuid ? fetchUUID(player) : fetchName(player);
        } catch (NullPointerException | IOException exception) {
            if (uuid) return fetch(player, false);
            return DEFAULT;
        }
    }

    private CachedSkin fetchUUID(Player player) throws IOException {
        String uuid = player.getUniqueId().toString();
        URL url = new URL(String.format(MOJANG_URL, uuid));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection.getResponseCode() != 200) throw new IOException();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) builder.append(line);

            JsonElement element = new JsonParser().parse(builder.toString());
            if (!element.isJsonObject()) return null;

            JsonArray properties = element.getAsJsonObject().get("properties").getAsJsonArray();
            JsonObject property = properties.get(0).getAsJsonObject();

            String value = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();

            return new CachedSkin(player.getName(), value, signature);
        }
    }

    private CachedSkin fetchName(Player player) throws IOException {
        String name = player.getName();
        URL url = new URL(String.format(ASHCON_URL, name));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection.getResponseCode() != 200) return new CachedSkin(name, DEFAULT.getValue(), DEFAULT.getSignature());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) builder.append(line);

            JsonElement element = new JsonParser().parse(builder.toString());
            if (!element.isJsonObject()) return null;

            JsonObject textures = element.getAsJsonObject().get("textures").getAsJsonObject();
            JsonObject raw = textures.get("raw").getAsJsonObject();

            String value = raw.get("value").getAsString();
            String signature = raw.get("signature").getAsString();

            return new CachedSkin(name, value, signature);
        }
    }
}
