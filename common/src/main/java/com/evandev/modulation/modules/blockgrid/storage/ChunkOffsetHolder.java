package com.evandev.modulation.modules.blockgrid.storage;

import com.evandev.modulation.modules.blockgrid.ChunkOffsetMap;

public interface ChunkOffsetHolder {
    ChunkOffsetMap modulation$getBlockOffsets();

    void modulation$setBlockOffsets(ChunkOffsetMap offsets);
}
