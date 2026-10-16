package com.evandev.modulation.modules.farmersdelight;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.platform.Services;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class FarmersDelightModule extends AbstractModule {

    private static final ModuleDef DEF = ModuleDef.of("farmers_delight");

    public static final BooleanTweak STOVES_PLACE_UNLIT = DEF.bool("stoves_place_unlit", false);

    public FarmersDelightModule() {
        super(DEF);
    }

    @Override
    public boolean shouldLoad() {
        return Services.PLATFORM.isModLoaded("farmersdelight");
    }
}
