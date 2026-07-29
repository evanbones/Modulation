package com.evandev.modulation.mixin.vanillabackport.client;

import com.blackgear.vanillabackport.client.level.particles.FallingLeavesParticle;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.PassableFoliageModule;
import com.evandev.modulation.modules.vanillabackport.client.LeafFling;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingLeavesParticle.PaleOakProvider.class)
public abstract class PaleOakProviderMixin {

    @Inject(method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("RETURN"))
    private void modulation$fling(
            SimpleParticleType type, ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir
    ) {
        PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
        if (module != null && module.isPassableFoliageEnabled() && module.isPaleOakLeavesEnabled()) {
            LeafFling.apply(cir.getReturnValue(), xSpeed, ySpeed, zSpeed);
        }
    }
}
