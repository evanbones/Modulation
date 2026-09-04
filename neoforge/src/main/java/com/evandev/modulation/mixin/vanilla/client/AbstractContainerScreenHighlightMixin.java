package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.client.render.SlotHighlightRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenHighlightMixin {

    @Shadow
    public abstract int getSlotColor(int index);

    @Inject(
            method = "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;IIF)V",
            at = @At("HEAD"),
            cancellable = true)
    private void modulation$renderSlotHighlightFront(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!SlotHighlightRenderer.isEnabled() || getSlotColor(slot.index) != -2130706433) return;
        if (slot.isHighlightable()) {
            SlotHighlightRenderer.renderFront(guiGraphics, slot.x, slot.y);
        }
        ci.cancel();
    }
}
