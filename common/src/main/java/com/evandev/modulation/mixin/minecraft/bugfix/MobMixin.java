package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("debugify")
@Mixin(Mob.class)
public abstract class MobMixin {

    @Shadow
    private LivingEntity target;

    @Shadow
    public abstract void setTarget(LivingEntity target);

    @Inject(
            method = "baseTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V")
    )
    private void modulation$clearDeadTarget(CallbackInfo ci) {
        if (target != null && target.isDeadOrDying() && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixGroupAiTargetDeathEnabled)) {
            setTarget(null);
        }
    }
}
