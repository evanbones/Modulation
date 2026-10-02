package com.evandev.modulation.mixin.jei.client;

import com.evandev.modulation.client.render.SlotHighlightRenderer;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.overlay.ingredients.IngredientGrid;
import mezz.jei.gui.overlay.ingredients.IngredientListSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@IfModLoaded(value = "jei", minVersion = "19.34.0")
@Mixin(value = IngredientGrid.class, remap = false)
public class IngredientGridMixin {

    @Inject(
            method = "drawContents",
            at = @At(value = "INVOKE", target = "Lmezz/jei/gui/overlay/ingredients/IngredientListRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;)V", remap = true))
    private void modulation$renderSlotHighlightBack(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci,
                                                    @Local(name = "highlightedSlot") Optional<IngredientListSlot> highlightedSlot) {
        if (!SlotHighlightRenderer.isEnabled() || highlightedSlot.isEmpty()) return;
        ImmutableRect2i area = highlightedSlot.get().getArea();
        SlotHighlightRenderer.renderBack(guiGraphics, area.getX() + (area.getWidth() - 16) / 2, area.getY() + (area.getHeight() - 16) / 2);
    }

    @Inject(method = "drawHighlight", at = @At("HEAD"), cancellable = true)
    private static void modulation$cancelSlotHighlightFront(GuiGraphics guiGraphics, ImmutableRect2i area, CallbackInfo ci) {
        if (!SlotHighlightRenderer.isEnabled()) return;
        ci.cancel();
    }
}
