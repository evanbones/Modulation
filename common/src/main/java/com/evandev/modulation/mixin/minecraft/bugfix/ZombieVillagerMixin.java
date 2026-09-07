package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("debugify")
@Mixin(ZombieVillager.class)
public class ZombieVillagerMixin {

    @Inject(
            method = "finishConversion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;refreshBrain(Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER)
    )
    private void modulation$dismountCuredJockey(ServerLevel serverLevel, CallbackInfo ci, @Local Villager villager) {
        if (villager.isBaby() && villager.getVehicle() instanceof Chicken && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixCuredVillagerJockeyEnabled)) {
            villager.removeVehicle();
        }
    }
}
