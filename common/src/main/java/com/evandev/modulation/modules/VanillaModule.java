package com.evandev.modulation.modules;

import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;

import java.util.List;

public class VanillaModule implements IModule {

    private final BooleanTweak fixFocusBug = new BooleanTweak("fix_focus_bug", true);
    private final BooleanTweak attackSleepingVillagers = new BooleanTweak("attack_sleeping_villagers", true);

    @Override
    public String getId() {
        return "vanilla";
    }

    @Override
    public boolean shouldLoad() {
        return true;
    }

    @Override
    public List<AbstractTweak<?>> getTweaks() {
        return List.of(fixFocusBug, attackSleepingVillagers);
    }

    @Override
    public void initialize() {
    }

    public boolean isFixFocusBugEnabled() {
        return fixFocusBug.getValue();
    }

    public boolean isAttackSleepingVillagersEnabled() {
        return attackSleepingVillagers.getValue();
    }
}