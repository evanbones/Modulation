package com.evandev.modulation.mixin.minecraft.bugfix.client;

import com.evandev.modulation.client.ImprovedFog;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 100)
public abstract class LevelRendererCloudCompositeMixin {

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void modulation$compositeCapturedClouds(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (ImprovedFog.interceptCloudCall((LevelRenderer) (Object) this)) {
            ci.cancel();
        }
    }
}
