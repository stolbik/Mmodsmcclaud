package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.ModeSetting;
import com.stolbik.visual.module.setting.NumSetting;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

/** Decorative particles around the player: snow, sakura, ash, fireflies, sparks. */
public class AmbientParticles extends Module {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Snow", "Snow", "Sakura", "Ash", "Fireflies", "Sparks"));
    private final NumSetting amount = add(new NumSetting("Amount", 4, 1, 20, 1));
    private final NumSetting radius = add(new NumSetting("Radius", 8, 3, 20, 1));

    public AmbientParticles() {
        super("Ambient particles", "Snow / sakura / ash / fireflies around you", Category.WORLD);
    }

    @Override
    public void onTick() {
        var mc = mc();
        if (mc.player == null || mc.world == null) {
            return;
        }
        ParticleEffect effect = switch (mode.get()) {
            case "Sakura" -> ParticleTypes.CHERRY_LEAVES;
            case "Ash" -> ParticleTypes.ASH;
            case "Fireflies" -> ParticleTypes.END_ROD;
            case "Sparks" -> ParticleTypes.ELECTRIC_SPARK;
            default -> ParticleTypes.SNOWFLAKE;
        };
        double r = radius.get();
        for (int i = 0; i < amount.getInt(); i++) {
            double x = mc.player.getX() + (Math.random() * 2 - 1) * r;
            double y = mc.player.getY() + Math.random() * r;
            double z = mc.player.getZ() + (Math.random() * 2 - 1) * r;
            mc.world.addParticle(effect, x, y, z, 0, -0.02, 0);
        }
    }
}
