package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.modules.blockgrid.RenderParticleOffset;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientLevel.class)
public class ParticleSpawnOffsetMixin {
    @Unique
    private static final String ADD_PARTICLE = "addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V";

    @ModifyVariable(method = ADD_PARTICLE, at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double modulation$shiftParticleX(double x) {
        return x + RenderParticleOffset.current().x;
    }

    @ModifyVariable(method = ADD_PARTICLE, at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double modulation$shiftParticleY(double y) {
        return y + RenderParticleOffset.current().y;
    }

    @ModifyVariable(method = ADD_PARTICLE, at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private double modulation$shiftParticleZ(double z) {
        return z + RenderParticleOffset.current().z;
    }
}
