package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.modules.blockgrid.SupportOffsets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockGetter.class)
public interface OffsetRaycastMixin {
    @ModifyReturnValue(method = "clipWithInteractionOverride", at = @At("RETURN"))
    private BlockHitResult modulation$clipShiftedNeighbors(BlockHitResult original, Vec3 startVec, Vec3 endVec, BlockPos pos, VoxelShape shape, BlockState state) {
        if (original != null) {
            return original;
        }
        BlockGetter level = (BlockGetter) this;
        BlockHitResult closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighbor = level.getBlockState(neighborPos);
            if (!SupportOffsets.mayOffset(neighbor)
                    || SupportOffsets.offsetFor(neighbor, level, neighborPos) == Vec3.ZERO) {
                continue;
            }
            BlockHitResult hit = neighbor.getShape(level, neighborPos, CollisionContext.empty())
                    .clip(startVec, endVec, neighborPos);
            if (hit == null) {
                continue;
            }
            double distance = hit.getLocation().distanceToSqr(startVec);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = hit;
            }
        }
        return closest;
    }
}
