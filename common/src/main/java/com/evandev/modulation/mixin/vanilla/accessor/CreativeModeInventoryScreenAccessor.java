package com.evandev.modulation.mixin.vanilla.accessor;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {
    @Invoker("checkTabClicked")
    boolean modulation$checkTabClicked(CreativeModeTab tab, double relativeMouseX, double relativeMouseY);
}
