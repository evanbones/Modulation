package com.evandev.modulation.mixin.minecraft;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.minecraft.accessor.ResourceFilterSectionAccessor;
import com.evandev.modulation.modules.VanillaModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceFilterSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/FallbackResourceManager;push(Lnet/minecraft/server/packs/PackResources;Ljava/util/function/Predicate;)V")
    )
    private void modulation$fixResourceLeakPush(FallbackResourceManager instance, PackResources pack, Predicate<ResourceLocation> originalPredicate, Operation<Void> original, @Local ResourceFilterSection filterSection) {
        original.call(instance, pack, modulation$getFixedPredicate(originalPredicate, filterSection));
    }

    @WrapOperation(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/FallbackResourceManager;pushFilterOnly(Ljava/lang/String;Ljava/util/function/Predicate;)V")
    )
    private void modulation$fixResourceLeakPushFilterOnly(FallbackResourceManager instance, String packId, Predicate<ResourceLocation> originalPredicate, Operation<Void> original, @Local ResourceFilterSection filterSection) {
        original.call(instance, packId, modulation$getFixedPredicate(originalPredicate, filterSection));
    }

    private Predicate<ResourceLocation> modulation$getFixedPredicate(Predicate<ResourceLocation> originalPredicate, ResourceFilterSection filterSection) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");

        if (module != null && module.isFixResourceFilterLeakEnabled() && filterSection != null) {
            return location -> ((ResourceFilterSectionAccessor) filterSection).getBlockList().stream().anyMatch(pattern ->
                    pattern.namespacePredicate().test(location.getNamespace()) && pattern.pathPredicate().test(location.getPath())
            );
        }

        return originalPredicate;
    }
}