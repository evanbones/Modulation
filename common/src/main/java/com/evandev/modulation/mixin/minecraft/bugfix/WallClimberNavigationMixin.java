package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("neoforge")
@Mixin(WallClimberNavigation.class)
public class WallClimberNavigationMixin {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getBbWidth()F")
    )
    private float modulation$stopCaveSpiderSpinning(float width) {
        return VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixCaveSpiderSpinningEnabled) ? Math.max(width, 1.0F) : width;
    }
}
