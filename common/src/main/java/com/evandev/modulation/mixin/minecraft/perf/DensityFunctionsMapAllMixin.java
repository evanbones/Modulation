package com.evandev.modulation.mixin.minecraft.perf;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.VanillaModule;
import com.evandev.modulation.perf.MapAllCache;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
        "net.minecraft.world.level.levelgen.DensityFunctions$Ap2",
        "net.minecraft.world.level.levelgen.DensityFunctions$RangeChoice",
        "net.minecraft.world.level.levelgen.DensityFunctions$ShiftedNoise",
        "net.minecraft.world.level.levelgen.DensityFunctions$Spline"
})
public class DensityFunctionsMapAllMixin {

    @Inject(method = "mapAll", at = @At("HEAD"), cancellable = true)
    private void modulation$checkCacheBeforeRecursion(DensityFunction.Visitor visitor, CallbackInfoReturnable<DensityFunction> cir) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isFixDensityMemoizationEnabled()) {
            DensityFunction self = (DensityFunction) this;
            DensityFunction cached = MapAllCache.get(visitor, self);
            if (cached != null) {
                cir.setReturnValue(cached);
                return;
            }
            MapAllCache.push();
        }
    }

    @Inject(method = "mapAll", at = @At("RETURN"))
    private void modulation$saveToCacheAfterRecursion(DensityFunction.Visitor visitor, CallbackInfoReturnable<DensityFunction> cir) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isFixDensityMemoizationEnabled()) {
            DensityFunction self = (DensityFunction) this;
            MapAllCache.put(visitor, self, cir.getReturnValue());
            MapAllCache.pop();
        }
    }
}