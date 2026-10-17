package com.stolbik.visual.module;

import com.stolbik.visual.module.modules.InfoHud;
import com.stolbik.visual.module.modules.Crosshair;
import com.stolbik.visual.module.modules.HealthWarning;
import com.stolbik.visual.module.modules.DeathCoords;
import com.stolbik.visual.module.modules.AutoRespawn;
import com.stolbik.visual.module.modules.ArmorWarning;
import com.stolbik.visual.module.modules.AmbientParticles;
import com.stolbik.visual.module.modules.HitSound;
import com.stolbik.visual.module.modules.NoBob;
import com.stolbik.visual.module.modules.Ambience;
import com.stolbik.visual.module.modules.AutoSprint;
import com.stolbik.visual.module.modules.HitParticles;
import com.stolbik.visual.module.modules.JumpCircle;
import com.stolbik.visual.module.modules.TargetHud;
import com.stolbik.visual.module.modules.Trails;
import com.stolbik.visual.module.modules.Zoom;
import com.stolbik.visual.module.modules.Hud;
import com.stolbik.visual.module.modules.NightVision;
import com.stolbik.visual.module.modules.Optimization;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {
    }

    public static void init() {
        MODULES.add(new Hud());
        MODULES.add(new NightVision());
        MODULES.add(new Optimization());
        MODULES.add(new AutoSprint());
        MODULES.add(new TargetHud());
        MODULES.add(new HitParticles());
        MODULES.add(new JumpCircle());
        MODULES.add(new Trails());
        MODULES.add(new Ambience());
        MODULES.add(new Zoom());
        MODULES.add(new InfoHud());
        MODULES.add(new Crosshair());
        MODULES.add(new HealthWarning());
        MODULES.add(new DeathCoords());
        MODULES.add(new AutoRespawn());
        MODULES.add(new ArmorWarning());
        MODULES.add(new AmbientParticles());
        MODULES.add(new HitSound());
        MODULES.add(new NoBob());
    }

    public static List<Module> all() {
        return MODULES;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> type) {
        for (Module m : MODULES) {
            if (type.isInstance(m)) {
                return (T) m;
            }
        }
        return null;
    }

    public static List<Module> byCategory(Module.Category category) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) {
            if (m.category == category) {
                out.add(m);
            }
        }
        return out;
    }

    public static void tick() {
        for (Module m : MODULES) {
            if (m.isEnabled()) {
                try {
                    m.onTick();
                } catch (Throwable t) {
                    System.err.println("[stolbikvisual] tick error in " + m.name + ": " + t);
                }
            }
        }
    }
}
