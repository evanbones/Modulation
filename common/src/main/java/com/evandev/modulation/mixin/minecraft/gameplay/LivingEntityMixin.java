package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "hurt", at = @At("RETURN"))
    private void modulation$monstersLeaveBoats(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level().isClientSide || !(entity instanceof Enemy) || source.getEntity() == null) {
            return;
        }
        if (entity.getVehicle() instanceof Boat
                && VanillaGameplayModule.MONSTERS_LEAVE_BOATS.on()) {
            entity.stopRiding();
        }
    }
}
