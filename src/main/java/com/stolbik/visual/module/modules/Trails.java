package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.ModeSetting;
import com.stolbik.visual.module.setting.NumSetting;
import com.stolbik.visual.util.ParticleChoice;
import net.minecraft.client.MinecraftClient;

/** Leaves a particle trail behind the player. */
public class Trails extends Module {
    private final ModeSetting particle = add(new ModeSetting("Particle", "End rod", ParticleChoice.NAMES));
    private final NumSetting amount = add(new NumSetting("Amount", 2, 1, 10, 1));
    private final NumSetting spread = add(new NumSetting("Spread", 0.2, 0, 1, 0.05));

    public Trails() {
        super("Trails", "Particle trail behind the player", Category.WORLD);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null) {
            return;
        }
        double dx = mc.player.getX() - mc.player.prevX;
        double dz = mc.player.getZ() - mc.player.prevZ;
        if (dx * dx + dz * dz < 0.0004 && Math.abs(mc.player.getY() - mc.player.prevY) < 0.02) {
            return;
        }
        var effect = ParticleChoice.of(particle.get());
        double s = spread.get();
        for (int i = 0; i < amount.getInt(); i++) {
            double ox = (Math.random() - 0.5) * s;
            double oy = Math.random() * mc.player.getHeight();
            double oz = (Math.random() - 0.5) * s;
            mc.world.addParticle(effect, mc.player.getX() + ox, mc.player.getY() + oy, mc.player.getZ() + oz, 0, 0.01, 0);
        }
    }
}
