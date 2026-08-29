package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class VanillaBugfixesModule extends AbstractModule {

    private final BooleanTweak fixFocusBug = tweak(new BooleanTweak("fix_focus_bug", true));
    private final BooleanTweak attackSleepingVillagers = tweak(new BooleanTweak("attack_sleeping_villagers", true));
    private final BooleanTweak fixExperienceLoss = tweak(new BooleanTweak("fix_experience_loss", true));
    private final BooleanTweak fixResourceFilterLeak = tweak(new BooleanTweak("fix_resource_filter_leak", true));
    private final BooleanTweak fixHorizonLine = tweak(new BooleanTweak("fix_horizon_line", true));
    private final BooleanTweak fixCaveSky = tweak(new BooleanTweak("fix_cave_sky", true));
    private final BooleanTweak betterPauseFreezing = tweak(new BooleanTweak("better_pause_freezing", true));
    private final BooleanTweak coloredCrackParticles = tweak(new BooleanTweak("colored_crack_particles", true));
    private final BooleanTweak ghastCharging = tweak(new BooleanTweak("ghast_charging", true));
    private final BooleanTweak fixMobsCrossingRails = tweak(new BooleanTweak("fix_mobs_crossing_rails", false));

    public VanillaBugfixesModule() {
        super("vanilla_bugfixes");
    }

    public boolean isFixFocusBugEnabled() {
        return fixFocusBug.getValue();
    }

    public boolean isAttackSleepingVillagersEnabled() {
        return attackSleepingVillagers.getValue();
    }

    public boolean isFixExperienceLossEnabled() {
        return fixExperienceLoss.getValue();
    }

    public boolean isFixResourceFilterLeakEnabled() {
        return fixResourceFilterLeak.getValue();
    }

    public boolean isFixHorizonLineEnabled() {
        return fixHorizonLine.getValue();
    }

    public boolean isFixCaveSkyEnabled() {
        return fixCaveSky.getValue();
    }

    public boolean isBetterPauseFreezingEnabled() {
        return betterPauseFreezing.getValue();
    }

    public boolean isColoredCrackParticlesEnabled() {
        return coloredCrackParticles.getValue();
    }

    public boolean isGhastChargingEnabled() {
        return ghastCharging.getValue();
    }

    public boolean isFixMobsCrossingRailsEnabled() {
        return fixMobsCrossingRails.getValue();
    }
}
