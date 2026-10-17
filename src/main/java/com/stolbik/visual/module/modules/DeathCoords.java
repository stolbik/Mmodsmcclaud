package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.Text;

/** Writes your death coordinates to chat. */
public class DeathCoords extends Module {
    private boolean reported;

    public DeathCoords() {
        super("Death coords", "Prints coordinates in chat when you die", Category.UTILITIES);
    }

    @Override
    public void onTick() {
        var mc = mc();
        if (mc.player == null) {
            return;
        }
        if (mc.currentScreen instanceof DeathScreen) {
            if (!reported) {
                reported = true;
                mc.player.sendMessage(Text.literal(String.format("[Stolbik] You died at %d %d %d",
                        (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ())), false);
            }
        } else {
            reported = false;
        }
    }
}
