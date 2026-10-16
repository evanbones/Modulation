package com.evandev.modulation.items;

import com.evandev.modulation.modules.reconnectible_chains.PostPlacementManager;
import com.evandev.modulation.modules.reconnectible_chains.ReconnectibleChainsModule;
import com.evandev.modulation.registry.ModRegistry;
import com.evandev.modulation.util.PlayerMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
//? if >=26.1 {
/*import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
*///?} else {
import net.minecraft.world.InteractionResultHolder;

import java.util.List;
//?}

//? if >=26.1 {
/*public class ChainStaffItem extends Item {
    public ChainStaffItem(Item.Properties properties) {
        super(properties.pickaxe(ToolMaterial.IRON, 4.0F, -2.8F).durability(2031));
    }
*///?} else {
public class ChainStaffItem extends PickaxeItem {
    public ChainStaffItem(Item.Properties properties) {
        super(Tiers.IRON, properties
                .durability(2031)
                .attributes(PickaxeItem.createAttributes(Tiers.IRON, 4.0F, -2.8F)));
    }
//?}

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    //? if >=26.1 {
    /*public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
    *///?} else {
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
    //?}
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                ItemStack newStack = new ItemStack(ModRegistry.ZIPLINE_STAFF);

                newStack.applyComponents(stack.getComponentsPatch());
                newStack.setDamageValue(stack.getDamageValue());

                player.setItemInHand(hand, newStack);
                level.playSound(null, player.blockPosition(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            //? if >=26.1 {
            /*return InteractionResult.SUCCESS;
            *///?} else {
            return InteractionResultHolder.success(player.getItemInHand(hand));
            //?}
        }

        player.startUsingItem(hand);
        //? if >=26.1 {
        /*return InteractionResult.CONSUME;
        *///?} else {
        return InteractionResultHolder.consume(stack);
        //?}
    }

    @Override
    //? if >=26.1 {
    /*public boolean releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        return this.releaseChainStaff(stack, level, entityLiving, timeLeft);
    }
    *///?} else {
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        this.releaseChainStaff(stack, level, entityLiving, timeLeft);
    }
    //?}

    private boolean releaseChainStaff(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) return false;

        if (!ReconnectibleChainsModule.ENABLED.on()) return false;

        int duration = this.getUseDuration(stack, player) - timeLeft;
        if (duration < ReconnectibleChainsModule.CHARGE_UP_TICKS.get()) return false;

        boolean isSecondPost = PostPlacementManager.INSTANCE.hasFirstPost(player.getUUID());
        boolean needsChains = ReconnectibleChainsModule.CONSUME_CHAINS.on() && !player.isCreative() && isSecondPost;

        int chainSlot = -1;
        if (needsChains) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                if (player.getInventory().getItem(i).is(PostPlacementManager.CHAIN_ITEM)) {
                    chainSlot = i;
                    break;
                }
            }
            if (chainSlot == -1) {
                if (level.isClientSide()) {
                    PlayerMessages.actionBar(player, Component.translatable("message.modulation.no_chains").withStyle(ChatFormatting.RED));
                }
                return false;
            }
        }

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

            boolean success = true;
            if (!level.isClientSide()) {
                success = ReconnectibleChainsModule.handlePostPlacement((ServerPlayer) player, placePos, clickedFace);
            }

            if (success) {
                if (!level.isClientSide()) {
                    if (ReconnectibleChainsModule.CONSUME_DURABILITY.on() && !player.isCreative()) {
                        EquipmentSlot slot = player.getItemInHand(InteractionHand.MAIN_HAND) == stack ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                        stack.hurtAndBreak(1, player, slot);
                    }
                }
                level.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                //? if >=26.1 {
                /*player.getCooldowns().addCooldown(stack, 20);
                *///?} else {
                player.getCooldowns().addCooldown(this, 20);
                //?}
            }
            return success;
        }
        return false;
    }

    @Override
    //? if >=26.1 {
    /*public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.accept(Component.translatable("tooltip.modulation.placement_mode").withStyle(ChatFormatting.GOLD));
        tooltipComponents.accept(Component.translatable("tooltip.modulation.switch_zipline_mode").withStyle(ChatFormatting.GRAY));
    }
    *///?} else {
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.modulation.placement_mode").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("tooltip.modulation.switch_zipline_mode").withStyle(ChatFormatting.GRAY));
    }
    //?}
}
