package com.evandev.modulation.mixin.minecraft.bugfix;

//? if <26.1 {
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getPickRadius", at = @At("HEAD"), cancellable = true)
    private void onGetPickRadius(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof Villager villager && villager.isSleeping()) {
            if (VanillaBugfixesModule.ATTACK_SLEEPING_VILLAGERS.on()) {
                cir.setReturnValue(0.5F);
            }
        }
    }

    @IfModAbsent("debugify")
    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    private void modulation$keepLightningDeathDrops(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        if ((Object) this instanceof ItemEntity itemEntity && itemEntity.tickCount <= 8) {
            if (VanillaBugfixesModule.FIX_LIGHTNING_ITEM_DROPS.on()) {
                ci.cancel();
            }
        }
    }
}
//?}
