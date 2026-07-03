package com.evandev.modulation.mixin.vanilla.accessor;

import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.BlockDisplay.class)
public interface BlockDisplayInvoker {
    @Invoker("setBlockState")
    void invokeSetBlockState(BlockState state);
}