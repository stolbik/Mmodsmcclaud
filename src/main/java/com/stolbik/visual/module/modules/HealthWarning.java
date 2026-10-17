package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.HudRenderable;
import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.NumSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Pulsing red edges on screen when health is low. */
public class HealthWarning extends Module implements HudRenderable {
    private final NumSetting threshold = add(new NumSetting("Hearts", 4, 1, 10, 1));

    public HealthWarning() {
        super("Low health warning", "Red pulse at the edges when health is low", Category.VISUAL);
    }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.player.getHealth() > threshold.get() * 2 || !mc.player.isAlive()) {
            return;
        }
        double pulse = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 250.0);
        int a = (int) (40 + 80 * pulse);
        int color = (a << 24) | 0xE02020;
        int w = ctx.getScaledWindowWidth();
        int h = ctx.getScaledWindowHeight();
        int t = 14;
        ctx.fill(0, 0, w, t, color);
        ctx.fill(0, h - t, w, h, color);
        ctx.fill(0, t, t, h - t, color);
        ctx.fill(w - t, t, w, h - t, color);
    }
}
