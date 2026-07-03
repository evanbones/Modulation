package com.evandev.modulation.mixin.vanilla.accessor;

import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapColor.class)
public interface MapColorAccessor {
    @Mutable
    @Accessor("col")
    void modulation$setCol(int col);

    @Accessor("col")
    int modulation$getCol();
}