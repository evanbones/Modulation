package com.evandev.modulation.mixin.minecraft.fog.client;

//? if >=26.1 {
/*import com.evandev.modulation.client.DistanceFog;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderTarget;
//? if >=26.3 {
/^import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
^///?}
//? if >=26.2 {
/^import org.objectweb.asm.Opcodes;
^///?} else {
import net.minecraft.client.Minecraft;
//?}
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {

    //? if >=26.2 {
    /^@WrapOperation(
            method = "*",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderTarget:Lcom/mojang/blaze3d/pipeline/RenderTarget;", opcode = Opcodes.GETFIELD)
    )
    private RenderTarget modulation$redirectCapture(SkyRenderer instance, Operation<RenderTarget> original) {
        return DistanceFog.redirect(original.call(instance));
    }
    ^///?} else {
    @WrapOperation(
            method = "*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getMainRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;")
    )
    private RenderTarget modulation$redirectCapture(Minecraft instance, Operation<RenderTarget> original) {
        return DistanceFog.redirect(original.call(instance));
    }
    //?}

    @Inject(method = "renderDarkDisc", at = @At("HEAD"), cancellable = true)
    private void modulation$replaceDarkDisc(CallbackInfo ci) {
        if (DistanceFog.enabled()) {
            ci.cancel();
        }
    }

    //? if >=26.3 {
    /^@Inject(method = "renderSunMoonAndStars", at = @At("TAIL"))
    private void modulation$renderSkyOccluder(CallbackInfo ci, @Local(argsOnly = true) RenderPass renderPass) {
        DistanceFog.renderSkyOccluder(renderPass);
    }
    ^///?} else {
    @Inject(method = "renderSunMoonAndStars", at = @At("TAIL"))
    private void modulation$renderSkyOccluder(CallbackInfo ci) {
        DistanceFog.renderSkyOccluder();
    }
    //?}
}
*///?}
