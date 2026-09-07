package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.compat.dyedflames.DyedFlamesCompat;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.evandev.modulation.platform.Services;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

    @IfModAbsent("neoforge")
    @WrapOperation(
            method = "isSmokeyPos",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 0)
    )
    private static BlockState modulation$captureSmokeCheckPos(Level level, BlockPos pos, Operation<BlockState> original, @Share("smokePos") LocalRef<BlockPos> smokePos) {
        smokePos.set(pos);
        return original.call(level, pos);
    }

    @IfModAbsent("neoforge")
    @ModifyArg(
            method = "isSmokeyPos",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"),
            index = 1
    )
    private static BlockPos modulation$campfireSmokeCollisionPos(BlockPos pos, @Share("smokePos") LocalRef<BlockPos> smokePos) {
        if (smokePos.get() != null && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixCampfireSmokePositionEnabled)) {
            return smokePos.get();
        }
        return pos;
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void modulation$placeCampfiresUnlit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isCampfiresPlaceUnlitEnabled)) {
            BlockState state = cir.getReturnValue();
            if (state != null && state.hasProperty(CampfireBlock.LIT)) {
                cir.setReturnValue(state.setValue(CampfireBlock.LIT, false));
            }
        }
    }

    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void modulation$igniteEntities(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isCampfiresIgniteEntitiesEnabled)) {
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
