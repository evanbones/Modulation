package com.evandev.modulation.mixin.vanilla.softleaves.client;

import com.evandev.modulation.api.ModuleManager;
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
            PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
            if (module != null && module.isPassableFoliageEnabled() && module.isCherryLeavesEnabled()) {
                LeafFling.apply(cir.getReturnValue(), xSpeed, ySpeed, zSpeed);
            }
        }
    }
}
