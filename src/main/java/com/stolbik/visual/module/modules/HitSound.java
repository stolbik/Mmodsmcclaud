package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.ModeSetting;
import com.stolbik.visual.module.setting.NumSetting;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

/** Plays a sound when you hit an entity. */
public class HitSound extends Module {
    private final ModeSetting sound = add(new ModeSetting("Sound", "Orb", "Orb", "Chime", "Arrow", "Level up"));
    private final NumSetting volume = add(new NumSetting("Volume", 1, 0.1, 2, 0.1));
    private final NumSetting pitch = add(new NumSetting("Pitch", 1, 0.5, 2, 0.1));

    public HitSound() {
        super("Hit sound", "Sound effect on hit", Category.UTILITIES);
    }

    public void onAttack() {
        var mc = mc();
        if (mc.player == null) {
            return;
        }
        SoundEvent s = switch (sound.get()) {
            case "Chime" -> SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME;
            case "Arrow" -> SoundEvents.ENTITY_ARROW_HIT_PLAYER;
            case "Level up" -> SoundEvents.ENTITY_PLAYER_LEVELUP;
            default -> SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
        };
        mc.player.playSound(s, (float) volume.get(), (float) pitch.get());
    }
}
