package com.evandev.modulation.mixin.blockgrid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SignBlock.class)
public class StandingSignModelShapeMixin {
    @Unique
    private static final int modulation$ROTATIONS = 16;
    @Unique
    private static final double modulation$PIXEL_CENTER = 8.0D;
    @Unique
    private static final double modulation$POST_HALF = 1.0D;
    @Unique
    private static final double modulation$POST_TOP = 7.0D;
    @Unique
    private static final double modulation$BOARD_HALF_WIDTH = 8.0D;
    @Unique
    private static final double modulation$BOARD_HALF_DEPTH = 1.0D;
    @Unique
    private static final double modulation$BOARD_TOP = 16.0D;

    @Unique
    private static final VoxelShape[] modulation$SHAPES = modulation$buildShapes();

    @Unique
    private static VoxelShape[] modulation$buildShapes() {
        VoxelShape[] shapes = new VoxelShape[modulation$ROTATIONS];
        for (int rotation = 0; rotation < modulation$ROTATIONS; rotation++) {
            double angle = Math.toRadians(rotation * (360.0D / modulation$ROTATIONS));
            double cos = Math.abs(Math.cos(angle));
            double sin = Math.abs(Math.sin(angle));
            shapes[rotation] = Shapes.or(
                    modulation$spun(modulation$POST_HALF, modulation$POST_HALF, cos, sin, 0.0D, modulation$POST_TOP),
                    modulation$spun(modulation$BOARD_HALF_WIDTH, modulation$BOARD_HALF_DEPTH, cos, sin, modulation$POST_TOP, modulation$BOARD_TOP));
        }
        return shapes;
    }

    @Unique
    private static VoxelShape modulation$spun(double halfWidth, double halfDepth, double cos, double sin, double minY, double maxY) {
        double spanX = halfWidth * cos + halfDepth * sin;
        double spanZ = halfWidth * sin + halfDepth * cos;
        return Block.box(
                modulation$PIXEL_CENTER - spanX, minY, modulation$PIXEL_CENTER - spanZ,
                modulation$PIXEL_CENTER + spanX, maxY, modulation$PIXEL_CENTER + spanZ);
    }

    @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
    private VoxelShape modulation$modelAccurateStandingShape(VoxelShape original, BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!(state.getBlock() instanceof StandingSignBlock) || !state.hasProperty(BlockStateProperties.ROTATION_16)) {
            return original;
        }
        return modulation$SHAPES[state.getValue(BlockStateProperties.ROTATION_16)];
    }
}
