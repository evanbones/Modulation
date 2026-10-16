package com.evandev.modulation.mixin.minecraft.fog.client;

//? if >=26.1 {
/*import com.evandev.modulation.client.DistanceFog;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=26.3 {
/^import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;

import java.util.Optional;
import java.util.OptionalDouble;
^///?} else {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
//?}

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow
    private SkyRenderer skyRenderer;

    @Shadow
    @Final
    private CloudRenderer cloudRenderer;

    @Shadow
    @Final
    private LevelTargetBundle targets;

    @Shadow
    @Final
    private OptionsRenderState optionsRenderState;

    //? if >=26.3 {
    /^@Shadow
    @Final
    private LevelRenderState levelRenderState;
    ^///?} else if >=26.2 {
    /^@Shadow
    @Final
    LevelRenderState levelRenderState;
    ^///?} else {
    @Shadow
    @Final
    private LevelRenderState levelRenderState;
    //?}

    //? if >=26.2 {
    /^@Inject(method = "render", at = @At("HEAD"))
    ^///?} else {
    @Inject(method = "renderLevel", at = @At("HEAD"))
    //?}
    private void modulation$beginDistanceFog(CallbackInfo ci) {
        DistanceFog.beginFrame();
    }

    //? if >=26.2 {
    /^@Inject(method = "render", at = @At("RETURN"))
    ^///?} else {
    @Inject(method = "renderLevel", at = @At("RETURN"))
    //?}
    private void modulation$endDistanceFog(CallbackInfo ci) {
        DistanceFog.endFrame();
    }

    //? if >=26.3 {
    /^@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addMainPass"))
    private void modulation$addDistanceFogPass(CallbackInfo ci, @Local FrameGraphBuilder frame, @Local(argsOnly = true) CameraRenderState cameraState,
                                               @Local(argsOnly = true) GpuBufferSlice terrainFog, @Local(argsOnly = true) Vector4f fogColor,
                                               @Local(argsOnly = true, ordinal = 1) boolean shouldRenderSky) {
        if (!DistanceFog.enabled() || Minecraft.getInstance().level == null) {
            return;
        }

        FramePass pass = frame.addPass("modulation_distance_fog");
        this.targets.main = pass.readsAndWrites(this.targets.main);
        pass.executes(() -> {
            RenderSystem.setShaderFog(terrainFog);
            DistanceFog.prepareTargets(fogColor);
            if (shouldRenderSky) {
                DistanceFog.captureSky(() -> this.modulation$renderSky(cameraState, terrainFog));
            }

            CloudStatus cloudStatus = this.optionsRenderState.cloudStatus;
            int cloudColor = this.levelRenderState.cloudColor;
            RenderTarget cloudsTarget = DistanceFog.cloudsTarget();
            if (cloudsTarget != null && cloudStatus != CloudStatus.OFF && ARGB.alpha(cloudColor) > 0) {
                RenderSystem.setShaderFog(terrainFog);
                this.cloudRenderer.prepare(cloudColor, cloudStatus, this.levelRenderState.cloudHeight, this.optionsRenderState.cloudRange,
                        cameraState.pos, this.levelRenderState.gameTime, this.levelRenderState.worldPartialTicks);
                try (RenderPass renderPass = RenderSystem.getDevice()
                        .createCommandEncoder()
                        .createRenderPass(() -> "Modulation distance fog clouds", cloudsTarget.getColorTextureView(), Optional.empty(),
                                cloudsTarget.getDepthTextureView(), OptionalDouble.empty())) {
                    this.cloudRenderer.render(cloudStatus, renderPass);
                }
            }
            RenderSystem.setShaderFog(terrainFog);
        });
    }

    @Unique
    private void modulation$renderSky(CameraRenderState cameraState, GpuBufferSlice skyFog) {
        FogType fogType = cameraState.fogType;
        if (fogType == FogType.POWDER_SNOW || fogType == FogType.LAVA || cameraState.entityRenderState.doesMobEffectBlockSky) {
            return;
        }
        SkyRenderState state = this.levelRenderState.skyRenderState;
        if (this.skyRenderer == null || state.skybox == DimensionType.Skybox.NONE) {
            return;
        }
        this.skyRenderer.render(skyFog, state);
    }
    ^///?} else {
    //? if >=26.2 {
    /^@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addMainPass"))
    ^///?} else {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;addMainPass"))
    //?}
    private void modulation$addDistanceFogPass(CallbackInfo ci, @Local FrameGraphBuilder frame, @Local(argsOnly = true) DeltaTracker deltaTracker,
                                               @Local(argsOnly = true) CameraRenderState cameraState, @Local(argsOnly = true) GpuBufferSlice terrainFog,
                                               @Local(argsOnly = true) Vector4f fogColor, @Local(argsOnly = true, ordinal = 1) boolean shouldRenderSky) {
        if (!DistanceFog.enabled() || Minecraft.getInstance().level == null) {
            return;
        }

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        FramePass pass = frame.addPass("modulation_distance_fog");
        this.targets.main = pass.readsAndWrites(this.targets.main);
        pass.executes(() -> {
            RenderSystem.setShaderFog(terrainFog);
            DistanceFog.prepareTargets(fogColor);
            if (shouldRenderSky) {
                DistanceFog.captureSky(() -> this.modulation$renderSky(cameraState));
            }

            CloudStatus cloudStatus = this.optionsRenderState.cloudStatus;
            int cloudColor = this.levelRenderState.cloudColor;
            if (cloudStatus != CloudStatus.OFF && ARGB.alpha(cloudColor) > 0) {
                float cloudHeight = this.levelRenderState.cloudHeight;
                int cloudRange = this.optionsRenderState.cloudRange;
                long gameTime = this.levelRenderState.gameTime;
                DistanceFog.captureClouds(() -> this.cloudRenderer.render(cloudColor, cloudStatus, cloudHeight, cloudRange, cameraState.pos, gameTime, partialTick));
            }
        });
    }

    @Unique
    private void modulation$renderSky(CameraRenderState cameraState) {
        FogType fogType = cameraState.fogType;
        if (fogType == FogType.POWDER_SNOW || fogType == FogType.LAVA || cameraState.entityRenderState.doesMobEffectBlockSky) {
            return;
        }
        SkyRenderState state = this.levelRenderState.skyRenderState;
        SkyRenderer sky = this.skyRenderer;
        if (sky == null || state.skybox == DimensionType.Skybox.NONE) {
            return;
        }

        if (state.skybox == DimensionType.Skybox.END) {
            sky.renderEndSky();
            if (state.endFlashIntensity > 1.0E-5F) {
                sky.renderEndFlash(new PoseStack(), state.endFlashIntensity, state.endFlashXAngle, state.endFlashYAngle);
            }
        } else {
            PoseStack poseStack = new PoseStack();
            sky.renderSkyDisc(state.skyColor);
            sky.renderSunriseAndSunset(poseStack, state.sunAngle, state.sunriseAndSunsetColor);
            sky.renderSunMoonAndStars(poseStack, state.sunAngle, state.moonAngle, state.starAngle, state.moonPhase, state.rainBrightness, state.starBrightness);
        }
    }
    //?}
}
*///?}
