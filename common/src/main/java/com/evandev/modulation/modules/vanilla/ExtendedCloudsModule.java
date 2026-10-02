package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class ExtendedCloudsModule extends AbstractModule {

    private static final ModuleDef DEF = ModuleDef.of("extended_clouds");

    public static final BooleanTweak ENABLE_EXTENDED_CLOUDS = DEF.bool("enable_extended_clouds", true);
    public static final BooleanTweak EXTEND_FRUSTUM = DEF.bool("extend_frustum", true).requires(ENABLE_EXTENDED_CLOUDS);
    public static final BooleanTweak ASYNC_CLOUD_MESHING = DEF.bool("async_cloud_meshing", true).requires(ENABLE_EXTENDED_CLOUDS);
    public static final IntTweak CLOUD_RANGE = DEF.integer("cloud_range", 64).range(2, 128);

    public ExtendedCloudsModule() {
        super(DEF);
    }
}
