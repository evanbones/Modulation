package com.evandev.modulation.modules.blockgrid;

import org.joml.Vector3f;

public final class PackedSurfaceOffset {
    private static final int FACE_BITS = 3;
    private static final int FACE_MASK = (1 << FACE_BITS) - 1;
    private static final int AXIS_BITS = 6;
    private static final int AXIS_MASK = (1 << AXIS_BITS) - 1;
    private static final int AXIS_ORIGIN = 1 << (AXIS_BITS - 1);
    private static final float UNITS_PER_BLOCK = 16.0F;

    private PackedSurfaceOffset() {
    }

    public static int pack(int face, Vector3f offset) {
        return (face & FACE_MASK)
                | toUnits(offset.x) << shift(0)
                | toUnits(offset.y) << shift(1)
                | toUnits(offset.z) << shift(2);
    }

    public static int face(int packed) {
        return packed & FACE_MASK;
    }

    public static Vector3f unpack(int packed) {
        return new Vector3f(
                fromUnits(packed >> shift(0)),
                fromUnits(packed >> shift(1)),
                fromUnits(packed >> shift(2)));
    }

    private static int shift(int axis) {
        return FACE_BITS + axis * AXIS_BITS;
    }

    private static int toUnits(float blocks) {
        int units = Math.round(blocks * UNITS_PER_BLOCK) + AXIS_ORIGIN;
        return Math.clamp(units, 0, AXIS_MASK);
    }

    private static float fromUnits(int packed) {
        return ((packed & AXIS_MASK) - AXIS_ORIGIN) / UNITS_PER_BLOCK;
    }
}
