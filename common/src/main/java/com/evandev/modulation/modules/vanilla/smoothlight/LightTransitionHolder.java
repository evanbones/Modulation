package com.evandev.modulation.modules.vanilla.smoothlight;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public interface LightTransitionHolder {

    ConcurrentHashMap<Long, LightTransition> modulation$lightTransitions();

    @Nullable
    ConcurrentHashMap<Long, LightTransition> modulation$lightTransitionsOrNull();
}
