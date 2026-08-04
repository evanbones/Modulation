package com.evandev.modulation.modules.blockgrid;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.blockgrid.storage.ChunkOffsetHolder;
import com.evandev.modulation.modules.blockgrid.storage.SignOffsetHolder;
import com.evandev.modulation.registry.ModTags;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Set;
import java.util.function.Function;

public final class SupportOffsets {
    private static final AABB UNIT_CUBE = new AABB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final double CENTER = 0.5D;

    private static Function<BlockPos, Vec3> renderLookup;
    private static Set<Block> sitters = new ReferenceOpenHashSet<>();
    private static Set<Block> facingMounted = new ReferenceOpenHashSet<>();

    private SupportOffsets() {
    }

    public static void onTagsUpdated() {
        Set<Block> tagged = new ReferenceOpenHashSet<>();
        Set<Block> mounted = new ReferenceOpenHashSet<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            BlockState state = block.defaultBlockState();
            if (state.is(ModTags.SITS_ON_SLABS)) {
                tagged.add(block);
            }
            if (state.is(ModTags.MOUNTS_ON_FACING)) {
                mounted.add(block);
            }
        }
        sitters = tagged;
        facingMounted = mounted;
    }

    public static void setRenderLookup(Function<BlockPos, Vec3> lookup) {
        renderLookup = lookup;
    }

    public static boolean isSitter(BlockState state) {
        return state.is(ModTags.SITS_ON_SLABS) || cached(sitters, state);
    }

    public static boolean isFacingMounted(BlockState state) {
        return state.is(ModTags.MOUNTS_ON_FACING) || cached(facingMounted, state);
    }

    public static boolean mayOffset(BlockState state) {
        BlockGridModule module = module();
        if (module == null || !module.isEnableBlockOffsets()) {
            return false;
        }
        if (state.getBlock() instanceof StandingSignBlock) {
            return module.isEnableSignOffsets();
        }
        return module.isEnableSlabOffsets() && isSitter(state);
    }

    public static boolean supportsFromClickedFace(BlockState state, Direction clickedFace) {
        Direction anchor = anchorOf(state);
        return anchor != null && anchor == clickedFace.getOpposite();
    }

    public static Vec3 offsetFor(BlockState state, BlockGetter level, BlockPos pos) {
        if (level == null || pos == null || level == EmptyBlockGetter.INSTANCE || !mayOffset(state)) {
            return Vec3.ZERO;
        }
        if (state.getBlock() instanceof StandingSignBlock) {
            return level.getBlockEntity(pos) instanceof SignOffsetHolder sign
                    ? sign.modulation$getSignOffset()
                    : Vec3.ZERO;
        }

        Vec3 stored = storedOffset(level, pos);
        if (stored != null) {
            return stored;
        }

        Direction anchor = anchorOf(state);
        if (anchor == null) {
            return Vec3.ZERO;
        }
        AABB box = supportBox(level, pos, anchor);
        if (box == null) {
            return Vec3.ZERO;
        }
        box = box.intersect(UNIT_CUBE);

        Direction.Axis axis = anchor.getAxis();
        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;
        for (Direction.Axis component : Direction.Axis.values()) {
            double value = component == axis
                    ? anchoredComponent(box, anchor)
                    : (box.min(component) + box.max(component)) * CENTER - CENTER;
            switch (component) {
                case X -> x = value;
                case Y -> y = value;
                case Z -> z = value;
            }
        }
        return x == 0.0D && y == 0.0D && z == 0.0D ? Vec3.ZERO : new Vec3(x, y, z);
    }

    public static boolean carriesAttachedSignal(BlockState state, BlockGetter level, BlockPos pos) {
        BlockGridModule module = module();
        if (module == null || !module.isEnableBlockOffsets() || !module.isEnableRedstoneConduction()) {
            return false;
        }
        if (level == null || pos == null || state.isAir()) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            if (crossesUnloadedChunk(level, pos, neighborPos, direction)) {
                continue;
            }
            BlockState neighbor = level.getBlockState(neighborPos);
            if (!neighbor.isSignalSource() || !isSitter(neighbor)) {
                continue;
            }
            if (anchorDirection(neighbor) == direction.getOpposite()
                    && offsetFor(neighbor, level, neighborPos) != Vec3.ZERO) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPartialSupport(BlockState state, BlockGetter level, BlockPos pos) {
        BlockGridModule module = module();
        if (module == null || !module.isEnableBlockOffsets() || !module.isEnablePartialSupportSurvival()) {
            return false;
        }
        if (level == null || pos == null) {
            return false;
        }
        Direction anchor = anchorOf(state);
        if (anchor == null || supportBox(level, pos, anchor) == null) {
            return false;
        }

        Vec3 offset = offsetFor(state, level, pos);
        if (offset == Vec3.ZERO) {
            return true;
        }
        VoxelShape shifted = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty())
                .move(offset.x, offset.y, offset.z);
        if (shifted.isEmpty()) {
            return true;
        }
        BlockPos anchorPos = pos.relative(anchor);
        VoxelShape supportShape = level.getBlockState(anchorPos)
                .getBlockSupportShape(level, anchorPos)
                .move(anchor.getStepX(), anchor.getStepY(), anchor.getStepZ());
        return !Shapes.joinIsNotEmpty(shifted, supportShape, BooleanOp.AND);
    }

    private static BlockGridModule module() {
        return ModuleManager.getModule("block_grid", BlockGridModule.class);
    }

    private static boolean cached(Set<Block> cache, BlockState state) {
        return !cache.isEmpty() && cache.contains(state.getBlock());
    }

    private static double anchoredComponent(AABB box, Direction anchor) {
        Direction.Axis axis = anchor.getAxis();
        return anchor.getAxisDirection().getStep() < 0
                ? Math.min(box.max(axis), 1.0D) - 1.0D
                : Math.max(box.min(axis), 0.0D);
    }

    private static Vec3 storedOffset(BlockGetter level, BlockPos pos) {
        if (level instanceof Level real) {
            if (!real.hasChunkAt(pos)) {
                return null;
            }
            ChunkAccess chunk = real.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
            if (chunk instanceof ChunkOffsetHolder holder) {
                ChunkOffsetMap offsets = holder.modulation$getBlockOffsets();
                return offsets != null ? offsets.get(pos) : null;
            }
            return null;
        }
        if (level instanceof LevelReader) {
            return null;
        }
        return renderLookup == null ? null : renderLookup.apply(pos);
    }

    private static Direction anchorOf(BlockState state) {
        return isSitter(state) ? anchorDirection(state) : null;
    }

    private static AABB supportBox(BlockGetter level, BlockPos pos, Direction anchor) {
        BlockPos anchorPos = pos.relative(anchor);
        if (level instanceof LevelReader reader && !reader.hasChunkAt(anchorPos)) {
            return null;
        }
        BlockState supporting = level.getBlockState(anchorPos);
        if (isSitter(supporting) || isFacingMounted(supporting)
                || supporting.getBlock() instanceof StandingSignBlock
                || supporting.getBlock() instanceof WallSignBlock) {
            return null;
        }
        VoxelShape shape = supporting.getBlockSupportShape(level, anchorPos);
        if (shape.isEmpty()) {
            return null;
        }

        Direction.Axis axis = anchor.getAxis();
        boolean towardNegative = anchor.getAxisDirection().getStep() < 0;
        AABB best = null;
        for (AABB box : shape.toAabbs()) {
            if (!coversCenter(box, axis)) {
                continue;
            }
            if (best == null || isNearerSitter(box, best, axis, towardNegative)) {
                best = box;
            }
        }
        return best;
    }

    private static boolean coversCenter(AABB box, Direction.Axis axis) {
        for (Direction.Axis lateral : Direction.Axis.values()) {
            if (lateral != axis && (box.min(lateral) > CENTER || box.max(lateral) < CENTER)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNearerSitter(AABB box, AABB best, Direction.Axis axis, boolean towardNegative) {
        return towardNegative ? box.max(axis) > best.max(axis) : box.min(axis) < best.min(axis);
    }

    private static boolean crossesUnloadedChunk(BlockGetter level, BlockPos pos, BlockPos neighborPos, Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y || !(level instanceof LevelReader reader)) {
            return false;
        }
        boolean sameChunk = neighborPos.getX() >> 4 == pos.getX() >> 4 && neighborPos.getZ() >> 4 == pos.getZ() >> 4;
        return !sameChunk && !reader.hasChunkAt(neighborPos);
    }

    private static Direction anchorDirection(BlockState state) {
        boolean wallMounted = state.getBlock() instanceof WallSignBlock
                || state.getBlock() instanceof WallTorchBlock
                || isFacingMounted(state);
        if (wallMounted && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        }
        if (state.hasProperty(BlockStateProperties.ATTACH_FACE)) {
            AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);
            return switch (face) {
                case FLOOR -> Direction.DOWN;
                case CEILING -> Direction.UP;
                case WALL -> state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                        ? state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite()
                        : null;
            };
        }
        if (state.hasProperty(BlockStateProperties.HANGING) && state.getValue(BlockStateProperties.HANGING)) {
            return Direction.UP;
        }
        if (state.hasProperty(BlockStateProperties.ATTACHED) && state.getValue(BlockStateProperties.ATTACHED)) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }
}
