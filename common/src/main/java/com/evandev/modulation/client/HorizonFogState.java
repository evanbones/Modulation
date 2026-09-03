package com.evandev.modulation.client;

public final class HorizonFogState {
    private static float start = -1.0F;
    private static float end = -1.0F;
    private static float skyVisibility;

    public static void capture(float fogStart, float fogEnd, float skyVisibility) {
        start = fogStart;
        end = fogEnd;
        HorizonFogState.skyVisibility = skyVisibility;
    }

    public static float getStart() {
        return start;
    }

    public static float getEnd() {
        return end;
    }

    public static float getSkyVisibility() {
        return skyVisibility;
    }
}
