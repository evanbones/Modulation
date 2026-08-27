package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.GhastAttackTimeAccess;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin implements GhastAttackTimeAccess {

    @Unique
    private int modulation$ghastAttackTime;

    @Inject(method = "tick", at = @At("TAIL"))
    private void modulation$trackGhastAttackTime(CallbackInfo ci) {
        if (!ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isGhastChargingEnabled)) {
            return;
        }

        if ((Object) this instanceof Ghast ghast && ghast.level().isClientSide) {
            if (ghast.isCharging()) {
                if (ghast.isAlive()) {
                    this.modulation$ghastAttackTime++;
                }
            } else {
                this.modulation$ghastAttackTime = 0;
            }
        }
    }

    @Override
    public int modulation$getAttackTime() {
        return this.modulation$ghastAttackTime;
    }
}
