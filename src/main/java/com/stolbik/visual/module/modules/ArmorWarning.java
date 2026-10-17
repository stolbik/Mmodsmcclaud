package com.stolbik.visual.module.modules;

import com.stolbik.visual.module.Module;
import com.stolbik.visual.module.setting.NumSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/** Warns when armor durability is low. */
public class ArmorWarning extends Module {
    private final NumSetting percent = add(new NumSetting("Percent", 15, 5, 50, 5));
    private int timer;

    public ArmorWarning() {
        super("Armor warning", "Warns when armor is about to break", Category.UTILITIES);
    }

    @Override
    public void onTick() {
        var mc = mc();
        if (mc.player == null || ++timer < 60) {
            return;
        }
        timer = 0;
        for (int i = 0; i < 4; i++) {
            ItemStack s = mc.player.getInventory().getArmorStack(i);
            if (!s.isEmpty() && s.getMaxDamage() > 0) {
                int left = s.getMaxDamage() - s.getDamage();
                if (left * 100 / s.getMaxDamage() <= percent.getInt()) {
                    mc.inGameHud.setOverlayMessage(Text.literal("Low durability: " + s.getName().getString()), false);
                    return;
                }
            }
        }
    }
}
