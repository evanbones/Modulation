package com.evandev.modulation.mixin.minecraft.foliage.client;

import com.evandev.modulation.modules.vanilla.PassableFoliageModule;
import com.evandev.modulation.modules.vanillabackport.client.LeafFling;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
public abstract class CherryProviderMixin {

    @Inject(method = "makeParticle", at = @At("RETURN"))
    private void modulation$flingCherry(
            ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir
    ) {
        if (options != null && options.getType() == ParticleTypes.CHERRY_LEAVES) {
            if (PassableFoliageModule.ENABLE_CHERRY_LEAVES.on()) {
                LeafFling.apply(cir.getReturnValue(), xSpeed, ySpeed, zSpeed);
            }
        }
    }
}
