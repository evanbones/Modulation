package com.evandev.modulation.modules.blockgrid;

import com.evandev.modulation.modules.blockgrid.storage.ChunkOffsetHolder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class ClientOffsetCache {
    private static final Long2ObjectMap<Long2ObjectMap<Vec3>> BY_CHUNK = new Long2ObjectOpenHashMap<>();

    private ClientOffsetCache() {
    }

    public static void install() {
        SupportOffsets.setRenderLookup(ClientOffsetCache::offsetAt);
    }

    public static void clear() {
        BY_CHUNK.clear();
    }

    public static void refreshSection(Level level, BlockPos pos) {
        if (level instanceof ClientLevel client) {
            client.setSectionDirtyWithNeighbors(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
        }
    }

    public static void receive(long packedChunk, List<ChunkOffsetMap.Entry> entries) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        Long2ObjectMap<Vec3> cached = new Long2ObjectOpenHashMap<>(entries.size());
        for (ChunkOffsetMap.Entry entry : entries) {
            cached.put(entry.packedPos(), entry.offset());
        }
        BY_CHUNK.put(packedChunk, cached);

        ChunkPos chunkPos = new ChunkPos(packedChunk);
        LevelChunk chunk = level.getChunkSource().getChunk(chunkPos.x, chunkPos.z, false);
        if (chunk instanceof ChunkOffsetHolder holder) {
            holder.modulation$setBlockOffsets(ChunkOffsetMap.of(entries));
        }

        for (ChunkOffsetMap.Entry entry : entries) {
            BlockPos pos = BlockPos.of(entry.packedPos());
            level.setSectionDirtyWithNeighbors(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
        }
    }

    private static Vec3 offsetAt(BlockPos pos) {
        Long2ObjectMap<Vec3> cached = BY_CHUNK.get(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
        return cached == null ? null : cached.get(pos.asLong());
    }
}
