package com.evandev.modulation.api;

import java.util.List;

public abstract class AbstractModule implements IModule {
    private final ModuleDef def;

    protected AbstractModule(ModuleDef def) {
        this.def = def;
    }

    @Override
    public String getId() {
        return def.getId();
    }

    @Override
    public ModuleDef getDef() {
        return def;
    }

    @Override
    public boolean shouldLoad() {
        return true;
    }

    @Override
    public void initialize() {
    }

    @Override
    public List<AbstractTweak<?, ?>> getTweaks() {
        return def.getTweaks();
    }
}
