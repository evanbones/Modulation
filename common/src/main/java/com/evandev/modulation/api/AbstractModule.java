package com.evandev.modulation.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for modules that registers each tweak at the point it's declared.
 */
public abstract class AbstractModule implements IModule {
    private final List<AbstractTweak<?>> tweaks = new ArrayList<>();

    protected <T extends AbstractTweak<?>> T tweak(T tweak) {
        tweaks.add(tweak);
        return tweak;
    }

    @Override
    public List<AbstractTweak<?>> getTweaks() {
        return Collections.unmodifiableList(tweaks);
    }
}
