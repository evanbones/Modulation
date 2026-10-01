package com.evandev.modulation.client;

public final class HorizonFogState {
    private static float start = -1.0F;
    private static float end = -1.0F;
    private static float skyVisibility;
    private static float skyHidden;
    private static int shape;

    public static void capture(float fogStart, float fogEnd, float skyVisibility, float skyHidden, int shape) {
        start = fogStart;
        end = fogEnd;
        HorizonFogState.skyVisibility = skyVisibility;
        HorizonFogState.skyHidden = skyHidden;
        HorizonFogState.shape = shape;
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

    public static float getSkyHidden() {
        return skyHidden;
    }

    public static int getShape() {
        return shape;
    }
}
