package com.evandev.modulation.client.compat;

import com.evandev.modulation.client.render.SlotHighlightRenderer;
import dev.emi.trinkets.TrinketSlot;

public final class TrinketsSlotHighlight {

    private TrinketsSlotHighlight() {
    }

    public static void init() {
        SlotHighlightRenderer.deferBackFor(slot -> slot instanceof TrinketSlot);
    }
}
