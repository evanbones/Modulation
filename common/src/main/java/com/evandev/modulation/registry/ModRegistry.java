package com.evandev.modulation.registry;

import com.evandev.modulation.items.ChainStaffItem;
import com.evandev.modulation.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ModRegistry {
    public static Block CAST_POST;
    public static Item CAST_POST_ITEM;
    public static Item CHAIN_STAFF;

    public static void init() {
        if (Services.PLATFORM.isModLoaded("connectiblechains")) {

            CAST_POST = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()) {
                private static final VoxelShape SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

                @Override
                public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
                    return SHAPE;
                }
            };

            CAST_POST_ITEM = new BlockItem(CAST_POST, new Item.Properties());
            CHAIN_STAFF = new ChainStaffItem();
        }
    }
}