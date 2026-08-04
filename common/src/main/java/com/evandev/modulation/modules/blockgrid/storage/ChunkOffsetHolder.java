package com.evandev.modulation.modules.blockgrid.storage;

import com.evandev.modulation.modules.blockgrid.BlockOffsets;

public interface ChunkOffsetHolder {
    BlockOffsets modulation$getBlockOffsets();

    void modulation$setBlockOffsets(BlockOffsets offsets);
}
