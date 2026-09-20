package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("ghastdirection")
@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastLookGoal")
public abstract class GhastLookGoalMixin {

    @Shadow
    @Final
    private Ghast ghast;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Ghast;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"
            ),
            cancellable = true
    )
    private void modulation$keepFacingWhileStill(CallbackInfo ci) {
        if (this.ghast.getDeltaMovement().lengthSqr() < 0.01
                && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixGhastDirectionEnabled)) {
            ci.cancel();
        }
    }
}
