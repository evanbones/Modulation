package com.evandev.modulation.mixin.emi.accessor;

import dev.emi.emi.screen.EmiScreenManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(EmiScreenManager.class)
public interface EmiScreenManagerAccessor {
    @Accessor("panels")
    static List<EmiScreenManager.SidebarPanel> modulation$getPanels() {
        throw new AssertionError();
    }
}
