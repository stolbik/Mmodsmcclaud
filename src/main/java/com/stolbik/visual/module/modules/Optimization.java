package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.BoolSetting;
import com.stolbik.visual.module.setting.ModeSetting;
import com.stolbik.visual.module.setting.NumSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.particle.ParticlesMode;

import java.util.Objects;

/**
 * Own optimization module. Four presets (Light / Strong / Max) plus Custom.
 * The idea is borrowed from EnoughVisuals; the implementation is written from scratch.
 * Original game options are stored on enable and restored on disable.
 */
public class Optimization extends Module {
    private final ModeSetting mode = add(new ModeSetting("Mode", "Light", "Light", "Strong", "Max", "Custom"));
    private final NumSetting renderDistance = add(new NumSetting("Render distance", 12, 2, 32, 1));
    private final NumSetting simulationDistance = add(new NumSetting("Simulation distance", 8, 5, 32, 1));
    private final NumSetting entityDistance = add(new NumSetting("Entity distance %", 100, 50, 500, 10));
    private final ModeSetting particles = add(new ModeSetting("Particles", "All", "All", "Decreased", "Minimal"));
    private final BoolSetting fastGraphics = add(new BoolSetting("Fast graphics", true));
    private final BoolSetting noAo = add(new BoolSetting("No smooth lighting", false));
    private final BoolSetting noClouds = add(new BoolSetting("No clouds", false));
    private final BoolSetting unlimitedFps = add(new BoolSetting("Unlimited FPS", false));
    private final BoolSetting vsyncOff = add(new BoolSetting("VSync off", false));
    private final BoolSetting threadBoost = add(new BoolSetting("Thread boost", false));

    private String lastMode = "";
    private int lastHash = 0;
    private boolean captured;

    private Integer origView;
    private Integer origSim;
    private Double origEntity;
    private ParticlesMode origParticles;
    private GraphicsMode origGraphics;
    private Boolean origAo;
    private CloudRenderMode origClouds;
    private Integer origFps;
    private Boolean origVsync;
    private int origPriority = Thread.NORM_PRIORITY;

    public Optimization() {
        super("Optimization", "Raises FPS: presets Light / Strong / Max or Custom", Category.UTILITIES);
    }

    private void applyPreset() {
        switch (mode.get()) {
            case "Light" -> preset(12, 10, 100, "All", true, false, false, false, false, false);
            case "Strong" -> preset(8, 6, 75, "Decreased", true, true, true, false, false, false);
            case "Max" -> preset(6, 5, 50, "Minimal", true, true, true, true, true, true);
            default -> {
            }
        }
    }

    private void preset(int view, int sim, int entity, String part, boolean fast, boolean ao,
                        boolean clouds, boolean fps, boolean vsync, boolean boost) {
        renderDistance.set(view);
        simulationDistance.set(sim);
        entityDistance.set(entity);
        particles.set(part);
        fastGraphics.set(fast);
        noAo.set(ao);
        noClouds.set(clouds);
        unlimitedFps.set(fps);
        vsyncOff.set(vsync);
        threadBoost.set(boost);
    }

    private int hash() {
        return Objects.hash(renderDistance.get(), simulationDistance.get(), entityDistance.get(),
                particles.get(), fastGraphics.get(), noAo.get(), noClouds.get(),
                unlimitedFps.get(), vsyncOff.get(), threadBoost.get());
    }

    @Override
    protected void onEnable() {
        capture();
        lastMode = mode.get();
        applyPreset();
        apply();
        lastHash = hash();
    }

    @Override
    protected void onDisable() {
        restore();
    }

    @Override
    public void onTick() {
        // Mode changed in the menu -> load its preset
        if (!mode.get().equals(lastMode)) {
            lastMode = mode.get();
            applyPreset();
            apply();
            lastHash = hash();
            return;
        }
        int h = hash();
        if (h != lastHash) {
            lastHash = h;
            // Manual edit of any value switches to Custom
            if (!mode.is("Custom")) {
                mode.set("Custom");
                lastMode = "Custom";
            }
            apply();
        }
    }

    private void capture() {
        GameOptions o = mc().options;
        origView = o.getViewDistance().getValue();
        origSim = o.getSimulationDistance().getValue();
        origEntity = o.getEntityDistanceScaling().getValue();
        origParticles = o.getParticles().getValue();
        origGraphics = o.getGraphicsMode().getValue();
        origAo = o.getAo().getValue();
        origClouds = o.getCloudRenderMode().getValue();
        origFps = o.getMaxFps().getValue();
        origVsync = o.getEnableVsync().getValue();
        origPriority = Thread.currentThread().getPriority();
        captured = true;
    }

    private void apply() {
        MinecraftClient client = mc();
        GameOptions o = client.options;
        try {
            o.getViewDistance().setValue(renderDistance.getInt());
            o.getSimulationDistance().setValue(simulationDistance.getInt());
            o.getEntityDistanceScaling().setValue(entityDistance.get() / 100.0);
            o.getParticles().setValue(switch (particles.get()) {
                case "Decreased" -> ParticlesMode.DECREASED;
                case "Minimal" -> ParticlesMode.MINIMAL;
                default -> ParticlesMode.ALL;
            });
            if (fastGraphics.get()) {
                o.getGraphicsMode().setValue(GraphicsMode.FAST);
            } else if (captured && origGraphics != null) {
                o.getGraphicsMode().setValue(origGraphics);
            }
            if (noAo.get()) {
                o.getAo().setValue(false);
            } else if (captured && origAo != null) {
                o.getAo().setValue(origAo);
            }
            if (noClouds.get()) {
                o.getCloudRenderMode().setValue(CloudRenderMode.OFF);
            } else if (captured && origClouds != null) {
                o.getCloudRenderMode().setValue(origClouds);
            }
            if (unlimitedFps.get()) {
                o.getMaxFps().setValue(260);
            } else if (captured && origFps != null) {
                o.getMaxFps().setValue(origFps);
            }
            if (vsyncOff.get()) {
                o.getEnableVsync().setValue(false);
            } else if (captured && origVsync != null) {
                o.getEnableVsync().setValue(origVsync);
            }
            Thread.currentThread().setPriority(threadBoost.get() ? Thread.MAX_PRIORITY : origPriority);
        } catch (Throwable t) {
            System.err.println("[stolbikvisual] optimization apply failed: " + t);
        }
    }

    private void restore() {
        if (!captured) {
            return;
        }
        GameOptions o = mc().options;
        try {
            if (origView != null) o.getViewDistance().setValue(origView);
            if (origSim != null) o.getSimulationDistance().setValue(origSim);
            if (origEntity != null) o.getEntityDistanceScaling().setValue(origEntity);
            if (origParticles != null) o.getParticles().setValue(origParticles);
            if (origGraphics != null) o.getGraphicsMode().setValue(origGraphics);
            if (origAo != null) o.getAo().setValue(origAo);
            if (origClouds != null) o.getCloudRenderMode().setValue(origClouds);
            if (origFps != null) o.getMaxFps().setValue(origFps);
            if (origVsync != null) o.getEnableVsync().setValue(origVsync);
            Thread.currentThread().setPriority(origPriority);
        } catch (Throwable t) {
            System.err.println("[stolbikvisual] optimization restore failed: " + t);
        }
        captured = false;
    }
}
