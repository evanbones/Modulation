package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.evandev.modulation.util.TargetHelper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void modulation$noChestWhenTargeted(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isNoChestWhenTargetedEnabled)) {
            if (TargetHelper.isPlayerTargeted(level, player)) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("message.modulation.no_chest_when_targeted"), true);
                }
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }
    }

    @IfModAbsent("debugify")
    @IfModAbsent("neoforge")
    @WrapMethod(method = "mirror")
    private BlockState modulation$fixMirroredDoubleChests(BlockState state, Mirror mirror, Operation<BlockState> original) {
        BlockState result = original.call(state, mirror);
        if (mirror != Mirror.NONE && ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixMirroredDoubleChestsEnabled)) {
            return result.setValue(ChestBlock.TYPE, result.getValue(ChestBlock.TYPE).getOpposite());
        }
        return result;
    }
}
