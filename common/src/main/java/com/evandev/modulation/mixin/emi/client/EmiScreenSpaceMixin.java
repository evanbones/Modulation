package com.evandev.modulation.mixin.emi.client;

import com.evandev.modulation.client.render.SlotHighlightRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("emi")
@Mixin(value = EmiScreenManager.ScreenSpace.class, remap = false)
public class EmiScreenSpaceMixin {

    @Unique
    private boolean modulation$highlightPending;
    @Unique
    private int modulation$highlightX;
    @Unique
    private int modulation$highlightY;

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Ldev/emi/emi/EmiRenderHelper;drawSlotHightlight(Ldev/emi/emi/runtime/EmiDrawContext;IIIII)V"))
    private void modulation$renderSlotHighlightBack(EmiDrawContext context, int x, int y, int w, int h, int z, Operation<Void> original) {
        if (!SlotHighlightRenderer.isEnabled()) {
            original.call(context, x, y, w, h, z);
            return;
        }
        modulation$highlightPending = true;
        modulation$highlightX = x + (w - 16) / 2;
        modulation$highlightY = y + (h - 16) / 2;
        SlotHighlightRenderer.renderBack(context.raw(), modulation$highlightX, modulation$highlightY);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void modulation$renderSlotHighlightFront(EmiDrawContext context, int mouseX, int mouseY, float delta, int startIndex, CallbackInfo ci) {
        if (!modulation$highlightPending) return;
        modulation$highlightPending = false;
        SlotHighlightRenderer.renderFront(context.raw(), modulation$highlightX, modulation$highlightY);
    }
}
