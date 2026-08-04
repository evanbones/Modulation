package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.modules.blockgrid.ServerOffsetSync;
import com.evandev.modulation.modules.blockgrid.SupportOffsets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateOffsetMixin {
    @ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
    private boolean modulation$surviveOnPartialSurface(boolean original, LevelReader level, BlockPos pos) {
        BlockState self = modulation$self();
        if (SupportOffsets.isSitter(self)) {
            BlockState below = level.getBlockState(pos.below());
            if (SupportOffsets.isSitter(below) || SupportOffsets.isFacingMounted(below)) {
                return false;
            }
        }
        return original || SupportOffsets.hasPartialSupport(self, level, pos);
    }

    @ModifyReturnValue(method = "hasLargeCollisionShape", at = @At("RETURN"))
    private boolean modulation$extendCollisionLookup(boolean original) {
        return original || SupportOffsets.mayOffset(modulation$self());
    }

    @ModifyReturnValue(method = "isRedstoneConductor", at = @At("RETURN"))
    private boolean modulation$conductThroughOffsetNeighbor(boolean original, BlockGetter level, BlockPos pos) {
        return original || SupportOffsets.carriesAttachedSignal(modulation$self(), level, pos);
    }

    @ModifyReturnValue(method = "getOffset", at = @At("RETURN"))
    private Vec3 modulation$shiftRenderToSupport(Vec3 original, BlockGetter level, BlockPos pos) {
        Vec3 shift = SupportOffsets.offsetFor(modulation$self(), level, pos);
        return shift == Vec3.ZERO ? original : original.add(shift);
    }

    @ModifyReturnValue(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
    private VoxelShape modulation$shiftShapeToSupport(VoxelShape original, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 shift = SupportOffsets.offsetFor(modulation$self(), level, pos);
        return shift == Vec3.ZERO ? original : original.move(shift.x, shift.y, shift.z);
    }

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void modulation$dropStoredOffset(Level level, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel && !modulation$self().is(newState.getBlock())) {
            ServerOffsetSync.onBlockRemoved(serverLevel, pos);
        }
    }

    @Unique
    private BlockState modulation$self() {
        return (BlockState) (Object) this;
    }
}
