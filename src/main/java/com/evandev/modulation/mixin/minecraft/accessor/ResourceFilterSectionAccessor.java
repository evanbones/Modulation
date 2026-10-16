package com.evandev.modulation.mixin.minecraft.accessor;

//? if <26.1 {
import net.minecraft.server.packs.resources.ResourceFilterSection;
import net.minecraft.util.ResourceLocationPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ResourceFilterSection.class)
public interface ResourceFilterSectionAccessor {
    @Accessor("blockList")
    List<ResourceLocationPattern> getBlockList();
}
//?}
