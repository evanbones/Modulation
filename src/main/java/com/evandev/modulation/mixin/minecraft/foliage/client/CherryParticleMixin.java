package com.evandev.modulation.mixin.minecraft.foliage.client;

import com.evandev.modulation.modules.vanillabackport.client.LeafFlingAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=26.3 {
/*import net.minecraft.client.particle.FallingParticle;
*///?} else if >=26.1 {
/*import net.minecraft.client.particle.FallingLeavesParticle;
*///?} else {
import net.minecraft.client.particle.CherryParticle;
//?}

//? if >=26.3 {
/*@Mixin(FallingParticle.class)
*///?} else if >=26.1 {
/*@Mixin(FallingLeavesParticle.class)
*///?} else {
@Mixin(CherryParticle.class)
//?}
public abstract class CherryParticleMixin implements LeafFlingAccess {

    @Unique
    private boolean modulation$flung;

    @Override
    public boolean modulation$isFlung() {
        return this.modulation$flung;
    }

    @Override
    public void modulation$setFlung(boolean flung) {
        this.modulation$flung = flung;
    }

    //? if >=26.3 {
    /*@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/FallingParticle;remove()V", ordinal = 1), cancellable = true)
    *///?} else if >=26.1 {
    /*@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/FallingLeavesParticle;remove()V", ordinal = 1), cancellable = true)
    *///?} else {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/CherryParticle;remove()V", ordinal = 1), cancellable = true)
    //?}
    private void modulation$keepFlungAlive(CallbackInfo ci) {
        if (this.modulation$flung && !((ParticleAccessor) this).modulation$isOnGround()) {
            ci.cancel();
        }
    }
}
