package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.vanilla.accessor.ThrownTridentAccessor;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Final
    @Shadow
    protected SynchedEntityData entityData;

    @Inject(method = "getPickRadius", at = @At("HEAD"), cancellable = true)
    private void onGetPickRadius(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof Villager villager && villager.isSleeping()) {
            if (ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isAttackSleepingVillagersEnabled)) {
                cir.setReturnValue(0.5F);
            }
        }
    }

    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    private void modulation$tridentsInVoidReturn(CallbackInfo ci) {
        if ((Object) this instanceof ThrownTrident trident) {
            if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isTridentsInVoidReturnEnabled)) {
                if (trident.getOwner() != null) {
                    int loyalty = this.entityData.get(ThrownTridentAccessor.modulation$getIdLoyalty());
                    if (loyalty > 0) {
                        ((ThrownTridentAccessor) trident).modulation$setDealtDamage(true);
                        ci.cancel();
                    }
                }
            }
        }
    }
}