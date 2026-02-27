package com.evandev.modulation.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModuleManager {
    private static final Map<String, IModule> MODULES = new LinkedHashMap<>();

    public static void register(IModule module) {
        if (module.shouldLoad()) {
            MODULES.put(module.getId(), module);
            module.initialize();
        }
    }

    public static Collection<IModule> getModules() {
        return MODULES.values();
    }

    public static IModule getModule(String id) {
        return MODULES.get(id);
    }
}