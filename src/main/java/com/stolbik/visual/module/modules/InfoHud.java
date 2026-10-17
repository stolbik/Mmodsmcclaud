package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.HudRenderable;
import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.BoolSetting;
import com.stolbik.visual.module.setting.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** Info widgets: speed, ping, direction, biome, clock, memory, armor, potions, keystrokes, CPS. */
public class InfoHud extends Module implements HudRenderable {
    private final ModeSetting side = add(new ModeSetting("Side", "Right", "Right", "Left"));
    private final BoolSetting speed = add(new BoolSetting("Speed (b/s)", true));
    private final BoolSetting ping = add(new BoolSetting("Ping", true));
    private final BoolSetting direction = add(new BoolSetting("Direction", true));
    private final BoolSetting biome = add(new BoolSetting("Biome", false));
    private final BoolSetting clock = add(new BoolSetting("Clock", true));
    private final BoolSetting memory = add(new BoolSetting("Memory", false));
    private final BoolSetting cps = add(new BoolSetting("CPS", true));
    private final BoolSetting armor = add(new BoolSetting("Armor durability", true));
    private final BoolSetting potions = add(new BoolSetting("Potion effects", true));
    private final BoolSetting keystrokes = add(new BoolSetting("Keystrokes", false));

    private final ArrayDeque<Long> clicks = new ArrayDeque<>();
    private boolean wasAttack;

    public InfoHud() {
        super("Info HUD", "Speed, ping, direction, clock, armor, potions, keystrokes, CPS", Category.VISUAL);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = mc();
        boolean now = mc.options.attackKey.isPressed();
        if (now && !wasAttack) {
            clicks.addLast(System.currentTimeMillis());
        }
        wasAttack = now;
        long cutoff = System.currentTimeMillis() - 1000;
        while (!clicks.isEmpty() && clicks.peekFirst() < cutoff) {
            clicks.pollFirst();
        }
    }

    @Override
    public void render(DrawContext ctx) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null || mc.options.hudHidden) {
            return;
        }
        List<String> lines = new ArrayList<>();
        if (speed.get()) {
            double dx = mc.player.getX() - mc.player.prevX;
            double dz = mc.player.getZ() - mc.player.prevZ;
            lines.add(String.format("Speed: %.2f b/s", Math.sqrt(dx * dx + dz * dz) * 20));
        }
        if (ping.get() && mc.getNetworkHandler() != null) {
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (e != null) {
                lines.add("Ping: " + e.getLatency() + " ms");
            }
        }
        if (direction.get()) {
            lines.add("Facing: " + mc.player.getHorizontalFacing().asString());
        }
        if (biome.get()) {
            String b = mc.world.getBiome(mc.player.getBlockPos()).getKey().map(k -> k.getValue().getPath()).orElse("?");
            lines.add("Biome: " + b);
        }
        if (clock.get()) {
            lines.add("Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
        if (memory.get()) {
            Runtime r = Runtime.getRuntime();
            lines.add("Memory: " + (r.totalMemory() - r.freeMemory()) / 1048576 + " / " + r.maxMemory() / 1048576 + " MB");
        }
        if (cps.get()) {
            lines.add("CPS: " + clicks.size());
        }
        if (armor.get()) {
            for (int i = 3; i >= 0; i--) {
                ItemStack s = mc.player.getInventory().getArmorStack(i);
                if (!s.isEmpty() && s.getMaxDamage() > 0) {
                    lines.add(s.getName().getString() + ": " + (s.getMaxDamage() - s.getDamage()) + "/" + s.getMaxDamage());
                }
            }
        }
        if (potions.get()) {
            for (StatusEffectInstance fx : mc.player.getStatusEffects()) {
                int sec = fx.getDuration() / 20;
                lines.add(fx.getEffectType().value().getName().getString() + " " + (fx.getAmplifier() + 1)
                        + String.format(" %d:%02d", sec / 60, sec % 60));
            }
        }
        boolean right = side.is("Right");
        int sw = ctx.getScaledWindowWidth();
        int y = 4;
        for (String line : lines) {
            int w = mc.textRenderer.getWidth(line);
            int x = right ? sw - w - 6 : 4;
            ctx.fill(x - 2, y - 1, x + w + 2, y + 10, 0x88101018);
            ctx.drawTextWithShadow(mc.textRenderer, line, x, y, 0xFFFFFFFF);
            y += 12;
        }
        if (keystrokes.get()) {
            drawKeys(ctx, mc, right ? sw - 70 : 6, ctx.getScaledWindowHeight() - 70);
        }
    }

    private void drawKeys(DrawContext ctx, MinecraftClient mc, int x, int y) {
        key(ctx, mc, "W", mc.options.forwardKey.isPressed(), x + 22, y);
        key(ctx, mc, "A", mc.options.leftKey.isPressed(), x, y + 22);
        key(ctx, mc, "S", mc.options.backKey.isPressed(), x + 22, y + 22);
        key(ctx, mc, "D", mc.options.rightKey.isPressed(), x + 44, y + 22);
        key(ctx, mc, "_", mc.options.jumpKey.isPressed(), x + 22, y + 44);
    }

    private void key(DrawContext ctx, MinecraftClient mc, String label, boolean down, int x, int y) {
        ctx.fill(x, y, x + 20, y + 20, down ? 0xCCB69CFF : 0x88101018);
        ctx.drawTextWithShadow(mc.textRenderer, label, x + 7, y + 6, 0xFFFFFFFF);
    }
}
