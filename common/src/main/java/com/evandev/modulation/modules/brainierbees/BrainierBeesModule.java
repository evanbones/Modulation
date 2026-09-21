package com.evandev.modulation.modules.brainierbees;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class BrainierBeesModule extends AbstractModule {

    private static final int DEFAULT_MAX_WANDER_RADIUS = 22;
    private static final int DEFAULT_FLOWER_LOCATE_RANGE = 8;
    private static final String BRAINIER_BEES = "brainierbees";

    private static final ModuleDef DEF = ModuleDef.of("brainier_bees");

    public static final BooleanTweak ENABLE_BRAINIER_BEES = DEF.bool("enable_brainier_bees", true).conflicts(BRAINIER_BEES);
    public static final IntTweak MAX_WANDER_RADIUS = DEF.integer("max_wander_radius", DEFAULT_MAX_WANDER_RADIUS).range(4, 128).conflicts(BRAINIER_BEES);
    public static final IntTweak FLOWER_LOCATE_RANGE = DEF.integer("flower_locate_range", DEFAULT_FLOWER_LOCATE_RANGE).range(1, 32).conflicts(BRAINIER_BEES);
    public static final BooleanTweak BEES_AVOID_LADDERS = DEF.bool("bees_avoid_ladders", true).conflicts(BRAINIER_BEES).requires(ENABLE_BRAINIER_BEES);

    public BrainierBeesModule() {
        super(DEF);
    }
}
