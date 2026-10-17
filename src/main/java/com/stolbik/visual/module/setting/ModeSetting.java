package com.stolbik.visual.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ModeSetting extends Setting {
    public final String[] modes;
    private int index;

    public ModeSetting(String name, String defaultMode, String... modes) {
        super(name);
        this.modes = modes;
        this.index = 0;
        set(defaultMode);
    }

    public String get() {
        return modes[index];
    }

    public int index() {
        return index;
    }

    public boolean is(String mode) {
        return modes[index].equals(mode);
    }

    public void set(String mode) {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(mode)) {
                index = i;
                return;
            }
        }
    }

    public void next() {
        index = (index + 1) % modes.length;
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(modes[index]);
    }

    @Override
    public void load(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsString());
        }
    }
}
