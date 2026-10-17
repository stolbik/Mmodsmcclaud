package com.stolbik.visual;

import com.stolbik.visual.config.Config;
import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.ModuleManager;
import com.stolbik.visual.module.modules.Hud;
import com.stolbik.visual.ui.ModuleScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class StolbikVisual implements ClientModInitializer {
    private static KeyBinding openGui;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        Config.load();

        openGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.stolbikvisual.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.stolbikvisual"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGui.wasPressed()) {
                client.setScreen(new ModuleScreen());
            }
            ModuleManager.tick();
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            for (Module m : ModuleManager.all()) {
                if (m instanceof Hud hud && hud.isEnabled()) {
                    hud.render(context);
                }
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.save());
    }
}
