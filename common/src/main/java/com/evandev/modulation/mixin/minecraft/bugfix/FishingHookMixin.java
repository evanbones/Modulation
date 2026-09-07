package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("debugify")
@Mixin(FishingHook.class)
public class FishingHookMixin {

    @Inject(
            method = "pullEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    )
    private void modulation$creditFishingRodKills(Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixFishingRodKillCreditEnabled)) {
            FishingHook hook = (FishingHook) (Object) this;
            living.getCombatTracker().recordDamage(hook.level().damageSources().thrown(hook, hook.getOwner()), living.getHealth());
        }
    }
}
