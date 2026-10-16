package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.evandev.modulation.util.InteractionResults;
import com.evandev.modulation.util.PlayerMessages;
import com.evandev.modulation.util.TargetHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
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
        if (VanillaGameplayModule.NO_CHEST_WHEN_TARGETED.on()) {
            if (TargetHelper.isPlayerTargeted(level, player)) {
                if (!level.isClientSide()) {
                    PlayerMessages.actionBar(player, Component.translatable("message.modulation.no_chest_when_targeted"));
                }
                cir.setReturnValue(InteractionResults.sidedSuccess(level.isClientSide()));
            }
        }
    }
}
