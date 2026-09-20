package com.evandev.modulation.modules.brainierbees;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class BrainierBeesModule extends AbstractModule {

    private static final int DEFAULT_MAX_WANDER_RADIUS = 22;
    private static final int DEFAULT_FLOWER_LOCATE_RANGE = 8;
    private static final String BRAINIER_BEES = "brainierbees";

    private final BooleanTweak enableBrainierBees = tweak(new BooleanTweak("enable_brainier_bees", true), null, BRAINIER_BEES);
    private final IntTweak maxWanderRadius = tweak(new IntTweak("max_wander_radius", DEFAULT_MAX_WANDER_RADIUS), null, BRAINIER_BEES);
    private final IntTweak flowerLocateRange = tweak(new IntTweak("flower_locate_range", DEFAULT_FLOWER_LOCATE_RANGE), null, BRAINIER_BEES);
    private final BooleanTweak beesAvoidLadders = tweak(new BooleanTweak("bees_avoid_ladders", true), null, BRAINIER_BEES);

    public BrainierBeesModule() {
        super("brainier_bees");
    }

    private static int clamp(Integer value, int min, int max, int fallback) {
        if (value == null) {
            return fallback;
        }
        return Math.max(min, Math.min(max, value));
    }

    public static BrainierBeesModule get() {
        return ModuleManager.getModule("brainier_bees", BrainierBeesModule.class);
    }

    public static boolean enabled() {
        return ModuleManager.isEnabled("brainier_bees", BrainierBeesModule.class, BrainierBeesModule::isBrainierBeesEnabled);
    }

    public static int maxWanderRadius() {
        BrainierBeesModule module = get();
        return module == null ? DEFAULT_MAX_WANDER_RADIUS : module.getMaxWanderRadius();
    }

    public static int flowerLocateRange() {
        BrainierBeesModule module = get();
        return module == null ? DEFAULT_FLOWER_LOCATE_RANGE : module.getFlowerLocateRange();
    }

    public boolean isBrainierBeesEnabled() {
        return enableBrainierBees.getBlockingMod() == null && enableBrainierBees.getValue();
    }

    public boolean isBeesAvoidLaddersEnabled() {
        return isBrainierBeesEnabled() && beesAvoidLadders.getValue();
    }

    public int getMaxWanderRadius() {
        return clamp(maxWanderRadius.getValue(), 4, 128, DEFAULT_MAX_WANDER_RADIUS);
    }

    public int getFlowerLocateRange() {
        return clamp(flowerLocateRange.getValue(), 1, 32, DEFAULT_FLOWER_LOCATE_RANGE);
    }
}
