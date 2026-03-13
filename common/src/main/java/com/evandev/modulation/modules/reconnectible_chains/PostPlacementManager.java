package com.evandev.modulation.modules.reconnectible_chains;

import com.evandev.connectiblechains.CommonClass;
import com.evandev.connectiblechains.entity.ChainKnotEntity;
import com.evandev.connectiblechains.entity.Chainable;
import com.evandev.modulation.blocks.CastPostBlock;
import com.evandev.modulation.mixin.minecraft.accessor.BlockDisplayInvoker;
import com.evandev.modulation.mixin.minecraft.accessor.DisplayInvoker;
import com.evandev.modulation.registry.ModRegistry;
import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class PostPlacementManager {
    public static final PostPlacementManager INSTANCE = new PostPlacementManager();

    private final Map<UUID, BlockPos> firstPostMap = new HashMap<>();
    private final List<PendingPost> pendingPosts = new ArrayList<>();

    public void tick() {
        if (pendingPosts.isEmpty()) return;

        pendingPosts.removeIf(pending -> {
            pending.ticksLeft--;

            if (pending.ticksLeft == 8) {
                ((DisplayInvoker) pending.display).invokeSetInterpolationDuration(8);
                ((DisplayInvoker) pending.display).invokeSetInterpolationDelay(0);

                float targetSx = 1f, targetSy = 1f, targetSz = 1f;
                float targetTx = 0f, targetTy = 0f, targetTz = 0f;

                switch (pending.facing) {
                    case UP -> targetSy = 2f;
                    case DOWN -> {
                        targetSy = 2f;
                        targetTy = -1f;
                    }
                    case SOUTH -> targetSz = 2f;
                    case NORTH -> {
                        targetSz = 2f;
                        targetTz = -1f;
                    }
                    case EAST -> targetSx = 2f;
                    case WEST -> {
                        targetSx = 2f;
                        targetTx = -1f;
                    }
                }

                ((DisplayInvoker) pending.display).invokeSetTransformation(new Transformation(
                        new Vector3f(targetTx, targetTy, targetTz), new Quaternionf(), new Vector3f(targetSx, targetSy, targetSz), new Quaternionf()
                ));
            }

            if (pending.ticksLeft <= 0) {
                pending.display.discard();
                placeFinalBlock(pending);
                return true;
            }
            return false;
        });
    }

    public void handlePostPlacement(ServerPlayer player, BlockPos pos, Direction clickedFace) {
        ServerLevel level = player.serverLevel();

        BlockPos extensionPos = pos.relative(clickedFace);
        if (level.isOutsideBuildHeight(pos) || level.isOutsideBuildHeight(extensionPos) ||
                !level.getBlockState(pos).canBeReplaced() || !level.getBlockState(extensionPos).canBeReplaced()) {
            return;
        }

        boolean waterlogged = level.getFluidState(pos).getType() == Fluids.WATER;
        BlockState finalState = ModRegistry.CAST_POST.defaultBlockState()
                .setValue(CastPostBlock.FACING, clickedFace)
                .setValue(CastPostBlock.WATERLOGGED, waterlogged)
                .setValue(CastPostBlock.HALF, DoubleBlockHalf.LOWER);

        level.playSound(null, pos, SoundEvents.BAMBOO_WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.5f);
        level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.1, 0.1, 0.1, 0.05);

        Display.BlockDisplay display = EntityType.BLOCK_DISPLAY.create(level);
        if (display != null) {
            display.setPos(pos.getX(), pos.getY(), pos.getZ());
            ((BlockDisplayInvoker) display).invokeSetBlockState(finalState);

            float initialScale = 0.01f;
            float sx = 1f, sy = 1f, sz = 1f;
            float tx = 0f, ty = 0f, tz = 0f;

            switch (clickedFace) {
                case UP -> {
                    sy = initialScale;
                    ty = 0;
                }
                case DOWN -> {
                    sy = initialScale;
                    ty = 1 - initialScale;
                }
                case SOUTH -> {
                    sz = initialScale;
                    tz = 0;
                }
                case NORTH -> {
                    sz = initialScale;
                    tz = 1 - initialScale;
                }
                case EAST -> {
                    sx = initialScale;
                    tx = 0;
                }
                case WEST -> {
                    sx = initialScale;
                    tx = 1 - initialScale;
                }
            }

            ((DisplayInvoker) display).invokeSetTransformation(new Transformation(
                    new Vector3f(tx, ty, tz), new Quaternionf(), new Vector3f(sx, sy, sz), new Quaternionf()
            ));

            level.addFreshEntity(display);
            pendingPosts.add(new PendingPost(pos, clickedFace, level, display, player, 9));
        }
    }

    private void placeFinalBlock(PendingPost pending) {
        ServerLevel level = pending.level;
        BlockPos pos = pending.pos;
        ServerPlayer player = pending.player;

        boolean waterlogged = level.getFluidState(pos).getType() == Fluids.WATER;
        BlockState finalStateLower = ModRegistry.CAST_POST.defaultBlockState()
                .setValue(CastPostBlock.FACING, pending.facing)
                .setValue(CastPostBlock.WATERLOGGED, waterlogged)
                .setValue(CastPostBlock.HALF, DoubleBlockHalf.LOWER);
        level.setBlock(pos, finalStateLower, 3);

        BlockPos upperPos = pos.relative(pending.facing);
        boolean upperWaterlogged = level.getFluidState(upperPos).getType() == Fluids.WATER;
        BlockState finalStateUpper = finalStateLower
                .setValue(CastPostBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(CastPostBlock.WATERLOGGED, upperWaterlogged);
        level.setBlock(upperPos, finalStateUpper, 3);

        BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(pos.relative(pending.facing.getOpposite())));
        for (int i = 0; i < 15; i++) {
            double offsetX = level.random.nextGaussian() * 0.15;
            double offsetZ = level.random.nextGaussian() * 0.15;
            level.sendParticles(particleOption, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1, offsetX, 0.2, offsetZ, 0.1);
        }

        if (!firstPostMap.containsKey(player.getUUID())) {
            firstPostMap.put(player.getUUID(), upperPos);
        } else {
            BlockPos firstPos = firstPostMap.remove(player.getUUID());
            double dist = Math.sqrt(upperPos.distSqr(firstPos));

            if (dist <= CommonClass.runtimeConfig.getMaxChainRange()) {

                BlockState state1 = level.getBlockState(firstPos);
                Direction dir1 = Direction.UP;
                if (state1.hasProperty(CastPostBlock.FACING)) {
                    dir1 = state1.getValue(CastPostBlock.FACING);
                }

                Direction dir2 = pending.facing;

                ChainKnotEntity knot1 = ChainKnotEntity.getOrCreate(level, firstPos, Items.CHAIN, dir1);
                ChainKnotEntity knot2 = ChainKnotEntity.getOrCreate(level, upperPos, Items.CHAIN, dir2);

                if (!knot1.equals(knot2)) {
                    knot1.attachChain(new Chainable.ChainData(knot2, Items.CHAIN), null, true);
                }
            }
        }
    }

    private static class PendingPost {
        final BlockPos pos;
        final Direction facing;
        final ServerLevel level;
        final Display.BlockDisplay display;
        final ServerPlayer player;
        int ticksLeft;

        PendingPost(BlockPos pos, Direction facing, ServerLevel level, Display.BlockDisplay display, ServerPlayer player, int ticksLeft) {
            this.pos = pos;
            this.facing = facing;
            this.level = level;
            this.display = display;
            this.player = player;
            this.ticksLeft = ticksLeft;
        }
    }
}