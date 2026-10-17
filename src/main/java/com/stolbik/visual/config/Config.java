package com.stolbik.visual.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.ModuleManager;
import com.stolbik.visual.module.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Config() {
    }

    public static Path dir() {
        return FabricLoader.getInstance().getGameDir().resolve("stolbukvisual");
    }

    private static Path file() {
        return dir().resolve("config.json");
    }

    public static void save() {
        try {
            Files.createDirectories(dir());
            JsonObject root = new JsonObject();
            for (Module m : ModuleManager.all()) {
                JsonObject mo = new JsonObject();
                mo.addProperty("enabled", m.isEnabled());
                JsonObject settings = new JsonObject();
                for (Setting s : m.settings()) {
                    settings.add(s.name, s.save());
                }
                mo.add("settings", settings);
                root.add(m.name, mo);
            }
            Files.writeString(file(), GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[stolbikvisual] cannot save config: " + e);
        }
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) {
            return;
        }
        try {
            JsonObject root = GSON.fromJson(Files.readString(f, StandardCharsets.UTF_8), JsonObject.class);
            if (root == null) {
                return;
            }
            for (Module m : ModuleManager.all()) {
                JsonElement el = root.get(m.name);
                if (el == null || !el.isJsonObject()) {
                    continue;
                }
                JsonObject mo = el.getAsJsonObject();
                JsonElement settingsEl = mo.get("settings");
                if (settingsEl != null && settingsEl.isJsonObject()) {
                    JsonObject so = settingsEl.getAsJsonObject();
                    for (Setting s : m.settings()) {
                        s.load(so.get(s.name));
                    }
                }
                JsonElement en = mo.get("enabled");
                if (en != null && en.isJsonPrimitive()) {
                    m.restoreEnabled(en.getAsBoolean());
                }
            }
        } catch (Exception e) {
            System.err.println("[stolbikvisual] cannot load config: " + e);
        }
    }
}
