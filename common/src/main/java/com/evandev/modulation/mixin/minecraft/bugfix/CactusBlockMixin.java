package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("debugify")
@Mixin(CactusBlock.class)
public class CactusBlockMixin {

    @WrapOperation(
            method = "canSurvive",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 0)
    )
    private BlockState modulation$cactusSeesMovedBlocks(LevelReader level, BlockPos pos, Operation<BlockState> original) {
        BlockState state = original.call(level, pos);
        if (state.is(Blocks.MOVING_PISTON) && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixPistonMovedCactusEnabled)) {
            if (level.getBlockEntity(pos) instanceof PistonMovingBlockEntity piston) {
                return piston.getMovedState();
            }
        }
        return state;
    }
}
