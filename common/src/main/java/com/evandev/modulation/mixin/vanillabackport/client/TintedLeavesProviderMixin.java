package com.evandev.modulation.mixin.vanillabackport.client;

import com.blackgear.vanillabackport.client.level.particles.FallingLeavesParticle;
import com.blackgear.vanillabackport.client.registries.ModParticles;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.PassableFoliageModule;
import com.evandev.modulation.modules.vanillabackport.client.LeafFling;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ColorParticleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingLeavesParticle.TintedLeavesProvider.class)
public abstract class TintedLeavesProviderMixin {

    @Inject(method = "createParticle(Lnet/minecraft/core/particles/ColorParticleOption;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("RETURN"))
    private void modulation$fling(
            ColorParticleOption type, ClientLevel level, double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir
    ) {
        PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
        if (module != null && module.isPassableFoliageEnabled()) {
            if (type.getType() == ModParticles.TINTED_LEAVES.get() && module.isTintedLeavesEnabled()) {
                LeafFling.apply(cir.getReturnValue(), xSpeed, ySpeed, zSpeed);
            } else if (type.getType() == ModParticles.TINTED_NEEDLES.get() && module.isTintedNeedlesEnabled()) {
                LeafFling.apply(cir.getReturnValue(), xSpeed, ySpeed, zSpeed);
            }
        }
    }
}
