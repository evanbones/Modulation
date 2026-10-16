package com.evandev.modulation.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
//? if <26.1
import net.minecraft.world.entity.LivingEntity;

public final class HandSlots {

    private HandSlots() {
    }

    public static EquipmentSlot of(InteractionHand hand) {
        //? if >=26.1 {
        /*return hand.asEquipmentSlot();
        *///?} else {
        return LivingEntity.getSlotForHand(hand);
        //?}
    }
}
