package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public class HoeItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void modulation$disableFarmlandCreation(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (VanillaGameplayModule.DISABLE_FARMLAND_CREATION.on()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
