package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.client.render.SlotHighlightRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenHighlightMixin {

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V"))
    private void modulation$renderSlotHighlightFront(GuiGraphics guiGraphics, int x, int y, int blitOffset, Operation<Void> original) {
        if (SlotHighlightRenderer.isEnabled()) {
            SlotHighlightRenderer.renderFront(guiGraphics, x, y);
            return;
        }
        original.call(guiGraphics, x, y, blitOffset);
    }
}
