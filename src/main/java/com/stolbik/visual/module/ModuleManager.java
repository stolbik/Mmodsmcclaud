package com.stolbik.visual.module;

import com.stolbik.visual.module.modules.AutoSprint;
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
    }

    public static List<Module> all() {
        return MODULES;
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
