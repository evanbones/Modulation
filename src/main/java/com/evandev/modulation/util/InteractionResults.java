package com.evandev.modulation.util;

import net.minecraft.world.InteractionResult;

public final class InteractionResults {

    private InteractionResults() {
    }

    public static InteractionResult sidedSuccess(boolean clientSide) {
        //? if >=26.1 {
        /*return InteractionResult.SUCCESS;
        *///?} else {
        return InteractionResult.sidedSuccess(clientSide);
        //?}
    }
}
