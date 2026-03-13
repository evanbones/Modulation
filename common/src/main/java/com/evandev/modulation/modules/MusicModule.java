package com.evandev.modulation.modules;

import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.evandev.modulation.api.tweaks.StringListTweak;

import java.util.List;

public class MusicModule implements IModule {
    public final IntTweak minPursuitEntities = new IntTweak("min_pursuit_entities", 3);
    public final IntTweak decayTime = new IntTweak("decay_time", 20);
    public final StringListTweak sounds = new StringListTweak("sounds", List.of("minecraft:music_disc.pigstep", "minecraft:music_disc.mellohi"));
    public final BooleanTweak enabled = new BooleanTweak("enabled", false);

    @Override
    public String getId() {
        return "music";
    }

    @Override
    public boolean shouldLoad() {
        return true;
    }

    @Override
    public List<AbstractTweak<?>> getTweaks() {
        return List.of(enabled, minPursuitEntities, decayTime, sounds);
    }

    @Override
    public void initialize() {
    }
}