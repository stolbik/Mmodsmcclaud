package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.BoolSetting;
import com.stolbik.visual.module.setting.NumSetting;
import net.minecraft.client.MinecraftClient;

/** Client-side time of day and weather. */
public class Ambience extends Module {
    private final BoolSetting customTime = add(new BoolSetting("Custom time", true));
    private final NumSetting time = add(new NumSetting("Time (ticks)", 13000, 0, 24000, 500));
    private final BoolSetting clearWeather = add(new BoolSetting("Clear weather", true));

    public Ambience() {
        super("Ambience", "Change time of day and remove rain (client only)", Category.WORLD);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = mc();
        if (mc.world == null) {
            return;
        }
        if (customTime.get()) {
            mc.world.setTimeOfDay((long) time.get());
        }
        if (clearWeather.get()) {
            mc.world.setRainGradient(0f);
            mc.world.setThunderGradient(0f);
        }
    }
}
