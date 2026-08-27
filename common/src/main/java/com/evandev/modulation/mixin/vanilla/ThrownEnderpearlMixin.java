package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderpearlMixin {

    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownEnderpearl;discard()V"))
    private void modulation$playEnderPearlTeleportSound(HitResult result, CallbackInfo ci) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isEnderPearlSoundEnabled)) {
            ThrownEnderpearl pearl = (ThrownEnderpearl) (Object) this;
            pearl.level().playSound(null, pearl.getX(), pearl.getY(), pearl.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);
        }
    }

    @WrapOperation(
            method = "onHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    private boolean modulation$cancelEnderPearlDamage(Entity entity, DamageSource damageSource, float amount, Operation<Boolean> original) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isDisableEnderPearlDamageEnabled)) {
            return false;
        }
        return original.call(entity, damageSource, amount);
    }
}
