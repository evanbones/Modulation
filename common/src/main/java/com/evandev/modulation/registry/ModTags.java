package com.evandev.modulation.registry;

import com.evandev.modulation.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> SITS_ON_SLABS = blockTag("sits_on_slabs");
    public static final TagKey<Block> MOUNTS_ON_FACING = blockTag("mounts_on_facing");

    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}
