package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("Auto respawn", "Respawns automatically after death", Category.UTILITIES);
    }

    @Override
    public void onTick() {
        var mc = mc();
        if (mc.player != null && mc.currentScreen instanceof DeathScreen) {
            mc.player.requestRespawn();
        }
    }
}
