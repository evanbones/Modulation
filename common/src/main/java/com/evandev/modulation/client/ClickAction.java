package com.evandev.modulation.client;

import net.minecraft.world.inventory.ClickType;

public record ClickAction(int slotId, int buttonNum, ClickType clickType) {
}
