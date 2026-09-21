package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.DoubleTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class PassableFoliageModule extends AbstractModule {

    private static final ModuleDef DEF = ModuleDef.of("passable_foliage");

    public static final BooleanTweak ENABLE_PASSABLE_FOLIAGE = DEF.bool("enable_passable_foliage", false);
    public static final BooleanTweak ENABLE_SCAFFOLDING_MODE = DEF.bool("enable_scaffolding_mode", false).requires(ENABLE_PASSABLE_FOLIAGE);
    public static final BooleanTweak ENABLE_PALE_OAK_LEAVES = DEF.bool("enable_pale_oak_leaves", true).requires(ENABLE_PASSABLE_FOLIAGE);
    public static final BooleanTweak ENABLE_TINTED_LEAVES = DEF.bool("enable_tinted_leaves", true).requires(ENABLE_PASSABLE_FOLIAGE);
    public static final BooleanTweak ENABLE_TINTED_NEEDLES = DEF.bool("enable_tinted_needles", true).requires(ENABLE_PASSABLE_FOLIAGE);
    public static final BooleanTweak ENABLE_CHERRY_LEAVES = DEF.bool("enable_cherry_leaves", true).requires(ENABLE_PASSABLE_FOLIAGE);
    public static final DoubleTweak BASE_DRAG = DEF.decimal("base_drag", 0.30);
    public static final DoubleTweak MAX_DRAG = DEF.decimal("max_drag", 0.70);
    public static final BooleanTweak ENABLE_LEAF_SOUNDS = DEF.bool("enable_leaf_sounds", true).requires(ENABLE_PASSABLE_FOLIAGE);

    public PassableFoliageModule() {
        super(DEF);
    }
}
