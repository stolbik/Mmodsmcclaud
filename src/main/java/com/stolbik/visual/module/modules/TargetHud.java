package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.NumSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;

/** Panel with the name and health of the entity you look at or just hit. */
public class TargetHud extends Module implements com.stolbik.visual.module.HudRenderable {
    private final NumSetting offsetX = add(new NumSetting("Offset X", 20, -200, 200, 5));
    private final NumSetting offsetY = add(new NumSetting("Offset Y", 40, -100, 200, 5));
    private LivingEntity target;
    private long lastSeen;

    public TargetHud() {
        super("TargetHUD", "Shows name and health of your target", Category.VISUAL);
    }

    public void setTarget(Entity entity) {
        if (entity instanceof LivingEntity living) {
            target = living;
            lastSeen = System.currentTimeMillis();
        }
    }

    @Override
    public void onTick() {
        MinecraftClient mc = mc();
        if (mc.crosshairTarget instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity living) {
            target = living;
            lastSeen = System.currentTimeMillis();
        }
        if (target != null && (target.isRemoved() || !target.isAlive() || System.currentTimeMillis() - lastSeen > 3000)) {
            target = null;
        }
    }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient mc = mc();
        if (target == null || mc.player == null || mc.options.hudHidden) {
            return;
        }
        int w = 130;
        int h = 36;
        int x = ctx.getScaledWindowWidth() / 2 + offsetX.getInt();
        int y = ctx.getScaledWindowHeight() / 2 + offsetY.getInt();
        ctx.fill(x, y, x + w, y + h, 0xCC15151D);
        ctx.fill(x, y, x + 2, y + h, 0xFFB69CFF);
        ctx.drawTextWithShadow(mc.textRenderer, target.getName().getString(), x + 6, y + 4, 0xFFFFFFFF);
        float hp = target.getHealth();
        float max = Math.max(1f, target.getMaxHealth());
        float frac = Math.max(0f, Math.min(1f, hp / max));
        ctx.fill(x + 6, y + 18, x + w - 6, y + 24, 0xFF2A2A35);
        int color = frac > 0.6f ? 0xFF4ADE80 : frac > 0.3f ? 0xFFFACC15 : 0xFFEF4444;
        ctx.fill(x + 6, y + 18, x + 6 + (int) ((w - 12) * frac), y + 24, color);
        String text = String.format("%.1f / %.1f   %.1fm", hp, max, mc.player.distanceTo(target));
        ctx.drawTextWithShadow(mc.textRenderer, text, x + 6, y + 26, 0xFFCCCCCC);
    }
}
