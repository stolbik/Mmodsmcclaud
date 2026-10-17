package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("Auto Sprint", "Sprint automatically while moving forward", Category.UTILITIES);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc().player;
        if (player == null || player.isSneaking() || player.horizontalCollision) {
            return;
        }
        if (player.forwardSpeed > 0 && player.getHungerManager().getFoodLevel() > 6) {
            player.setSprinting(true);
        }
    }
}
