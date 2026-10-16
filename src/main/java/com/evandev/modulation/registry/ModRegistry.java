package com.evandev.modulation.registry;

import com.evandev.modulation.Constants;
import com.evandev.modulation.blocks.CastPostBlock;
import com.evandev.modulation.items.ChainStaffItem;
import com.evandev.modulation.items.ZiplineStaffItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
//? if >=26.1 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.component.UseEffects;
*///?}

public class ModRegistry {
    public static Block CAST_POST;
    public static Item CAST_POST_ITEM;
    public static Item CHAIN_STAFF;
    public static Item ZIPLINE_STAFF;

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    public static Block createCastPost() {
        return new CastPostBlock(blockProperties("cast_post").mapColor(MapColor.METAL).strength(5.0f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());
    }

    public static Item createCastPostItem() {
        //? if >=26.1 {
        /*return new BlockItem(CAST_POST, itemProperties("cast_post").useBlockDescriptionPrefix());
        *///?} else {
        return new BlockItem(CAST_POST, itemProperties("cast_post"));
        //?}
    }

    public static Item createChainStaff() {
        //? if >=26.1 {
        /*return new ChainStaffItem(itemProperties("chain_staff").component(DataComponents.USE_EFFECTS, new UseEffects(true, true, 1.0F)));
        *///?} else {
        return new ChainStaffItem(itemProperties("chain_staff"));
        //?}
    }

    public static Item createZiplineStaff() {
        return new ZiplineStaffItem(itemProperties("zipline_staff"));
    }

    private static BlockBehaviour.Properties blockProperties(String name) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of();
        //? if >=26.1
        //properties.setId(ResourceKey.create(Registries.BLOCK, id(name)));
        return properties;
    }

    private static Item.Properties itemProperties(String name) {
        Item.Properties properties = new Item.Properties();
        //? if >=26.1
        //properties.setId(ResourceKey.create(Registries.ITEM, id(name)));
        return properties;
    }
}
