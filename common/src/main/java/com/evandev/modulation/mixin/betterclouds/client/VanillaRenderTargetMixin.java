package com.evandev.modulation.mixin.betterclouds.client;

import com.evandev.modulation.client.ImprovedFog;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.qendolin.betterclouds.clouds.VanillaRenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("betterclouds")
@Mixin(value = VanillaRenderTarget.class, remap = false)
public abstract class VanillaRenderTargetMixin {

    @Inject(method = "begin", at = @At("RETURN"))
    private void modulation$redirectCapturedClouds(CallbackInfo ci) {
        if (ImprovedFog.isCapturingClouds()) {
            ImprovedFog.bindCloudsTarget();
        }
    }
}
