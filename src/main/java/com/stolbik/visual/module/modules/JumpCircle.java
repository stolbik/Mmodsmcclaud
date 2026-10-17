package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.ModeSetting;
import com.stolbik.visual.module.setting.NumSetting;
import com.stolbik.visual.util.ParticleChoice;
import net.minecraft.client.MinecraftClient;

/** Ring of particles under the player when jumping. */
public class JumpCircle extends Module {
    private final ModeSetting particle = add(new ModeSetting("Particle", "Spark", ParticleChoice.NAMES));
    private final NumSetting radius = add(new NumSetting("Radius", 1.2, 0.5, 3, 0.1));
    private final NumSetting points = add(new NumSetting("Points", 24, 8, 64, 2));
    private boolean wasOnGround = true;

    public JumpCircle() {
        super("Jump circle", "Particle ring when you jump", Category.VISUAL);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null) {
            return;
        }
        boolean onGround = mc.player.isOnGround();
        if (wasOnGround && !onGround && mc.player.getVelocity().y > 0.1) {
            var effect = ParticleChoice.of(particle.get());
            int n = points.getInt();
            double r = radius.get();
            for (int i = 0; i < n; i++) {
                double a = Math.PI * 2 * i / n;
                mc.world.addParticle(effect, mc.player.getX() + Math.cos(a) * r, mc.player.getY() + 0.05,
                        mc.player.getZ() + Math.sin(a) * r, Math.cos(a) * 0.05, 0.01, Math.sin(a) * 0.05);
            }
        }
        wasOnGround = onGround;
    }
}
