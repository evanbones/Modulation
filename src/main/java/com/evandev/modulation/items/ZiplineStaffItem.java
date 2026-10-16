package com.evandev.modulation.items;

import com.evandev.modulation.registry.ModRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
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
/*public class ZiplineStaffItem extends Item {
    public ZiplineStaffItem(Item.Properties properties) {
        super(properties.pickaxe(ToolMaterial.IRON, 4.0F, -2.8F).durability(2031));
    }
*///?} else {
public class ZiplineStaffItem extends PickaxeItem {
    public ZiplineStaffItem(Item.Properties properties) {
        super(Tiers.IRON, properties
                .durability(2031)
                .attributes(PickaxeItem.createAttributes(Tiers.IRON, 4.0F, -2.8F)));
    }
//?}

    @Override
    //? if >=26.1 {
    /*public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
    *///?} else {
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
    //?}
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                ItemStack newStack = new ItemStack(ModRegistry.CHAIN_STAFF);

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
        return super.use(level, player, hand);
    }

    @Override
    //? if >=26.1 {
    /*public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.accept(Component.literal("Zipline Mode").withStyle(ChatFormatting.AQUA));
        tooltipComponents.accept(Component.literal("Shift + Right-Click to switch to Placement Mode").withStyle(ChatFormatting.GRAY));
    }
    *///?} else {
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Zipline Mode").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.literal("Shift + Right-Click to switch to Placement Mode").withStyle(ChatFormatting.GRAY));
    }
    //?}
}
