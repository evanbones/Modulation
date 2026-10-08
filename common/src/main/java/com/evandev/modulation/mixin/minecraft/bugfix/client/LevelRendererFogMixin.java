package com.evandev.modulation.mixin.minecraft.bugfix.client;

import com.evandev.modulation.client.ImprovedFog;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererFogMixin {

    @WrapOperation(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/FogRenderer;setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZF)V"
            )
    )
    private void modulation$captureFogBackground(
            Camera camera,
            FogRenderer.FogMode fogMode,
            float farPlaneDistance,
            boolean shouldCreateFog,
            float partialTick,
            Operation<Void> original,
            @Local(argsOnly = true, ordinal = 0) Matrix4f frustumMatrix,
            @Local(argsOnly = true, ordinal = 1) Matrix4f projectionMatrix
    ) {
        original.call(camera, fogMode, farPlaneDistance, shouldCreateFog, partialTick);
        if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
            ImprovedFog.captureBackground((LevelRenderer) (Object) this, camera, frustumMatrix, projectionMatrix, partialTick);
        }
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void modulation$endFogFrame(CallbackInfo ci) {
        ImprovedFog.endFrame();
    }
}
