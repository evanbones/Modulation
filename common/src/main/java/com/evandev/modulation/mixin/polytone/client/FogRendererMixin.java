package com.evandev.modulation.mixin.polytone.client;

import com.evandev.modulation.client.HorizonFogState;
import com.evandev.modulation.client.SkyExposure;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void modulation$captureTerrainFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean shouldCreateFog, float partialTick, CallbackInfo ci) {
        if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
            HorizonFogState.capture(RenderSystem.getShaderFogStart(), RenderSystem.getShaderFogEnd(), modulation$skyVisibility(camera));
        }
    }

    @Unique
    private static float modulation$skyVisibility(Camera camera) {
        if (camera.getFluidInCamera() != FogType.NONE) {
            return 0.0F;
        }

        Entity entity = camera.getEntity();

        if (entity instanceof LivingEntity livingEntity
                && (livingEntity.hasEffect(MobEffects.BLINDNESS) || livingEntity.hasEffect(MobEffects.DARKNESS))) {
            return 0.0F;
        }

        return SkyExposure.localSkyLight(entity.level(), camera.getBlockPosition());
    }
}
