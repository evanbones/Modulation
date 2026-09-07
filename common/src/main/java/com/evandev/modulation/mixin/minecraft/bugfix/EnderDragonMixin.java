package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("debugify")
@Mixin(EnderDragon.class)
public class EnderDragonMixin {

    @Shadow
    public int dragonDeathTime;

    @Shadow
    public EndCrystal nearestCrystal;

    @WrapMethod(method = "checkCrystals")
    private void modulation$noCrystalHealingWhileDying(Operation<Void> original) {
        if (dragonDeathTime > 0 && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixCrystalsHealingDyingDragonEnabled)) {
            nearestCrystal = null;
            return;
        }
        original.call();
    }

    @Inject(method = "tickDeath", at = @At("HEAD"))
    private void modulation$clearCrystalOnDeath(CallbackInfo ci) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixCrystalsHealingDyingDragonEnabled)) {
            nearestCrystal = null;
        }
    }
}
