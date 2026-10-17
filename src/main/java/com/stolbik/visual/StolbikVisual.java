package com.stolbik.visual;

import com.stolbik.visual.config.Config;
import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.ModuleManager;
import com.stolbik.visual.module.modules.Hud;
import com.stolbik.visual.module.modules.HitParticles;
import com.stolbik.visual.module.modules.TargetHud;
import com.stolbik.visual.ui.ModuleScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
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
                if (!m.isEnabled()) {
                    continue;
                }
                if (m instanceof com.stolbik.visual.module.HudRenderable r) {
                    r.render(context);
                }
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient()) {
                TargetHud th = ModuleManager.get(TargetHud.class);
                if (th != null && th.isEnabled()) {
                    th.setTarget(entity);
                }
                com.stolbik.visual.module.modules.HitSound hs = ModuleManager.get(com.stolbik.visual.module.modules.HitSound.class);
                if (hs != null && hs.isEnabled()) {
                    hs.onAttack();
                }
                HitParticles hp = ModuleManager.get(HitParticles.class);
                if (hp != null && hp.isEnabled()) {
                    hp.onAttack(entity);
                }
            }
            return ActionResult.PASS;
        });

        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof TitleScreen || screen instanceof GameMenuScreen) {
                Screens.getButtons(screen).add(ButtonWidget.builder(Text.literal("Stolbik Visual"),
                        b -> client.setScreen(new ModuleScreen())).dimensions(6, 6, 100, 20).build());
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.save());
    }
}
