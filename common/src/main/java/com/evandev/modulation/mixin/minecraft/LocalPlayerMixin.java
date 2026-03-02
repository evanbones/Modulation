package com.evandev.modulation.mixin.minecraft;

import com.evandev.modulation.registry.ModRegistry;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean preventChainStaffSlowdown(LocalPlayer player) {
        if (player.isUsingItem() && player.getUseItem().is(ModRegistry.CHAIN_STAFF)) {
            return false;
        }
        return player.isUsingItem();
    }
}