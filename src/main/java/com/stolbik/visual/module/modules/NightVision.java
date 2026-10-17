package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class NightVision extends Module {
    public NightVision() {
        super("Night Vision", "See clearly in the dark", Category.VISUAL);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc().player;
        if (player == null) {
            return;
        }
        StatusEffectInstance current = player.getStatusEffect(StatusEffects.NIGHT_VISION);
        if (current == null || current.getDuration() < 300) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 600, 0, false, false, false));
        }
    }

    @Override
    protected void onDisable() {
        ClientPlayerEntity player = mc().player;
        if (player != null) {
            player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }
}
