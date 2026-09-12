package com.evandev.modulation.mixin.emi.client;

import com.evandev.modulation.client.render.SlotHighlightRenderer;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("emi")
@Mixin(value = SlotWidget.class, remap = false)
public abstract class SlotWidgetMixin {

    @Unique
    private static int modulation$stackX(Bounds bounds) {
        return bounds.x() + (bounds.width() - 16) / 2;
    }

    @Unique
    private static int modulation$stackY(Bounds bounds) {
        return bounds.y() + (bounds.height() - 16) / 2;
    }

    @Shadow
    public abstract Bounds getBounds();

    @Shadow
    public abstract boolean shouldDrawSlotHighlight(int mouseX, int mouseY);

    @Inject(method = "drawStack", at = @At("HEAD"))
    private void modulation$renderSlotHighlightBack(GuiGraphics draw, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!SlotHighlightRenderer.isEnabled() || !shouldDrawSlotHighlight(mouseX, mouseY)) return;
        Bounds bounds = getBounds();
        SlotHighlightRenderer.renderBack(draw, modulation$stackX(bounds), modulation$stackY(bounds));
    }

    @Inject(method = "drawSlotHighlight", at = @At("HEAD"), cancellable = true)
    private void modulation$cancelSlotHighlightFront(GuiGraphics draw, Bounds bounds, CallbackInfo ci) {
        if (!SlotHighlightRenderer.isEnabled()) return;
        ci.cancel();
    }
}
