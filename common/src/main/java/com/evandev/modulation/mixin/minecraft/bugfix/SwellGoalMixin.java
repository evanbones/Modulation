package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("debugify")
@Mixin(SwellGoal.class)
public class SwellGoalMixin {

    @Shadow
    private LivingEntity target;

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/sensing/Sensing;hasLineOfSight(Lnet/minecraft/world/entity/Entity;)Z")
    )
    private boolean modulation$defuseCreeperOnGameModeChange(boolean canSeeTarget) {
        if (canSeeTarget && target != null && !target.canBeSeenAsEnemy()) {
            return !VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixCreeperDefusingEnabled);
        }
        return canSeeTarget;
    }
}
