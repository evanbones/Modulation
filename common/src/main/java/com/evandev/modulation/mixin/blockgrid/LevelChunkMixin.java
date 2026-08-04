package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.modules.blockgrid.ChunkOffsetMap;
import com.evandev.modulation.modules.blockgrid.storage.ChunkOffsetHolder;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunk.class)
public class LevelChunkMixin implements ChunkOffsetHolder {
    @Unique
    private ChunkOffsetMap modulation$blockOffsets = new ChunkOffsetMap();

    @Override
    public ChunkOffsetMap modulation$getBlockOffsets() {
        return modulation$blockOffsets;
    }

    @Override
    public void modulation$setBlockOffsets(ChunkOffsetMap offsets) {
        this.modulation$blockOffsets = offsets != null ? offsets : new ChunkOffsetMap();
    }
}
