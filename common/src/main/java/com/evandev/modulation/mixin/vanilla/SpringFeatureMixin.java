package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpringFeature.class)
public class SpringFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void modulation$noDinnerlava(FeaturePlaceContext<SpringConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isNoDinnerlavaEnabled)) {
            SpringConfiguration config = context.config();
            if (config.holeCount == 0 && config.state.is(FluidTags.LAVA)) {
                cir.setReturnValue(false);
            }
        }
    }
}
