package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.DoubleTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class PassableFoliageModule extends AbstractModule {

    private final BooleanTweak enablePassableFoliage = tweak(new BooleanTweak("enable_passable_foliage", false));
    private final BooleanTweak enableScaffoldingMode = tweak(new BooleanTweak("enable_scaffolding_mode", false));
    private final BooleanTweak enablePaleOakLeaves = tweak(new BooleanTweak("enable_pale_oak_leaves", true));
    private final BooleanTweak enableTintedLeaves = tweak(new BooleanTweak("enable_tinted_leaves", true));
    private final BooleanTweak enableTintedNeedles = tweak(new BooleanTweak("enable_tinted_needles", true));
    private final BooleanTweak enableCherryLeaves = tweak(new BooleanTweak("enable_cherry_leaves", true));
    private final DoubleTweak baseDrag = tweak(new DoubleTweak("base_drag", 0.30));
    private final DoubleTweak maxDrag = tweak(new DoubleTweak("max_drag", 0.70));
    private final BooleanTweak enableLeafSounds = tweak(new BooleanTweak("enable_leaf_sounds", true));

    public PassableFoliageModule() {
        super("passable_foliage");
    }

    public boolean isPassableFoliageEnabled() {
        return enablePassableFoliage.getValue();
    }

    public boolean isScaffoldingModeEnabled() {
        return enableScaffoldingMode.getValue();
    }

    public boolean isPaleOakLeavesEnabled() {
        return enablePaleOakLeaves.getValue();
    }

    public boolean isTintedLeavesEnabled() {
        return enableTintedLeaves.getValue();
    }

    public boolean isTintedNeedlesEnabled() {
        return enableTintedNeedles.getValue();
    }

    public boolean isCherryLeavesEnabled() {
        return enableCherryLeaves.getValue();
    }

    public double getBaseDrag() {
        return baseDrag.getValue();
    }

    public double getMaxDrag() {
        return maxDrag.getValue();
    }

    public boolean isLeafSoundsEnabled() {
        return enableLeafSounds.getValue();
    }
}
