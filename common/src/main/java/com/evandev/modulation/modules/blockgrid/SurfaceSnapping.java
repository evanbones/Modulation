package com.evandev.modulation.modules.blockgrid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public final class SurfaceSnapping {
    private static final double TOLERANCE = 1.0E-4D;
    private static final double CENTER = 0.5D;
    private static final double PLACEMENT_STEPS = 16.0D;
    private static final double MOUNT_STEPS = 2.0D;

    private SurfaceSnapping() {
    }

    public static Vector3f placementOffsetFor(UseOnContext context) {
        return placementOffsetFor(context.getLevel(), context.getClickedPos(), context.getClickedFace(), context.getClickLocation());
    }

    public static Vector3f placementOffsetFor(Level level, BlockPos pos, Direction face, Vec3 clickLocation) {
        BlockState state = level.getBlockState(pos);
        if (managesOwnOffset(state)) {
            return new Vector3f();
        }

        Vec3 hit = localHit(pos, clickLocation);
        VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());
        AABB box = surfaceBoxAt(shape, face, hit);
        if (box == null) {
            return new Vector3f();
        }
        box = trimOccludedMargins(shape, box, face);

        double sink = sinkDepth(box, face);
        Vector3f offset = new Vector3f();
        for (Direction.Axis axis : Direction.Axis.values()) {
            double value = axis == face.getAxis() ? sink : centerOf(box, axis) - CENTER;
            assign(offset, axis, quantize(value, PLACEMENT_STEPS));
        }
        return offset;
    }

    public static Vector3f mountOffsetFor(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (managesOwnOffset(state)) {
            return new Vector3f();
        }

        Direction face = context.getClickedFace();
        Vec3 hit = localHit(pos, context.getClickLocation());
        AABB box = surfaceBoxAt(state.getShape(level, pos, CollisionContext.empty()), face, hit);

        Vector3f offset = new Vector3f();
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis == face.getAxis()) {
                if (box != null) {
                    assign(offset, axis, sinkDepth(box, face));
                }
            } else {
                double aimed = axis.choose(hit.x, hit.y, hit.z) - CENTER;
                assign(offset, axis, Math.clamp(quantize(aimed, MOUNT_STEPS), -CENTER, CENTER));
            }
        }
        return offset;
    }

    public static boolean fitsWithOffset(Level level, BlockPos pos, BlockState state, Vector3f offset) {
        if (offset.x == 0.0F && offset.y == 0.0F && offset.z == 0.0F) {
            return true;
        }
        VoxelShape shifted = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty())
                .move(offset.x, offset.y, offset.z);
        if (shifted.isEmpty()) {
            return true;
        }

        AABB bounds = shifted.bounds();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = lowCell(bounds.minX); dx <= highCell(bounds.maxX); dx++) {
            for (int dy = lowCell(bounds.minY); dy <= highCell(bounds.maxY); dy++) {
                for (int dz = lowCell(bounds.minZ); dz <= highCell(bounds.maxZ); dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    cursor.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    VoxelShape neighbor = level.getBlockState(cursor).getCollisionShape(level, cursor, CollisionContext.empty());
                    if (!neighbor.isEmpty() && Shapes.joinIsNotEmpty(shifted, neighbor.move(dx, dy, dz), BooleanOp.AND)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean managesOwnOffset(BlockState state) {
        return SupportOffsets.isSitter(state)
                || SupportOffsets.isFacingMounted(state)
                || state.getBlock() instanceof StandingSignBlock
                || state.getBlock() instanceof WallSignBlock;
    }

    private static Vec3 localHit(BlockPos pos, Vec3 clickLocation) {
        return clickLocation.subtract(pos.getX(), pos.getY(), pos.getZ());
    }

    private static AABB surfaceBoxAt(VoxelShape shape, Direction face, Vec3 hit) {
        AABB best = null;
        double bestLevel = 0.0D;
        for (AABB box : shape.toAabbs()) {
            if (!spansHit(box, face.getAxis(), hit)) {
                continue;
            }
            double level = faceLevel(box, face);
            if (best == null || isFurtherAlong(level, bestLevel, face)) {
                best = box;
                bestLevel = level;
            }
        }
        return best;
    }

    private static AABB trimOccludedMargins(VoxelShape shape, AABB box, Direction face) {
        Direction.Axis axis = face.getAxis();
        double level = faceLevel(box, face);
        for (AABB other : shape.toAabbs()) {
            if (other == box || !sitsAtOrAbove(other, axis, level, face)) {
                continue;
            }
            for (Direction.Axis lateral : Direction.Axis.values()) {
                if (lateral == axis) {
                    continue;
                }
                Direction.Axis across = remainingAxis(axis, lateral);
                if (other.min(across) <= box.min(across) + TOLERANCE && other.max(across) >= box.max(across) - TOLERANCE) {
                    box = shrinkAlong(box, lateral, other.min(lateral), other.max(lateral));
                }
            }
        }
        return box;
    }

    private static AABB shrinkAlong(AABB box, Direction.Axis axis, double from, double to) {
        double leading = Math.max(0.0D, from - box.min(axis));
        double trailing = Math.max(0.0D, box.max(axis) - to);
        if (leading <= TOLERANCE && trailing <= TOLERANCE) {
            return box;
        }
        boolean keepLeading = leading >= trailing;
        double min = keepLeading ? box.min(axis) : to;
        double max = keepLeading ? from : box.max(axis);
        return switch (axis) {
            case X -> new AABB(min, box.minY, box.minZ, max, box.maxY, box.maxZ);
            case Y -> new AABB(box.minX, min, box.minZ, box.maxX, max, box.maxZ);
            case Z -> new AABB(box.minX, box.minY, min, box.maxX, box.maxY, max);
        };
    }

    private static boolean spansHit(AABB box, Direction.Axis faceAxis, Vec3 hit) {
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis == faceAxis) {
                continue;
            }
            double value = axis.choose(hit.x, hit.y, hit.z);
            if (value < box.min(axis) - TOLERANCE || value > box.max(axis) + TOLERANCE) {
                return false;
            }
        }
        return true;
    }

    private static boolean isFurtherAlong(double level, double reference, Direction face) {
        return face.getAxisDirection().getStep() > 0 ? level > reference : level < reference;
    }

    private static boolean sitsAtOrAbove(AABB box, Direction.Axis axis, double level, Direction face) {
        return face.getAxisDirection().getStep() > 0
                ? box.min(axis) >= level - TOLERANCE
                : box.max(axis) <= level + TOLERANCE;
    }

    private static double faceLevel(AABB box, Direction face) {
        Direction.Axis axis = face.getAxis();
        return face.getAxisDirection().getStep() > 0 ? box.max(axis) : box.min(axis);
    }

    private static double sinkDepth(AABB box, Direction face) {
        double boundary = face.getAxisDirection().getStep() > 0 ? 1.0D : 0.0D;
        return faceLevel(box, face) - boundary;
    }

    private static double centerOf(AABB box, Direction.Axis axis) {
        return (box.min(axis) + box.max(axis)) * CENTER;
    }

    private static double quantize(double value, double steps) {
        return Math.round(value * steps) / steps;
    }

    private static int lowCell(double coordinate) {
        return (int) Math.floor(coordinate);
    }

    private static int highCell(double coordinate) {
        return (int) Math.floor(coordinate - TOLERANCE);
    }

    private static void assign(Vector3f target, Direction.Axis axis, double value) {
        switch (axis) {
            case X -> target.x = (float) value;
            case Y -> target.y = (float) value;
            case Z -> target.z = (float) value;
        }
    }

    private static Direction.Axis remainingAxis(Direction.Axis first, Direction.Axis second) {
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis != first && axis != second) {
                return axis;
            }
        }
        return second;
    }
}
