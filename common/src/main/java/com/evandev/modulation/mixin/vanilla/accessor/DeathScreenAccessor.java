package com.evandev.modulation.mixin.vanilla.accessor;

import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DeathScreen.class)
public interface DeathScreenAccessor {
    @Invoker("getClickedComponentStyleAt")
    Style modulation$getClickedComponentStyleAt(int x);
}
