package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.registry.ModTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {

    @ModifyReturnValue(method = "isRespawnFuel", at = @At("RETURN"))
    private static boolean modulation$useFuelTag(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return stack.is(ModTags.RESPAWN_ANCHOR_FUEL);
    }
}
