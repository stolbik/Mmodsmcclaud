package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Hud extends Module {
    private final BoolSetting watermark = add(new BoolSetting("Watermark", true));
    private final BoolSetting fps = add(new BoolSetting("FPS", true));
    private final BoolSetting coords = add(new BoolSetting("Coordinates", false));

    public Hud() {
        super("HUD", "Watermark, FPS and coordinates on screen", Category.VISUAL);
        restoreEnabled(true);
    }

    public void render(DrawContext context) {
        MinecraftClient mc = mc();
        if (mc.options.hudHidden || mc.player == null) {
            return;
        }
        int y = 4;
        if (watermark.get()) {
            context.drawTextWithShadow(mc.textRenderer, "stolbik visual", 4, y, 0xFFB69CFF);
            y += 11;
        }
        if (fps.get()) {
            context.drawTextWithShadow(mc.textRenderer, mc.getCurrentFps() + " FPS", 4, y, 0xFFFFFFFF);
            y += 11;
        }
        if (coords.get()) {
            String text = String.format("XYZ %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            context.drawTextWithShadow(mc.textRenderer, text, 4, y, 0xFFFFFFFF);
        }
    }
}
