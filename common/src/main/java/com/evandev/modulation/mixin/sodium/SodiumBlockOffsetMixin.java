package com.evandev.modulation.mixin.sodium;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.evandev.modulation.modules.blockgrid.SlabOffsets;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BlockRenderer.class, remap = false)
public class SodiumBlockOffsetMixin {
    @ModifyExpressionValue(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasOffsetFunction()Z"))
    private boolean modulation$offsetOurBlocksToo(boolean original, @Local(argsOnly = true) BlockState state) {
        return original || SlabOffsets.mayOffset(state);
    }
}
