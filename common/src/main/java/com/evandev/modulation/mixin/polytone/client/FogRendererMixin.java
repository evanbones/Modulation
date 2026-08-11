package com.evandev.modulation.mixin.polytone.client;

import com.evandev.modulation.client.HorizonFogState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void modulation$captureTerrainFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean shouldCreateFog, float partialTick, CallbackInfo ci) {
        if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
            HorizonFogState.capture(RenderSystem.getShaderFogStart(), RenderSystem.getShaderFogEnd());
        }
    }
}
