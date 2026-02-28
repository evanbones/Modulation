package com.evandev.modulation.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ModulationMixinPlugin implements IMixinConfigPlugin {

    private static final String MIXIN_PACKAGE = "com.evandev.modulation.mixin.";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(MIXIN_PACKAGE)) {
            String remainder = mixinClassName.substring(MIXIN_PACKAGE.length());
            int dotIndex = remainder.indexOf('.');

            if (dotIndex != -1) {
                String subPackage = remainder.substring(0, dotIndex);
                if (!subPackage.equals("minecraft")) {
                    return isModLoaded(subPackage);
                }
            }
        }
        return true;
    }

    private boolean isModLoaded(String modId) {
        try {
            Class<?> fabricLoaderClass = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object instance = fabricLoaderClass.getMethod("getInstance").invoke(null);
            return (boolean) fabricLoaderClass.getMethod("isModLoaded", String.class).invoke(instance, modId);
        } catch (Exception e) {
            try {
                Class<?> modlistClass = Class.forName("net.minecraftforge.fml.ModList");
                Object instance = modlistClass.getMethod("get").invoke(null);
                return (boolean) modlistClass.getMethod("isLoaded", String.class).invoke(instance, modId);
            } catch (Exception e2) {
                return false;
            }
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}