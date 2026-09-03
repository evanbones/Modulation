package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.SkyExposure;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.material.FogType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererSkyMixin {

    @Shadow
    private ClientLevel level;

    @ModifyExpressionValue(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"
            )
    )
    private float[] modulation$skipTwilightRing(float[] original) {
        if (ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixHorizonLineEnabled)) {
            return null;
        }
        return original;
    }

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void modulation$skipCaveSky(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        if (modulation$shouldHideSky(camera)) {
            skyFogSetup.run();
            ci.cancel();
        }
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void modulation$skipCaveClouds(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (modulation$shouldHideSky(Minecraft.getInstance().gameRenderer.getMainCamera())) {
            ci.cancel();
        }
    }

    @Unique
    private boolean modulation$shouldHideSky(Camera camera) {
        if (!ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixCaveSkyEnabled)) {
            return false;
        }

        ClientLevel clientLevel = this.level;
        if (clientLevel == null || !clientLevel.dimensionType().hasSkyLight()) {
            return false;
        }

        if (clientLevel.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL) {
            return false;
        }

        if (camera.getFluidInCamera() != FogType.NONE) {
            return false;
        }

        return SkyExposure.isEnclosed(clientLevel, camera.getBlockPosition());
    }
}
