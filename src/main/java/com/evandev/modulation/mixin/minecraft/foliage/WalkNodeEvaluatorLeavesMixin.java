package com.evandev.modulation.mixin.minecraft.foliage;

import com.evandev.modulation.modules.vanilla.PassableFoliageModule;
import com.evandev.modulation.registry.ModTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WalkNodeEvaluator.class)
public class WalkNodeEvaluatorLeavesMixin {

    @ModifyReturnValue(method = "getPathTypeFromState", at = @At("RETURN"))
    private static PathType modulation$passableLeavesAreOpen(PathType original, @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
        if (original == PathType.LEAVES
                && PassableFoliageModule.ENABLE_PASSABLE_FOLIAGE.on()
                && !PassableFoliageModule.ENABLE_SCAFFOLDING_MODE.on()
                && level.getBlockState(pos).is(ModTags.PASSABLE_LEAVES)) {
            return PathType.OPEN;
        }
        return original;
    }
}
