package com.evandev.modulation.modules.blockgrid;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class BlockGridModule extends AbstractModule {

    private final BooleanTweak enableBlockOffsets = tweak(new BooleanTweak("enable_block_offsets", true));
    private final BooleanTweak enableSlabOffsets = tweak(new BooleanTweak("enable_slab_offsets", true));
    private final BooleanTweak enableSignOffsets = tweak(new BooleanTweak("enable_sign_offsets", true));
    private final BooleanTweak enableHangingEntityOffsets = tweak(new BooleanTweak("enable_hanging_entity_offsets", true));
    private final BooleanTweak enableParticleOffsets = tweak(new BooleanTweak("enable_particle_offsets", true));
    private final BooleanTweak enableBlockEntityOffsets = tweak(new BooleanTweak("enable_block_entity_offsets", true));
    private final BooleanTweak enableRedstoneConduction = tweak(new BooleanTweak("enable_redstone_conduction", true));
    private final BooleanTweak enablePartialSupportSurvival = tweak(new BooleanTweak("enable_partial_support_survival", true));

    public BlockGridModule() {
        super("block_grid");
    }

    public boolean isEnableBlockOffsets() {
        return enableBlockOffsets.getValue();
    }

    public boolean isEnableSlabOffsets() {
        return enableSlabOffsets.getValue();
    }

    public boolean isEnableSignOffsets() {
        return enableSignOffsets.getValue();
    }

    public boolean isEnableHangingEntityOffsets() {
        return enableHangingEntityOffsets.getValue();
    }

    public boolean isEnableParticleOffsets() {
        return enableParticleOffsets.getValue();
    }

    public boolean isEnableBlockEntityOffsets() {
        return enableBlockEntityOffsets.getValue();
    }

    public boolean isEnableRedstoneConduction() {
        return enableRedstoneConduction.getValue();
    }

    public boolean isEnablePartialSupportSurvival() {
        return enablePartialSupportSurvival.getValue();
    }
}
