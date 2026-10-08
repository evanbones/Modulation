package com.evandev.modulation.mixin.minecraft.bugfix.client;

import com.evandev.modulation.client.ImprovedFog;
import com.mojang.blaze3d.platform.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GlStateManager.class, remap = false)
public abstract class GlStateManagerCloudCaptureMixin {

    @ModifyVariable(method = "_glBindFramebuffer", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private static int modulation$redirectCloudCapture(int framebuffer, int target) {
        return ImprovedFog.redirectFramebuffer(target, framebuffer);
    }
}
