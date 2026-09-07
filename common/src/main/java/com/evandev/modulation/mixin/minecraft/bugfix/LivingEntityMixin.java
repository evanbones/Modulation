package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "hurt", at = @At("HEAD"))
    private void modulation$lightCreepersOnExplosion(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Creeper creeper && source.is(DamageTypeTags.IS_EXPLOSION)) {
            if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isChainingCreepersEnabled)) {
                if (creeper.getHealth() < 3.0F && amount >= 3.0F) {
                    return;
                }
                creeper.ignite();
            }
        }
    }

    @IfModAbsent("debugify")
    @ModifyExpressionValue(
            method = "checkFallDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z")
    )
    private boolean modulation$noSlowFallingLandingParticles(boolean isAir) {
        if (!isAir && ((Object) this instanceof Chicken || (Object) this instanceof Blaze || (Object) this instanceof WitherBoss)) {
            return VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixSlowFallingParticlesEnabled);
        }
        return isAir;
    }

    @WrapOperation(
            method = "travel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D")
    )
    private double modulation$riptideIgnoresDepthStrider(LivingEntity self, Holder<Attribute> attribute, Operation<Double> original) {
        if (attribute == Attributes.WATER_MOVEMENT_EFFICIENCY && self.isAutoSpinAttack() && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixRiptideDepthStriderEnabled)) {
            return 0.0;
        }
        return original.call(self, attribute);
    }

    @ModifyArg(
            method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V")
    )
    private SoundSource modulation$finalEatingSoundSource(SoundSource source) {
        return VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixEatingSoundEnabled) ? SoundSource.PLAYERS : source;
    }

    @WrapOperation(
            method = "handleEntityEvent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V")
    )
    private void modulation$audibleShieldSounds(LivingEntity self, SoundEvent sound, float volume, float pitch, Operation<Void> original) {
        boolean shieldSound = sound == SoundEvents.SHIELD_BLOCK || sound == SoundEvents.SHIELD_BREAK;
        if (shieldSound && self instanceof Player && self.level().isClientSide && !self.isSilent()
                && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixShieldSoundsEnabled)) {
            self.level().playLocalSound(self.getX(), self.getY(), self.getZ(), sound, self.getSoundSource(), volume, pitch, false);
            return;
        }
        original.call(self, sound, volume, pitch);
    }
}
