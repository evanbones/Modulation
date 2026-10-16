package com.evandev.modulation.mixin.minecraft.visual;

import com.evandev.modulation.modules.vanilla.smoothlight.LightEngineLevelAccess;
import com.evandev.modulation.modules.vanilla.smoothlight.LightTransitions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=26.1 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
*///?}

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin implements LightEngineLevelAccess {

    @Inject(method = "getEmission", at = @At("RETURN"), cancellable = true)
    private void modulation$transitionEmission(long pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        int actual = cir.getReturnValueI();
        int value = LightTransitions.emission(this.modulation$lightEngineLevel(), pos, actual);
        if (value != actual) cir.setReturnValue(value);
    }

    //? if >=26.1 {
    /*@WrapOperation(
            method = "propagateIncrease",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/BlockLightEngine;getOpacity(Lnet/minecraft/world/level/block/state/BlockState;)I")
    )
    private int modulation$transitionOpacity(BlockLightEngine instance, BlockState state, Operation<Integer> original, @Local(ordinal = 2) long toNode) {
        int actual = original.call(instance, state);
        return LightTransitions.opacity(this.modulation$lightEngineLevel(), toNode, actual);
    }

    @WrapOperation(
            method = "propagateIncrease",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/BlockLightEngine;shapeOccludes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z")
    )
    private boolean modulation$transitionOcclusion(BlockLightEngine instance, BlockState fromState, BlockState toState, Direction direction, Operation<Boolean> original,
                                                   @Local(argsOnly = true, ordinal = 0) long fromNode, @Local(ordinal = 2) long toNode) {
        boolean fromOverridden = LightTransitions.overridesOcclusionShape(this.modulation$lightEngineLevel(), fromNode);
        boolean toOverridden = LightTransitions.overridesOcclusionShape(this.modulation$lightEngineLevel(), toNode);
        if (!fromOverridden && !toOverridden) {
            return original.call(instance, fromState, toState, direction);
        }
        VoxelShape fromShape = fromOverridden ? Shapes.empty() : LightEngine.getOcclusionShape(fromState, direction);
        VoxelShape toShape = toOverridden ? Shapes.empty() : LightEngine.getOcclusionShape(toState, direction.getOpposite());
        return Shapes.faceShapeOccludes(fromShape, toShape);
    }
    *///?}
}
