package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.compat.dyedflames.DyedFlamesCompat;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.evandev.modulation.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
//? if >=26.1
//import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void modulation$placeCampfiresUnlit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (VanillaGameplayModule.CAMPFIRES_PLACE_UNLIT.on()) {
            BlockState state = cir.getReturnValue();
            if (state != null && state.hasProperty(CampfireBlock.LIT)) {
                cir.setReturnValue(state.setValue(CampfireBlock.LIT, false));
            }
        }
    }

    //? if >=26.1 {
    /*@Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    private void modulation$igniteEntities(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo ci) {
    *///?} else {
    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void modulation$igniteEntities(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
    //?}
        if (VanillaGameplayModule.CAMPFIRES_IGNITE_ENTITIES.on()) {
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
            if (entity.getRemainingFireTicks() == 0) {
                entity.igniteForSeconds(8.0F);
            }
            if (Services.PLATFORM.isModLoaded("dyedflames")) {
                DyedFlamesCompat.onCampfireInside(entity, state);
            }
        }
    }
}
