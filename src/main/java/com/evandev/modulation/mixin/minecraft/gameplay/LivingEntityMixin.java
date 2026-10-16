package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=26.1 {
/*import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
*///?} else {
import net.minecraft.world.entity.vehicle.Boat;
//?}

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    //? if >=26.1 {
    /*@Inject(method = "hurtServer", at = @At("HEAD"))
    private void modulation$lightCreepersOnExplosion(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.modulation$chainCreeper(source, amount);
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void modulation$monstersLeaveBoats(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.modulation$leaveBoat(source, cir.getReturnValueZ());
    }
    *///?} else {
    @Inject(method = "hurt", at = @At("HEAD"))
    private void modulation$lightCreepersOnExplosion(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.modulation$chainCreeper(source, amount);
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void modulation$monstersLeaveBoats(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.modulation$leaveBoat(source, cir.getReturnValueZ());
    }
    //?}

    @Unique
    private void modulation$chainCreeper(DamageSource source, float amount) {
        if ((Object) this instanceof Creeper creeper && source.is(DamageTypeTags.IS_EXPLOSION)) {
            if (VanillaGameplayModule.CHAINING_CREEPERS.on()) {
                if (creeper.getHealth() < 3.0F && amount >= 3.0F) {
                    return;
                }
                creeper.ignite();
            }
        }
    }

    @Unique
    private void modulation$leaveBoat(DamageSource source, boolean hurt) {
        if (!hurt) {
            return;
        }
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level().isClientSide() || !(entity instanceof Enemy) || source.getEntity() == null) {
            return;
        }
        //? if >=26.1 {
        /*boolean inBoat = entity.getVehicle() instanceof AbstractBoat;
        *///?} else {
        boolean inBoat = entity.getVehicle() instanceof Boat;
        //?}
        if (inBoat && VanillaGameplayModule.MONSTERS_LEAVE_BOATS.on()) {
            entity.stopRiding();
        }
    }
}
