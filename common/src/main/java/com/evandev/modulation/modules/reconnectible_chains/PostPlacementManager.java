package com.evandev.modulation.modules.reconnectible_chains;

import com.evandev.connectiblechains.CommonClass;
import com.evandev.connectiblechains.entity.ChainKnotEntity;
import com.evandev.connectiblechains.entity.Chainable;
import com.evandev.modulation.blocks.CastPostBlock;
import com.evandev.modulation.mixin.minecraft.BlockDisplayInvoker;
import com.evandev.modulation.mixin.minecraft.DisplayInvoker;
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

            if (pending.ticksLeft == 10) {
                ((DisplayInvoker) pending.display).invokeSetInterpolationDuration(10);
                ((DisplayInvoker) pending.display).invokeSetInterpolationDelay(0);
                ((DisplayInvoker) pending.display).invokeSetTransformation(new Transformation(
                        new Vector3f(0f, 0f, 0f), new Quaternionf(), new Vector3f(1f, 1f, 1f), new Quaternionf()
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
        BlockState finalState = ModRegistry.CAST_POST.defaultBlockState().setValue(CastPostBlock.FACING, clickedFace);

        level.playSound(null, pos, SoundEvents.BAMBOO_WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.5f);

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
            pendingPosts.add(new PendingPost(pos, clickedFace, level, display, player, 11));
        }
    }

    private void placeFinalBlock(PendingPost pending) {
        ServerLevel level = pending.level;
        BlockPos pos = pending.pos;
        ServerPlayer player = pending.player;

        BlockState finalState = ModRegistry.CAST_POST.defaultBlockState().setValue(CastPostBlock.FACING, pending.facing);
        level.setBlock(pos, finalState, 3);

        BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(pos.relative(pending.facing.getOpposite())));
        for (int i = 0; i < 15; i++) {
            double offsetX = level.random.nextGaussian() * 0.15;
            double offsetZ = level.random.nextGaussian() * 0.15;
            level.sendParticles(particleOption, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1, offsetX, 0.2, offsetZ, 0.1);
        }
        level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.1, 0.1, 0.1, 0.05);

        if (!firstPostMap.containsKey(player.getUUID())) {
            firstPostMap.put(player.getUUID(), pos);
        } else {
            BlockPos firstPos = firstPostMap.remove(player.getUUID());
            double dist = Math.sqrt(pos.distSqr(firstPos));

            if (dist <= CommonClass.runtimeConfig.getMaxChainRange()) {
                ChainKnotEntity knot1 = ChainKnotEntity.getOrCreate(level, firstPos, Items.CHAIN);
                ChainKnotEntity knot2 = ChainKnotEntity.getOrCreate(level, pos, Items.CHAIN);

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