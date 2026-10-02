package com.evandev.modulation.mixin.minecraft.bugfix.client;

import com.evandev.modulation.client.ImprovedFog;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
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

    @WrapOperation(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V"
            )
    )
    private void modulation$compositeCapturedClouds(LevelRenderer instance, PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, Operation<Void> original) {
        if (!ImprovedFog.compositeCapturedClouds(instance)) {
            original.call(instance, poseStack, frustumMatrix, projectionMatrix, partialTick, camX, camY, camZ);
        }
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void modulation$endFogFrame(CallbackInfo ci) {
        ImprovedFog.endFrame();
    }

    @WrapOperation(
            method = "renderClouds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;setupRenderState()V")
    )
    private void modulation$redirectCapturedClouds(RenderType renderType, Operation<Void> original) {
        original.call(renderType);
        if (ImprovedFog.isCapturingClouds()) {
            ImprovedFog.bindCloudsTarget();
        }
    }
}
