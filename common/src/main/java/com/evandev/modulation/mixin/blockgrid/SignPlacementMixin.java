package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.modules.blockgrid.ServerOffsetSync;
import com.evandev.modulation.modules.blockgrid.SupportOffsets;
import com.evandev.modulation.modules.blockgrid.SurfaceSnapping;
import com.evandev.modulation.modules.blockgrid.storage.SignOffsetHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class SignPlacementMixin {
    @ModifyReturnValue(method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;", at = @At("RETURN"))
    private InteractionResult modulation$alignPlacementToSurface(InteractionResult original, BlockPlaceContext context) {
        if (!original.consumesAction()) {
            return original;
        }
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState placed = level.getBlockState(pos);

        boolean standingSign = placed.getBlock() instanceof StandingSignBlock;
        if (!standingSign && (!SupportOffsets.mayOffset(placed)
                || !SupportOffsets.supportsFromClickedFace(placed, context.getClickedFace()))) {
            return original;
        }

        BlockPos supportPos = pos.relative(context.getClickedFace().getOpposite());
        Vector3f offset = SurfaceSnapping.placementOffsetFor(level, supportPos, context.getClickedFace(), context.getClickLocation());
        if (offset.x == 0.0F && offset.y == 0.0F && offset.z == 0.0F) {
            return original;
        }

        Vec3 shift = new Vec3(offset.x, offset.y, offset.z);
        if (level.getBlockEntity(pos) instanceof SignOffsetHolder sign) {
            sign.modulation$setSignOffset(shift);
        } else if (level instanceof ServerLevel serverLevel) {
            ServerOffsetSync.store(serverLevel, pos, shift);
        }
        return original;
    }
}
