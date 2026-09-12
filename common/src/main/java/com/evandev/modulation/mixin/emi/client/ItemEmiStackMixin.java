package com.evandev.modulation.mixin.emi.client;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.ItemEmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("emi")
@Mixin(value = ItemEmiStack.class, remap = false)
public abstract class ItemEmiStackMixin {

    @Shadow
    public abstract ItemStack getItemStack();

    @Inject(method = "render", at = @At("TAIL"))
    private void modulation$renderDecorations(GuiGraphics draw, int x, int y, float delta, int flags, CallbackInfo ci) {
        if ((flags & EmiIngredient.RENDER_ICON) == 0 && (flags & EmiIngredient.RENDER_AMOUNT) == 0) {
            ItemStack stack = getItemStack();
            if (!stack.isEmpty()) {
                draw.renderItemDecorations(Minecraft.getInstance().font, stack, x, y, "");
            }
        }
    }
}
