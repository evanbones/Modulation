// SPDX-License-Identifier: AGPL-3.0-only
// Ported from BetterWalls (https://modrinth.com/mod/betterwalls) by Lemonnik6484 and JX_Snack,
// licensed under AGPL-3.0-only. This file (and its accompanying mixins) is licensed under
// AGPL-3.0-only, as an exception to the repository's overall MIT license — see LICENSE.
package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class VanillaWallsModule extends AbstractModule {

    private static final ModuleDef DEF = ModuleDef.of("vanilla_walls");

    public static final BooleanTweak WALLS_CONNECT_TO_FENCES = DEF.bool("walls_connect_to_fences", true);
    public static final BooleanTweak FENCES_CONNECT_TO_WALLS_AND_BARS = DEF.bool("fences_connect_to_walls_and_bars", true);
    public static final BooleanTweak BARS_CONNECT_TO_FENCES = DEF.bool("bars_connect_to_fences", true);

    public VanillaWallsModule() {
        super(DEF);
    }
}
