package com.evandev.modulation.mixin.minecraft.fog.client;

//? if >=26.1 && <26.3 {
/*import com.evandev.modulation.client.DistanceFog;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CloudRenderer;
//? if >=26.2
/^import net.minecraft.client.renderer.GameRenderer;^/
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfMinecraftVersion(minVersion = "26.1", maxVersion = "26.3", maxInclusive = false)
@Mixin(CloudRenderer.class)
public abstract class CloudRendererMixin {

    //? if >=26.2 {
    /^@WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;mainRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;")
    )
    private RenderTarget modulation$redirectCapture(GameRenderer instance, Operation<RenderTarget> original) {
        return DistanceFog.redirect(original.call(instance));
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;cloudsTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;")
    )
    private RenderTarget modulation$skipCloudsTargetWhileCapturing(LevelRenderer instance, Operation<RenderTarget> original) {
        return DistanceFog.isCapturing() ? null : original.call(instance);
    }
    ^///?} else {
    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getMainRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;")
    )
    private RenderTarget modulation$redirectCapture(Minecraft instance, Operation<RenderTarget> original) {
        return DistanceFog.redirect(original.call(instance));
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;getCloudsTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;")
    )
    private RenderTarget modulation$skipCloudsTargetWhileCapturing(LevelRenderer instance, Operation<RenderTarget> original) {
        return DistanceFog.isCapturing() ? null : original.call(instance);
    }
    //?}
}
*///?} else {
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(minVersion = "26.1", maxVersion = "26.3", maxInclusive = false)
@Mixin(targets = "net.minecraft.client.renderer.CloudRenderer")
public class CloudRendererMixin {
}
//?}
