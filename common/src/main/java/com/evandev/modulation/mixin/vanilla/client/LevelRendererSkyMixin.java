package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.SkyExposure;
import com.evandev.modulation.mixin.vanilla.accessor.FogRendererAccessor;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererSkyMixin {

    @Shadow
    private ClientLevel level;

    @Unique
    private float modulation$skyFade;

    @Unique
    private float modulation$cloudFade;

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void modulation$skipCaveSky(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        this.modulation$skyFade = SkyExposure.skyHideFactor(this.level, camera, partialTick);
        if (this.modulation$skyFade >= 1.0F) {
            skyFogSetup.run();
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 modulation$fadeSkyColor(Vec3 original) {
        if (this.modulation$skyFade <= 0.0F) {
            return original;
        }

        double fade = this.modulation$skyFade;
        return new Vec3(
                Mth.lerp(fade, original.x, FogRendererAccessor.modulation$getFogRed()),
                Mth.lerp(fade, original.y, FogRendererAccessor.modulation$getFogGreen()),
                Mth.lerp(fade, original.z, FogRendererAccessor.modulation$getFogBlue())
        );
    }

    @ModifyExpressionValue(
            method = "renderSky",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F")
    )
    private float modulation$fadeCelestialBodies(float original) {
        if (this.modulation$skyFade <= 0.0F) {
            return original;
        }
        return 1.0F - (1.0F - original) * (1.0F - this.modulation$skyFade);
    }

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

        if (original == null || this.modulation$skyFade <= 0.0F) {
            return original;
        }

        float[] faded = original.clone();
        faded[3] *= 1.0F - this.modulation$skyFade;
        return faded;
    }

    @WrapOperation(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V"
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/renderer/LevelRenderer;darkBuffer:Lcom/mojang/blaze3d/vertex/VertexBuffer;",
                            ordinal = 0
                    )
            )
    )
    private void modulation$fadeVoidPlane(VertexBuffer instance, Matrix4f frustumMatrix, Matrix4f projectionMatrix, ShaderInstance shader, Operation<Void> original) {
        if (this.modulation$skyFade <= 0.0F) {
            original.call(instance, frustumMatrix, projectionMatrix, shader);
            return;
        }

        float fade = this.modulation$skyFade;
        RenderSystem.setShaderColor(
                FogRendererAccessor.modulation$getFogRed() * fade,
                FogRendererAccessor.modulation$getFogGreen() * fade,
                FogRendererAccessor.modulation$getFogBlue() * fade,
                1.0F
        );
        original.call(instance, frustumMatrix, projectionMatrix, shader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void modulation$skipCaveClouds(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        this.modulation$cloudFade = SkyExposure.skyHideFactor(Minecraft.getInstance().gameRenderer.getMainCamera(), partialTick);
        if (this.modulation$cloudFade >= 1.0F) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "renderClouds",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V"
            )
    )
    private void modulation$fadeClouds(VertexBuffer instance, Matrix4f frustumMatrix, Matrix4f projectionMatrix, ShaderInstance shader, Operation<Void> original) {
        if (this.modulation$cloudFade <= 0.0F) {
            original.call(instance, frustumMatrix, projectionMatrix, shader);
            return;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F - this.modulation$cloudFade);
        original.call(instance, frustumMatrix, projectionMatrix, shader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
