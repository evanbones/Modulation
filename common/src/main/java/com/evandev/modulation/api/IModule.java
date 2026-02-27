package com.evandev.modulation.api;

import java.util.List;

public interface IModule {
    String getId();

    boolean shouldLoad();

    List<AbstractTweak<?>> getTweaks();

    void initialize();
}