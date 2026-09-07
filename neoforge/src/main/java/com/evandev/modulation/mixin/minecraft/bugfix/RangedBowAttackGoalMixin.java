package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("debugify")
@Mixin(RangedBowAttackGoal.class)
public class RangedBowAttackGoalMixin {

    @Shadow
    @Final
    private Mob mob;

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;lookAt(Lnet/minecraft/world/entity/Entity;FF)V", shift = At.Shift.AFTER)
    )
    private void modulation$aimAtTargetWhileStrafing(CallbackInfo ci) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixStrafingMobAimEnabled)) {
            mob.getLookControl().setLookAt(mob.getTarget(), 30.0F, 30.0F);
        }
    }
}
