package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.NumSetting;
import net.minecraft.client.MinecraftClient;

/** Narrows the field of view while enabled. */
public class Zoom extends Module {
    private final NumSetting fov = add(new NumSetting("FOV", 30, 10, 70, 1));
    private Integer original;

    public Zoom() {
        super("Zoom", "Zoom in (lower FOV) while enabled", Category.UTILITIES);
    }

    @Override
    protected void onEnable() {
        MinecraftClient mc = mc();
        original = mc.options.getFov().getValue();
    }

    @Override
    public void onTick() {
        MinecraftClient mc = mc();
        int v = Math.max(30, fov.getInt());
        if (mc.options.getFov().getValue() != v) {
            mc.options.getFov().setValue(v);
        }
    }

    @Override
    protected void onDisable() {
        if (original != null) {
            mc().options.getFov().setValue(original);
            original = null;
        }
    }
}
