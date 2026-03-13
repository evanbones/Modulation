package com.evandev.modulation.mixin.minecraft.accessor;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DisplayInvoker {

    @Invoker("setInterpolationDuration")
    void invokeSetInterpolationDuration(int duration);

    @Invoker("setInterpolationDelay")
    void invokeSetInterpolationDelay(int delay);

    @Invoker("setTransformation")
    void invokeSetTransformation(Transformation transformation);
}