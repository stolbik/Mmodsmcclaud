package com.stolbik.visual.ui;

import com.stolbik.visual.config.Config;
import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.ModuleManager;
import com.stolbik.visual.module.setting.BoolSetting;
import com.stolbik.visual.module.setting.ModeSetting;
import com.stolbik.visual.module.setting.NumSetting;
import com.stolbik.visual.module.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** Simple module menu: left click toggles a module, right click expands its settings. */
public class ModuleScreen extends Screen {
    private static final int W = 150;
    private static final int ROW = 14;
    private final List<Module> expanded = new ArrayList<>();
    private NumSetting dragging;
    private int dragX;
    private int dragW;

    public ModuleScreen() {
        super(Text.literal("Stolbik Visual"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x88000000);
        int x = 10;
        for (Module.Category cat : Module.Category.values()) {
            ctx.fill(x, 10, x + W, 10 + ROW, 0xFF6D4AD8);
            ctx.drawTextWithShadow(textRenderer, cat.title, x + 4, 13, 0xFFFFFFFF);
            int y = 10 + ROW;
            for (Module m : ModuleManager.byCategory(cat)) {
                ctx.fill(x, y, x + W, y + ROW, m.isEnabled() ? 0xFF3A2A7A : 0xFF1A1A22);
                ctx.drawTextWithShadow(textRenderer, m.name, x + 4, y + 3, m.isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
                y += ROW;
                if (expanded.contains(m)) {
                    for (Setting s : m.settings()) {
                        ctx.fill(x, y, x + W, y + ROW, 0xFF121218);
                        ctx.drawTextWithShadow(textRenderer, label(s), x + 8, y + 3, 0xFFDDDDDD);
                        if (s instanceof NumSetting n) {
                            int bw = (int) ((W - 4) * n.fraction());
                            ctx.fill(x + 2, y + ROW - 2, x + 2 + bw, y + ROW, 0xFFB69CFF);
                        }
                        y += ROW;
                    }
                }
            }
            x += W + 10;
        }
        ctx.drawTextWithShadow(textRenderer, "LMB toggle, RMB settings, click setting to change", 10, height - 14, 0xFF888888);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private String label(Setting s) {
        if (s instanceof BoolSetting b) {
            return s.name + ": " + (b.get() ? "on" : "off");
        }
        if (s instanceof ModeSetting mo) {
            return s.name + ": " + mo.get();
        }
        if (s instanceof NumSetting n) {
            return s.name + ": " + (n.step >= 1 ? String.valueOf(n.getInt()) : String.format("%.1f", n.get()));
        }
        return s.name;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int x = 10;
        for (Module.Category cat : Module.Category.values()) {
            int y = 10 + ROW;
            for (Module m : ModuleManager.byCategory(cat)) {
                if (mx >= x && mx < x + W && my >= y && my < y + ROW) {
                    if (button == 0) {
                        m.toggle();
                    } else if (button == 1) {
                        if (!expanded.remove(m)) {
                            expanded.add(m);
                        }
                    }
                    return true;
                }
                y += ROW;
                if (expanded.contains(m)) {
                    for (Setting s : m.settings()) {
                        if (mx >= x && mx < x + W && my >= y && my < y + ROW) {
                            if (s instanceof BoolSetting b) {
                                b.toggle();
                            } else if (s instanceof ModeSetting mo) {
                                mo.next();
                            } else if (s instanceof NumSetting n) {
                                dragging = n;
                                dragX = x + 2;
                                dragW = W - 4;
                                n.setFraction((mx - dragX) / dragW);
                            }
                            return true;
                        }
                        y += ROW;
                    }
                }
            }
            x += W + 10;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging != null) {
            dragging.setFraction((mx - dragX) / dragW);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        dragging = null;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public void removed() {
        Config.save();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
