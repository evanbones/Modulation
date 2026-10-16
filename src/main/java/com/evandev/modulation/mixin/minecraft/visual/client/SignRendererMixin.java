package com.evandev.modulation.mixin.minecraft.visual.client;

import com.evandev.modulation.modules.vanilla.VanillaVisualModule;
//? if >=26.1 {
/*import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.util.ARGB;
*///?} else {
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.util.FastColor;
//?}
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=26.1 {
/*@Mixin(AbstractSignRenderer.class)
*///?} else {
@Mixin(SignRenderer.class)
//?}
public class SignRendererMixin {

    @Inject(method = "getDarkColor", at = @At("HEAD"), cancellable = true)
    private static void modulation$modifySignTextColor(SignText sign, CallbackInfoReturnable<Integer> cir) {
        if (VanillaVisualModule.LEGIBLE_SIGNS.on() && !sign.hasGlowingText()) {
            DyeColor dc = sign.getColor();
            int res;
            switch (dc) {
                case BLACK -> res = 0x000000;
                case GRAY -> res = 0x333333;
                case BROWN -> res = dc.getTextColor();
                default -> res = dc.getTextureDiffuseColor();
            }
            //? if >=26.1 {
            /*cir.setReturnValue(ARGB.color(255, (res >> 16) & 0xFF, (res >> 8) & 0xFF, res & 0xFF));
            *///?} else {
            cir.setReturnValue(FastColor.ARGB32.color(0, (res >> 16) & 0xFF, (res >> 8) & 0xFF, res & 0xFF));
            //?}
        }
    }
}
