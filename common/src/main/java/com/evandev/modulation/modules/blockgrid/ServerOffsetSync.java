package com.evandev.modulation.modules.blockgrid;

import com.evandev.modulation.modules.blockgrid.storage.ChunkOffsetHolder;
import com.evandev.modulation.networking.ChunkOffsetsPayload;
import com.evandev.modulation.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public final class ServerOffsetSync {

    private ServerOffsetSync() {
    }

    public static void store(ServerLevel level, BlockPos pos, Vec3 offset) {
        LevelChunk chunk = level.getChunkAt(pos);
        if (!(chunk instanceof ChunkOffsetHolder holder)) {
            return;
        }
        ChunkOffsetMap offsets = holder.modulation$getBlockOffsets();
        if (offsets == null) {
            offsets = new ChunkOffsetMap();
            holder.modulation$setBlockOffsets(offsets);
        }
        if (offset == null || offset == Vec3.ZERO) {
            offsets.remove(pos);
        } else {
            offsets.put(pos, offset);
        }
        markDirtyAndBroadcast(level, chunk, offsets);
    }

    public static void discard(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        if (!(chunk instanceof ChunkOffsetHolder holder)) {
            return;
        }
        ChunkOffsetMap offsets = holder.modulation$getBlockOffsets();
        if (offsets == null || offsets.get(pos) == null) {
            return;
        }
        offsets.remove(pos);
        markDirtyAndBroadcast(level, chunk, offsets);
    }

    public static void onBlockRemoved(ServerLevel level, BlockPos pos) {
        discard(level, pos);
    }

    public static void onChunkWatched(ServerPlayer player, LevelChunk chunk) {
        if (!(chunk instanceof ChunkOffsetHolder holder)) {
            return;
        }
        ChunkOffsetMap offsets = holder.modulation$getBlockOffsets();
        if (offsets == null || offsets.isEmpty()) {
            return;
        }
        Services.PLATFORM.sendBlockOffsetsToPlayer(player,
                new ChunkOffsetsPayload(chunk.getPos().toLong(), offsets.entries()));
    }

    private static void markDirtyAndBroadcast(ServerLevel level, LevelChunk chunk, ChunkOffsetMap offsets) {
        chunk.setUnsaved(true);
        ChunkOffsetsPayload payload = new ChunkOffsetsPayload(chunk.getPos().toLong(), offsets.entries());
        Services.PLATFORM.sendBlockOffsetsToTracking(level, chunk.getPos(), payload);
    }
}
