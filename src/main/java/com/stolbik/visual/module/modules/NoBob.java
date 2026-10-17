package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;

public class NoBob extends Module {
    private Boolean original;

    public NoBob() {
        super("No view bobbing", "Disables camera bobbing while walking", Category.UTILITIES);
    }

    @Override
    protected void onEnable() {
        original = mc().options.getBobView().getValue();
        mc().options.getBobView().setValue(false);
    }

    @Override
    protected void onDisable() {
        if (original != null) {
            mc().options.getBobView().setValue(original);
            original = null;
        }
    }
}
