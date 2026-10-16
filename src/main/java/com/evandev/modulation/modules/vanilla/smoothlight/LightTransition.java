package com.evandev.modulation.modules.vanilla.smoothlight;

public record LightTransition(int currentEmission, int targetEmission, int currentOpacity, int targetOpacity) {

    private static int approach(int current, int target, int step) {
        if (current < target) return Math.min(current + step, target);
        if (current > target) return Math.max(current - step, target);
        return current;
    }

    public boolean done() {
        return currentEmission == targetEmission && currentOpacity == targetOpacity;
    }

    public boolean opacityChanging() {
        return currentOpacity != targetOpacity;
    }

    public LightTransition step(int step) {
        return new LightTransition(
                approach(currentEmission, targetEmission, step), targetEmission,
                approach(currentOpacity, targetOpacity, step), targetOpacity);
    }
}
