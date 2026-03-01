package com.evandev.modulation.items;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.reconnectible_chains.ReconnectibleChainsModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class ChainStaffItem extends PickaxeItem {
    public ChainStaffItem() {
        super(Tiers.IRON, 4, -2.8F, new Item.Properties().durability(2031));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) return;

        ReconnectibleChainsModule module = (ReconnectibleChainsModule) ModuleManager.getModule("reconnectible_chains");
        if (module == null || !module.isEnabled()) return;

        int duration = this.getUseDuration(stack) - timeLeft;
        if (duration < module.getChargeUpTicks()) return;

        BlockHitResult hit = level.clip(new ClipContext(
                player.getEyePosition(),
                player.getEyePosition().add(player.getViewVector(1.0F).scale(500.0)),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos targetPos = hit.getBlockPos();
            Direction clickedFace = hit.getDirection();
            BlockPos placePos = targetPos.relative(clickedFace);

            if (!level.isClientSide) {
                module.handlePostPlacement((ServerPlayer) player, placePos, clickedFace);
            }
            player.getCooldowns().addCooldown(this, 20);
        }
    }
}