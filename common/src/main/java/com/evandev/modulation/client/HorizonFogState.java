package com.evandev.modulation.client;

public final class HorizonFogState {
    private static float start = -1.0F;
    private static float end = -1.0F;

    public static void capture(float fogStart, float fogEnd) {
        start = fogStart;
        end = fogEnd;
    }

    public static float getStart() {
        return start;
    }

    public static float getEnd() {
        return end;
    }
}
