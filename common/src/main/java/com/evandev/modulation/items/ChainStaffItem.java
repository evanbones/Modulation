package com.evandev.modulation.items;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.reconnectible_chains.ReconnectibleChainsModule;
import com.evandev.modulation.registry.ModRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChainStaffItem extends PickaxeItem {
    public ChainStaffItem() {
        super(Tiers.IRON, new Item.Properties()
                .durability(2031)
                .attributes(PickaxeItem.createAttributes(Tiers.IRON, 4.0F, -2.8F)));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                ItemStack newStack = new ItemStack(ModRegistry.ZIPLINE_STAFF);

                newStack.applyComponents(stack.getComponentsPatch());
                newStack.setDamageValue(stack.getDamageValue());

                player.setItemInHand(hand, newStack);
                level.playSound(null, player.blockPosition(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) return;

        ReconnectibleChainsModule module = (ReconnectibleChainsModule) ModuleManager.getModule("reconnectible_chains");
        if (module == null || !module.isEnabled()) return;

        int duration = this.getUseDuration(stack, player) - timeLeft;
        if (duration < module.getChargeUpTicks()) return;

        BlockHitResult hit = level.clip(new ClipContext(
                player.getEyePosition(),
                player.getEyePosition().add(player.getViewVector(1.0F).scale(500.0)),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos targetPos = hit.getBlockPos();
            Direction clickedFace = hit.getDirection();

            BlockState hitState = level.getBlockState(targetPos);
            BlockPos placePos;

            if (hitState.canBeReplaced()) {
                placePos = targetPos;
                clickedFace = Direction.UP;
            } else {
                placePos = targetPos.relative(clickedFace);
            }

            if (!level.isClientSide) {
                module.handlePostPlacement((ServerPlayer) player, placePos, clickedFace);
            }
            level.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.getCooldowns().addCooldown(this, 20);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.modulation.placement_mode").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("tooltip.modulation.switch_zipline_mode").withStyle(ChatFormatting.GRAY));
    }
}