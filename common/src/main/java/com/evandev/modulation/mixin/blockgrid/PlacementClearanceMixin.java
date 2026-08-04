package com.evandev.modulation.mixin.blockgrid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.evandev.modulation.modules.blockgrid.SurfaceSnapping;
import com.evandev.modulation.modules.blockgrid.SupportOffsets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class PlacementClearanceMixin {
    @ModifyReturnValue(method = "canPlace", at = @At("RETURN"))
    private boolean modulation$blockOverlappingPlacement(boolean original, BlockPlaceContext context, BlockState state) {
        if (!original || !SupportOffsets.mayOffset(state) || !SupportOffsets.supportsFromClickedFace(state, context.getClickedFace())) {
            return original;
        }
        BlockPos pos = context.getClickedPos();
        BlockPos support = pos.relative(context.getClickedFace().getOpposite());
        Vector3f offset = SurfaceSnapping.placementOffsetFor(context.getLevel(), support, context.getClickedFace(), context.getClickLocation());
        return SurfaceSnapping.fitsWithOffset(context.getLevel(), pos, state, offset);
    }
}
