package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.ModeSetting;
import com.stolbik.visual.module.setting.NumSetting;
import com.stolbik.visual.util.ParticleChoice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

/** Burst of particles on the entity you hit. */
public class HitParticles extends Module {
    private final ModeSetting particle = add(new ModeSetting("Particle", "Crit", ParticleChoice.NAMES));
    private final NumSetting amount = add(new NumSetting("Amount", 12, 1, 40, 1));

    public HitParticles() {
        super("Hit particles", "Particles when you hit an entity", Category.VISUAL);
    }

    public void onAttack(Entity target) {
        MinecraftClient mc = mc();
        if (mc.world == null) {
            return;
        }
        var effect = ParticleChoice.of(particle.get());
        for (int i = 0; i < amount.getInt(); i++) {
            double ox = (Math.random() - 0.5) * target.getWidth() * 1.5;
            double oy = Math.random() * target.getHeight();
            double oz = (Math.random() - 0.5) * target.getWidth() * 1.5;
            mc.world.addParticle(effect, target.getX() + ox, target.getY() + oy, target.getZ() + oz,
                    ox * 0.3, 0.05, oz * 0.3);
        }
    }
}
