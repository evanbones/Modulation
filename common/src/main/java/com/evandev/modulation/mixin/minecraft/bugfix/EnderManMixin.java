package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
public class EnderManMixin {

    @WrapOperation(
            method = "customServerAiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/EnderMan;getLightLevelDependentMagicValue()F")
    )
    private float modulation$noTeleportWhileRiding(EnderMan enderMan, Operation<Float> original) {
        if (enderMan.isPassenger() && ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixEndermanTeleportSpamEnabled)) {
            return 0.0F;
        }
        return original.call(enderMan);
    }
}
