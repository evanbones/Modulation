package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean modulation$experienceOrbLavaSurface(boolean inLava) {
        if (inLava && ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixExperienceOrbLavaEnabled)) {
            return ((ExperienceOrb) (Object) this).isEyeInFluid(FluidTags.LAVA);
        }
        return inLava;
    }
}
