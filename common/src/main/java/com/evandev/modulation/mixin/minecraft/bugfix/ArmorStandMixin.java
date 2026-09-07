package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModAbsent("debugify")
@Mixin(ArmorStand.class)
public class ArmorStandMixin {

    @Shadow
    private void showBreakingParticles() {
        throw new AssertionError();
    }

    @Inject(
            method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ArmorStand;brokenByAnything(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", ordinal = 0)
    )
    private void modulation$explosionBreakParticles(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixArmorStandBreakParticlesEnabled)) {
            showBreakingParticles();
        }
    }

    @Inject(
            method = "causeDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ArmorStand;brokenByAnything(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", ordinal = 0)
    )
    private void modulation$fireBreakParticles(ServerLevel level, DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixArmorStandBreakParticlesEnabled)) {
            showBreakingParticles();
        }
    }
}
