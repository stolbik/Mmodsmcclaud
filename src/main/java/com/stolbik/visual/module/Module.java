package com.stolbik.visual.module;

import com.stolbik.visual.module.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public enum Category {
        VISUAL("Visual"),
        WORLD("World"),
        UTILITIES("Utilities");

        public final String title;

        Category(String title) {
            this.title = title;
        }
    }

    public final String name;
    public final String description;
    public final Category category;
    private boolean enabled;
    private final List<Setting> settings = new ArrayList<>();

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    protected <T extends Setting> T add(T setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting> settings() {
        return settings;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        if (value == enabled) {
            return;
        }
        enabled = value;
        try {
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        } catch (Throwable t) {
            System.err.println("[stolbikvisual] module " + name + " failed: " + t);
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    /** Sets the saved state without calling onEnable (used by config loading). */
    public void restoreEnabled(boolean value) {
        setEnabled(value);
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void onTick() {
    }
}
