package com.evandev.modulation.registry;

import com.evandev.modulation.blocks.CastPostBlock;
import com.evandev.modulation.items.ChainStaffItem;
import com.evandev.modulation.platform.Services;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModRegistry {
    public static Block CAST_POST;
    public static Item CAST_POST_ITEM;
    public static Item CHAIN_STAFF;

    public static void init() {
        if (Services.PLATFORM.isModLoaded("connectiblechains")) {
            CAST_POST = new CastPostBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());

            CAST_POST_ITEM = new BlockItem(CAST_POST, new Item.Properties());
            CHAIN_STAFF = new ChainStaffItem();
        }
    }
}