package com.stolbik.visual.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumSetting extends Setting {
    public final double min;
    public final double max;
    public final double step;
    private double value;

    public NumSetting(String name, double value, double min, double max, double step) {
        super(name);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = clamp(value);
    }

    private double clamp(double v) {
        if (step > 0) {
            v = Math.round(v / step) * step;
        }
        return Math.max(min, Math.min(max, v));
    }

    public double get() {
        return value;
    }

    public int getInt() {
        return (int) Math.round(value);
    }

    public void set(double value) {
        this.value = clamp(value);
    }

    /** Sets the value from a 0..1 position on the slider. */
    public void setFraction(double fraction) {
        set(min + (max - min) * Math.max(0, Math.min(1, fraction)));
    }

    public double fraction() {
        return max == min ? 0 : (value - min) / (max - min);
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(value);
    }

    @Override
    public void load(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsDouble());
        }
    }
}
