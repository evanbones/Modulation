package com.evandev.modulation.modules.farmersdelight;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.platform.Services;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class FarmersDelightModule extends AbstractModule {

    private final BooleanTweak stovesPlaceUnlit = tweak(new BooleanTweak("stoves_place_unlit", false));

    public FarmersDelightModule() {
        super("farmers_delight");
    }

    @Override
    public boolean shouldLoad() {
        return Services.PLATFORM.isModLoaded("farmersdelight");
    }

    public boolean isStovesPlaceUnlitEnabled() {
        return stovesPlaceUnlit.getValue();
    }
}
