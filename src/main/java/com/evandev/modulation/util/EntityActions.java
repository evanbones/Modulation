package com.evandev.modulation.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//? if >=26.3 {
/*import net.minecraft.util.Prediction;
import net.minecraft.world.item.component.SwingAnimation;
*///?}

public final class EntityActions {

    private EntityActions() {
    }

    public static void drop(Player player, ItemStack stack, boolean thrownFromHand) {
        //? if >=26.3 {
        /*player.drop(stack, thrownFromHand, player.level().isClientSide() ? Prediction.PREDICTED : Prediction.SERVER_ONLY);
        *///?} else {
        player.drop(stack, thrownFromHand);
        //?}
    }

    public static void swing(LivingEntity entity, InteractionHand hand, boolean sendToSwingingEntity) {
        //? if >=26.3 {
        /*entity.swing(hand, SwingAnimation.DEFAULT, sendToSwingingEntity);
        *///?} else {
        entity.swing(hand, sendToSwingingEntity);
        //?}
    }
}
