package com.stolbik.visual.util;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public final class ParticleChoice {
    public static final String[] NAMES = {"End rod", "Flame", "Heart", "Soul fire", "Glow", "Cloud", "Note", "Crit", "Enchant", "Snowflake", "Spark", "Firework"};

    private ParticleChoice() {
    }

    public static ParticleEffect of(String name) {
        return switch (name) {
            case "Flame" -> ParticleTypes.FLAME;
            case "Heart" -> ParticleTypes.HEART;
            case "Soul fire" -> ParticleTypes.SOUL_FIRE_FLAME;
            case "Glow" -> ParticleTypes.GLOW;
            case "Cloud" -> ParticleTypes.CLOUD;
            case "Note" -> ParticleTypes.NOTE;
            case "Crit" -> ParticleTypes.CRIT;
            case "Enchant" -> ParticleTypes.ENCHANT;
            case "Snowflake" -> ParticleTypes.SNOWFLAKE;
            case "Spark" -> ParticleTypes.ELECTRIC_SPARK;
            case "Firework" -> ParticleTypes.FIREWORK;
            default -> ParticleTypes.END_ROD;
        };
    }
}
