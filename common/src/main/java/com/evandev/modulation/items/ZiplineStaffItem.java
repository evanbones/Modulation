package com.evandev.modulation.items;

import com.evandev.modulation.registry.ModRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZiplineStaffItem extends PickaxeItem {
    public ZiplineStaffItem() {
        super(Tiers.IRON, 4, -2.8F, new Item.Properties().durability(2031));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                ItemStack newStack = new ItemStack(ModRegistry.CHAIN_STAFF);
                if (stack.hasTag()) if (stack.getTag() != null) {
                    newStack.setTag(stack.getTag().copy());
                }
                newStack.setDamageValue(stack.getDamageValue());
                player.setItemInHand(hand, newStack);
                level.playSound(null, player.blockPosition(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Zipline Mode").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.literal("Shift + Right-Click to switch to Placement Mode").withStyle(ChatFormatting.GRAY));
    }
}