package com.evandev.modulation.registry;

import com.evandev.modulation.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> PASSABLE_LEAVES = blockTag("passable_leaves");

    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}
