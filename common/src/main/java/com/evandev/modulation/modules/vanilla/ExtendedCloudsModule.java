package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.DoubleTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class ExtendedCloudsModule extends AbstractModule {

    private static final ModuleDef DEF = ModuleDef.of("extended_clouds");

    public static final BooleanTweak ENABLE_EXTENDED_CLOUDS = DEF.bool("enable_extended_clouds", true);
    public static final BooleanTweak EXTEND_FRUSTUM = DEF.bool("extend_frustum", true).requires(ENABLE_EXTENDED_CLOUDS);
    public static final BooleanTweak ASYNC_CLOUD_MESHING = DEF.bool("async_cloud_meshing", true).requires(ENABLE_EXTENDED_CLOUDS);
    public static final DoubleTweak CLOUD_DISTANCE_MULTIPLIER = DEF.decimal("cloud_distance_multiplier", 4.0).range(0.1, 16.0);

    public ExtendedCloudsModule() {
        super(DEF);
    }
}
