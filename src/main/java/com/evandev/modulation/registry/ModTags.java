package com.evandev.modulation.registry;

import com.evandev.modulation.Constants;
import com.evandev.modulation.mixin.minecraft.accessor.HolderReferenceAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ModTags {
    public static final TagKey<Block> PASSABLE_LEAVES = blockTag("passable_leaves");
    public static final TagKey<Item> RESPAWN_ANCHOR_FUEL = itemTag("respawn_anchor_fuel");

    public static boolean isSafe(BlockState state, TagKey<Block> tag) {
        return ((HolderReferenceAccessor) state.getBlock().builtInRegistryHolder()).getTags() != null && state.is(tag);
    }

    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}
