package com.evandev.modulation.modules.vanillabackport;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface EntityLeafDrag {
    void modulation$applyLeafDrag(BlockState state, Level level, BlockPos pos);
}
