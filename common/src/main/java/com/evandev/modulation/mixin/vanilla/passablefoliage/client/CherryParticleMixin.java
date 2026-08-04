package com.evandev.modulation.mixin.vanilla.passablefoliage.client;

import com.evandev.modulation.modules.vanillabackport.client.LeafFlingAccess;
import net.minecraft.client.particle.CherryParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CherryParticle.class)
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

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/CherryParticle;remove()V", ordinal = 1), cancellable = true)
    private void modulation$keepFlungAlive(CallbackInfo ci) {
        if (this.modulation$flung && !((ParticleAccessor) this).modulation$isOnGround()) {
            ci.cancel();
        }
    }
}
