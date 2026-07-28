package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class VanillaGameplayModule extends AbstractModule {

    private final BooleanTweak flammableCobwebs = tweak(new BooleanTweak("flammable_cobwebs", true));
    private final BooleanTweak campfiresPlaceUnlit = tweak(new BooleanTweak("campfires_place_unlit", true));

    public VanillaGameplayModule() {
        super("vanilla_gameplay");
    }

    public boolean isFlammableCobwebsEnabled() {
        return flammableCobwebs.getValue();
    }

    public boolean isCampfiresPlaceUnlitEnabled() {
        return campfiresPlaceUnlit.getValue();
    }
}
