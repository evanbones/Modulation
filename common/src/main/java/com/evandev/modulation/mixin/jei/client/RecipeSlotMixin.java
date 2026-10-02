package com.evandev.modulation.mixin.jei.client;

import com.evandev.modulation.client.render.SlotHighlightRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded(value = "jei", minVersion = "19.34.0")
@Mixin(value = RecipeSlot.class, remap = false)
public class RecipeSlotMixin {

    @Shadow
    private ImmutableRect2i rect;

    @Inject(
            method = "draw(Lnet/minecraft/client/gui/GuiGraphics;Z)V",
            at = @At(value = "INVOKE", target = "Lmezz/jei/library/gui/ingredients/RecipeSlot;getDisplayedIngredient()Ljava/util/Optional;", remap = false),
            remap = true)
    private void modulation$renderSlotHighlightBack(GuiGraphics guiGraphics, boolean hovered, CallbackInfo ci) {
        if (!hovered || !SlotHighlightRenderer.isEnabled()) return;
        SlotHighlightRenderer.renderBack(guiGraphics, this.rect.getX() + (this.rect.getWidth() - 16) / 2, this.rect.getY() + (this.rect.getHeight() - 16) / 2);
    }

    @WrapOperation(
            method = "draw(Lnet/minecraft/client/gui/GuiGraphics;Z)V",
            at = @At(value = "INVOKE", target = "Lmezz/jei/library/gui/ingredients/RecipeSlot;drawHighlight(Lnet/minecraft/client/gui/GuiGraphics;I)V"),
            remap = true)
    private void modulation$cancelSlotHighlightFront(RecipeSlot instance, GuiGraphics guiGraphics, int color, Operation<Void> original) {
        if (SlotHighlightRenderer.isEnabled()) return;
        original.call(instance, guiGraphics, color);
    }
}
