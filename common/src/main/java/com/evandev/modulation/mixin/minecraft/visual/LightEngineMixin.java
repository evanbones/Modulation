package com.evandev.modulation.mixin.minecraft.visual;

import com.evandev.modulation.modules.vanilla.smoothlight.LightEngineLevelAccess;
import com.evandev.modulation.modules.vanilla.smoothlight.LightTransitions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightEngine.class)
public abstract class LightEngineMixin implements LightEngineLevelAccess {

    @Shadow
    @Final
    protected LightChunkGetter chunkSource;

    @Unique
    @Override
    public BlockGetter modulation$lightEngineLevel() {
        return this.chunkSource.getLevel();
    }

    @Unique
    private boolean modulation$transitionsBlockLight() {
        return (Object) this instanceof BlockLightEngine;
    }

    @Inject(method = "getOpacity", at = @At("RETURN"), cancellable = true)
    private void modulation$transitionOpacity(BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!this.modulation$transitionsBlockLight()) return;
        int actual = cir.getReturnValueI();
        int value = LightTransitions.opacity(this.chunkSource.getLevel(), pos.asLong(), actual);
        if (value != actual) cir.setReturnValue(value);
    }

    @Inject(method = "getOcclusionShape(Lnet/minecraft/world/level/block/state/BlockState;JLnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"), cancellable = true)
    private void modulation$transitionOcclusionShape(BlockState state, long pos, Direction direction, CallbackInfoReturnable<VoxelShape> cir) {
        if (!this.modulation$transitionsBlockLight()) return;
        if (LightTransitions.overridesOcclusionShape(this.chunkSource.getLevel(), pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }
}
