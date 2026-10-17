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

/**
 * Touch-friendly menu: one centered panel with category tabs and a scrollable list.
 * Tap a module to toggle it, tap the "..." button to show its settings, drag to scroll.
 */
public class ModuleScreen extends Screen {
    private static final int ROW = 20;
    private static final int TAB_H = 22;
    private static final int GEAR_W = 26;

    private static int tab;
    private static double scroll;
    private static Module opened;

    private int px, pw, top, bottom, listTop, listBottom;
    private NumSetting dragging;
    private double dragStartX;
    private double dragStartY;
    private boolean scrolling;

    public ModuleScreen() {
        super(Text.literal("Stolbik Visual"));
    }

    @Override
    protected void init() {
        pw = Math.min(340, Math.max(220, (int) (width * 0.56)));
        px = (width - pw) / 2;
        top = 6;
        bottom = height - 6;
        listTop = top + TAB_H + 4;
        listBottom = bottom - ROW - 4;
    }

    private Module.Category[] cats() {
        return Module.Category.values();
    }

    private List<Object> rows() {
        List<Object> out = new ArrayList<>();
        for (Module m : ModuleManager.byCategory(cats()[tab])) {
            out.add(m);
            if (m == opened) {
                out.addAll(m.settings());
            }
        }
        return out;
    }

    private double maxScroll() {
        return Math.max(0, rows().size() * ROW - (listBottom - listTop));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x99000000);
        ctx.fill(px - 4, top - 4, px + pw + 4, bottom + 4, 0xEE0E0E14);

        int tw = pw / cats().length;
        for (int i = 0; i < cats().length; i++) {
            int x = px + i * tw;
            ctx.fill(x, top, x + tw - 2, top + TAB_H, i == tab ? 0xFF6D4AD8 : 0xFF22222C);
            String t = cats()[i].title;
            ctx.drawTextWithShadow(textRenderer, t, x + (tw - 2 - textRenderer.getWidth(t)) / 2, top + 7, 0xFFFFFFFF);
        }

        scroll = Math.max(0, Math.min(maxScroll(), scroll));
        ctx.enableScissor(px, listTop, px + pw, listBottom);
        int y = listTop - (int) scroll;
        for (Object row : rows()) {
            if (row instanceof Module m) {
                ctx.fill(px, y, px + pw, y + ROW - 1, m.isEnabled() ? 0xFF3A2A7A : 0xFF1A1A22);
                ctx.drawTextWithShadow(textRenderer, m.name, px + 6, y + 6, m.isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
                if (!m.settings().isEmpty()) {
                    ctx.fill(px + pw - GEAR_W, y, px + pw, y + ROW - 1, m == opened ? 0xFF6D4AD8 : 0xFF2C2C38);
                    ctx.drawTextWithShadow(textRenderer, "...", px + pw - GEAR_W + 8, y + 5, 0xFFFFFFFF);
                }
            } else if (row instanceof Setting s) {
                ctx.fill(px + 6, y, px + pw, y + ROW - 1, 0xFF121218);
                ctx.drawTextWithShadow(textRenderer, label(s), px + 12, y + 6, 0xFFDDDDDD);
                if (s instanceof NumSetting n) {
                    int bw = (int) ((pw - 18) * n.fraction());
                    ctx.fill(px + 9, y + ROW - 4, px + 9 + bw, y + ROW - 2, 0xFFB69CFF);
                }
            }
            y += ROW;
        }
        ctx.disableScissor();

        ctx.fill(px, bottom - ROW, px + pw, bottom, 0xFF2A1A3A);
        String close = "Close";
        ctx.drawTextWithShadow(textRenderer, close, px + (pw - textRenderer.getWidth(close)) / 2, bottom - ROW + 6, 0xFFFFFFFF);
    }

    private String label(Setting s) {
        if (s instanceof BoolSetting b) {
            return s.name + ": " + (b.get() ? "ON" : "OFF");
        }
        if (s instanceof ModeSetting mo) {
            return s.name + ": " + mo.get();
        }
        if (s instanceof NumSetting n) {
            return s.name + ": " + (n.step >= 1 ? String.valueOf(n.getInt()) : String.format("%.2f", n.get()));
        }
        return s.name;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        dragStartX = mx;
        dragStartY = my;
        scrolling = false;
        dragging = null;

        if (my >= bottom - ROW && my <= bottom && mx >= px && mx <= px + pw) {
            close();
            return true;
        }
        if (my >= top && my < top + TAB_H && mx >= px && mx <= px + pw) {
            int tw = pw / cats().length;
            tab = Math.max(0, Math.min(cats().length - 1, (int) ((mx - px) / tw)));
            scroll = 0;
            opened = null;
            return true;
        }
        if (my >= listTop && my <= listBottom && mx >= px && mx <= px + pw) {
            int index = (int) ((my - listTop + scroll) / ROW);
            List<Object> rows = rows();
            if (index >= 0 && index < rows.size()) {
                Object row = rows.get(index);
                if (row instanceof NumSetting n) {
                    dragging = n;
                    n.setFraction((mx - (px + 9)) / (pw - 18));
                } else if (row instanceof Setting) {
                    // BoolSetting / ModeSetting are applied on release, so scrolling by touch does not flip them
                }
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging != null) {
            dragging.setFraction((mx - (px + 9)) / (pw - 18));
            return true;
        }
        if (my >= listTop - 20 && my <= listBottom + 20) {
            if (Math.abs(my - dragStartY) > 4 || scrolling) {
                scrolling = true;
                scroll = Math.max(0, Math.min(maxScroll(), scroll - dy));
            }
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        boolean wasSlider = dragging != null;
        dragging = null;
        boolean moved = scrolling || Math.abs(mx - dragStartX) > 6 || Math.abs(my - dragStartY) > 6;
        scrolling = false;
        if (!wasSlider && !moved && my >= listTop && my <= listBottom && mx >= px && mx <= px + pw) {
            int index = (int) ((my - listTop + scroll) / ROW);
            List<Object> rows = rows();
            if (index >= 0 && index < rows.size()) {
                Object row = rows.get(index);
                if (row instanceof Module m) {
                    if (mx >= px + pw - GEAR_W && !m.settings().isEmpty()) {
                        opened = (opened == m) ? null : m;
                    } else {
                        m.toggle();
                    }
                } else if (row instanceof BoolSetting b) {
                    b.toggle();
                } else if (row instanceof ModeSetting mo) {
                    mo.next();
                }
            }
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        scroll = Math.max(0, Math.min(maxScroll(), scroll - vertical * ROW));
        return true;
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
