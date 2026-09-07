package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("pathunderfencegates")
@Mixin(WallTorchBlock.class)
public class WallTorchBlockMixin {

    @ModifyReturnValue(
            method = "canSurvive(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            at = @At("RETURN")
    )
    private static boolean modulation$wallTorchesOnDirtPath(boolean original, @Local(argsOnly = true) LevelReader level, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) Direction facing) {
        if (original || !VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixPathUnderBlocksEnabled)) {
            return original;
        }
        return level.getBlockState(pos.relative(facing.getOpposite())).is(Blocks.DIRT_PATH);
    }
}
