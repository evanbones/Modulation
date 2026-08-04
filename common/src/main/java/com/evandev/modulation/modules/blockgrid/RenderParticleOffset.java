package com.evandev.modulation.modules.blockgrid;

import net.minecraft.world.phys.Vec3;

public final class RenderParticleOffset {
    private static Vec3 current = Vec3.ZERO;

    private RenderParticleOffset() {
    }

    public static void begin(Vec3 offset) {
        current = offset;
    }

    public static void end() {
        current = Vec3.ZERO;
    }

    public static Vec3 current() {
        return current;
    }
}
