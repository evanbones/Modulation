package com.evandev.modulation.mixin.minecraft.gui.client;

import com.evandev.modulation.client.render.SlotHighlightRenderer;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("trinkets")
@Mixin(value = AbstractContainerScreen.class, priority = 1500)
public class AbstractContainerScreenTrinketsMixin {

    @Inject(
            method = "renderSlot",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void modulation$renderDeferredSlotHighlightBack(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        SlotHighlightRenderer.renderDeferredBack(guiGraphics, slot);
    }
}
