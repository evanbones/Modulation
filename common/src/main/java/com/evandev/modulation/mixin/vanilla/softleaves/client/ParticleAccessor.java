package com.evandev.modulation.mixin.vanilla.softleaves.client;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface ParticleAccessor {

    @Accessor("onGround")
    boolean modulation$isOnGround();

    @Accessor("gravity")
    void modulation$setGravity(float gravity);

    @Accessor("friction")
    void modulation$setFriction(float friction);
}
