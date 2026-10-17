package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.HudRenderable;
import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.BoolSetting;
import com.stolbik.visual.module.setting.ModeSetting;
import com.stolbik.visual.module.setting.NumSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Extra custom crosshair drawn on top of the vanilla one. */
public class Crosshair extends Module implements HudRenderable {
    private final ModeSetting style = add(new ModeSetting("Style", "Cross", "Cross", "Dot", "Circle"));
    private final NumSetting size = add(new NumSetting("Size", 4, 1, 12, 1));
    private final NumSetting gap = add(new NumSetting("Gap", 2, 0, 8, 1));
    private final BoolSetting rainbow = add(new BoolSetting("Rainbow", false));

    public Crosshair() {
        super("Crosshair", "Custom crosshair overlay", Category.VISUAL);
    }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient mc = mc();
        if (mc.options.hudHidden || mc.currentScreen != null) {
            return;
        }
        int cx = ctx.getScaledWindowWidth() / 2;
        int cy = ctx.getScaledWindowHeight() / 2;
        int color = rainbow.get() ? java.awt.Color.HSBtoRGB((System.currentTimeMillis() % 4000) / 4000f, 0.8f, 1f) | 0xFF000000 : 0xFFB69CFF;
        int s = size.getInt();
        int g = gap.getInt();
        switch (style.get()) {
            case "Dot" -> ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, color);
            case "Circle" -> {
                int r = s + g;
                for (int i = 0; i < 24; i++) {
                    double a = Math.PI * 2 * i / 24;
                    int px = cx + (int) Math.round(Math.cos(a) * r);
                    int py = cy + (int) Math.round(Math.sin(a) * r);
                    ctx.fill(px, py, px + 1, py + 1, color);
                }
            }
            default -> {
                ctx.fill(cx - g - s, cy, cx - g, cy + 1, color);
                ctx.fill(cx + g + 1, cy, cx + g + s + 1, cy + 1, color);
                ctx.fill(cx, cy - g - s, cx + 1, cy - g, color);
                ctx.fill(cx, cy + g + 1, cx + 1, cy + g + s + 1, color);
            }
        }
    }
}
