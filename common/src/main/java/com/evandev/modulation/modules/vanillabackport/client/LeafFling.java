package com.evandev.modulation.modules.vanillabackport.client;

import com.evandev.modulation.mixin.vanilla.passablefoliage.client.ParticleAccessor;
import net.minecraft.client.particle.Particle;

public final class LeafFling {

    private LeafFling() {
    }

    public static void apply(Particle particle, double vx, double vy, double vz) {
        if (particle == null || (vx == 0.0 && vy == 0.0 && vz == 0.0)) {
            return;
        }
        particle.setParticleSpeed(vx, vy, vz);
        ((ParticleAccessor) particle).modulation$setGravity(0.012F);
        ((ParticleAccessor) particle).modulation$setFriction(0.92F);
        if (particle instanceof LeafFlingAccess access) {
            access.modulation$setFlung(true);
        }
    }
}
