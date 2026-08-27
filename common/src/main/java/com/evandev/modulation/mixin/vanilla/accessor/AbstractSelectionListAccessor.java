package com.evandev.modulation.mixin.vanilla.accessor;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSelectionList.class)
public interface AbstractSelectionListAccessor {
    @Invoker("scrollbarVisible")
    boolean modulation$scrollbarVisible();

    @Invoker("getScrollbarPosition")
    int modulation$getScrollbarPosition();
}
