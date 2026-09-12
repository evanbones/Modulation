package com.evandev.modulation.mixin.emi.client;

import com.evandev.modulation.client.render.SlotHighlightRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.EmiScreenManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("emi")
@Mixin(value = EmiScreenManager.ScreenSpace.class, priority = 1100, remap = false)
public class EmiScreenSpaceMixin {

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Ldev/emi/emi/EmiRenderHelper;drawSlotHightlight(Ldev/emi/emi/runtime/EmiDrawContext;IIIII)V"))
    private void modulation$renderSlotHighlightBack(EmiDrawContext context, int x, int y, int w, int h, int z, Operation<Void> original) {
        if (!SlotHighlightRenderer.isEnabled()) {
            original.call(context, x, y, w, h, z);
            return;
        }
        int highlightX = x + (w - 16) / 2;
        int highlightY = y + (h - 16) / 2;
        SlotHighlightRenderer.renderBack(context.raw(), highlightX, highlightY, 50);
    }
}
