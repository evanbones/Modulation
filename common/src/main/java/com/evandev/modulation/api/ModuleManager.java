package com.evandev.modulation.api;

import com.evandev.modulation.Constants;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Predicate;

public class ModuleManager {
    private static final Map<String, IModule> MODULES = new LinkedHashMap<>();

    public static void loadModules() {
        for (IModule module : ServiceLoader.load(IModule.class)) {
            register(module);
        }
    }

    private static void register(IModule module) {
        if (module.shouldLoad()) {
            MODULES.put(module.getId(), module);
            module.initialize();
            Constants.LOG.info("Loaded Modulation module: {}", module.getId());
        }
    }

    public static Collection<IModule> getModules() {
        return MODULES.values();
    }

    public static IModule getModule(String id) {
        return MODULES.get(id);
    }

    public static <T extends IModule> T getModule(String id, Class<T> type) {
        IModule module = MODULES.get(id);
        return type.isInstance(module) ? type.cast(module) : null;
    }

    public static <T extends IModule> boolean isEnabled(String id, Class<T> type, Predicate<T> check) {
        T module = getModule(id, type);
        return module != null && check.test(module);
    }
}