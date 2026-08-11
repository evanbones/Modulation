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
                if (!subPackage.equals("vanilla")) {
                    return isModLoaded(subPackage);
                }
            }
        }
        return true;
    }

    private boolean isModLoaded(String modId) {
        return switch (modId) {
            case "vanillabackport" -> isClassPresent("com.blackgear.vanillabackport.core.VanillaBackport");
            case "figura" -> isClassPresent("net.figura.Figura");
            case "connectiblechains" -> isClassPresent("com.evandev.connectiblechains.command.ConnectChainCommand");
            case "sodium" -> isClassPresent("net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer");
            case "polytone" -> isClassPresent("net.mehvahdjukaar.polytone.Polytone");
            default -> true;
        };
    }

    private boolean isClassPresent(String className) {
        String path = className.replace('.', '/') + ".class";
        return this.getClass().getClassLoader().getResource(path) != null;
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