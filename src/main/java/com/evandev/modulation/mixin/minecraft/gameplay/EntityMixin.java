package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.mixin.minecraft.accessor.ThrownTridentAccessor;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Final
    @Shadow
    protected SynchedEntityData entityData;

    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    private void modulation$tridentsInVoidReturn(CallbackInfo ci) {
        if ((Object) this instanceof ThrownTrident trident) {
            if (VanillaGameplayModule.TRIDENTS_IN_VOID_RETURN.on()) {
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
