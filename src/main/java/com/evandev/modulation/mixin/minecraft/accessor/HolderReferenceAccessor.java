package com.evandev.modulation.mixin.minecraft.accessor;

import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(Holder.Reference.class)
public interface HolderReferenceAccessor {
    @Accessor("tags")
    Set<?> getTags();
}
