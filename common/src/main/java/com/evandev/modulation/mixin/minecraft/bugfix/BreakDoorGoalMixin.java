package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BreakDoorGoal.class)
public class BreakDoorGoalMixin {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z")
    )
    private boolean modulation$captureDoorState(Level level, BlockPos pos, boolean isMoving, Operation<Boolean> original, @Share("doorState") LocalRef<BlockState> doorState) {
        doorState.set(level.getBlockState(pos));
        return original.call(level, pos, isMoving);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState modulation$brokenDoorParticles(Level level, BlockPos pos, Operation<BlockState> original, @Share("doorState") LocalRef<BlockState> doorState) {
        if (doorState.get() != null && ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixZombieDoorParticlesEnabled)) {
            return doorState.get();
        }
        return original.call(level, pos);
    }
}
