package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaVisualModule;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private static void modulation$lessAnnoyingFireHead(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
        if (ModuleManager.isEnabled("vanilla_visual", VanillaVisualModule.class, VanillaVisualModule::isLessAnnoyingFireEnabled)) {
            if (minecraft.player != null && (minecraft.player.isInvulnerableTo(minecraft.player.level().damageSources().onFire()) || minecraft.player.hasEffect(MobEffects.FIRE_RESISTANCE))) {
                ci.cancel();
            } else {
                poseStack.pushPose();
                poseStack.translate(0.0F, -0.2F, 0.0F);
            }
        }
    }

    @Inject(method = "renderFire", at = @At("TAIL"))
    private static void modulation$lessAnnoyingFireTail(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
        if (ModuleManager.isEnabled("vanilla_visual", VanillaVisualModule.class, VanillaVisualModule::isLessAnnoyingFireEnabled)) {
            poseStack.popPose();
        }
    }
}
