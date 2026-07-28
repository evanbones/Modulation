// SPDX-License-Identifier: AGPL-3.0-only
// Ported from BetterWalls (https://modrinth.com/mod/betterwalls) by Lemonnik6484 and JX_Snack,
// licensed under AGPL-3.0-only. This file (and its accompanying mixins) is licensed under
// AGPL-3.0-only, as an exception to the repository's overall MIT license — see LICENSE.
package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class VanillaWallsModule extends AbstractModule {

    private final BooleanTweak wallsConnectToFences = tweak(new BooleanTweak("walls_connect_to_fences", true));
    private final BooleanTweak fencesConnectToWallsAndBars = tweak(new BooleanTweak("fences_connect_to_walls_and_bars", true));
    private final BooleanTweak barsConnectToFences = tweak(new BooleanTweak("bars_connect_to_fences", true));

    public VanillaWallsModule() {
        super("vanilla_walls");
    }

    public boolean isWallsConnectToFencesEnabled() {
        return wallsConnectToFences.getValue();
    }

    public boolean isFencesConnectToWallsAndBarsEnabled() {
        return fencesConnectToWallsAndBars.getValue();
    }

    public boolean isBarsConnectToFencesEnabled() {
        return barsConnectToFences.getValue();
    }
}
