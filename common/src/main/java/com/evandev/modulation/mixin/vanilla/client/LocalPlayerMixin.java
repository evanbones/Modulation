package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.registry.ModRegistry;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @ModifyExpressionValue(
            method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")
    )
    private boolean preventChainStaffSlowdown(boolean original) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (original && player.getUseItem().is(ModRegistry.CHAIN_STAFF)) {
            return false;
        }
        return original;
    }
}