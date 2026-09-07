package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.level.levelgen.DensityFunctions$EndIslandDensityFunction")
public class EndIslandDensityFunctionMixin {

    @WrapOperation(
            method = "getHeightValue",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;sqrt(F)F")
    )
    private static float modulation$endIslandRings(float value, Operation<Float> original, SimplexNoise noise, int x, int z) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixEndIslandRingsEnabled)) {
            return Mth.sqrt((float) ((long) x * (long) x + (long) z * (long) z));
        }
        return original.call(value);
    }
}
