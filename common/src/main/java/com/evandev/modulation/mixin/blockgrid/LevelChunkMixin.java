package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.modules.blockgrid.BlockOffsets;
import com.evandev.modulation.modules.blockgrid.storage.ChunkOffsetHolder;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunk.class)
public class LevelChunkMixin implements ChunkOffsetHolder {
    @Unique
    private BlockOffsets modulation$blockOffsets = new BlockOffsets();

    @Override
    public BlockOffsets modulation$getBlockOffsets() {
        return modulation$blockOffsets;
    }

    @Override
    public void modulation$setBlockOffsets(BlockOffsets offsets) {
        this.modulation$blockOffsets = offsets != null ? offsets : new BlockOffsets();
    }
}
