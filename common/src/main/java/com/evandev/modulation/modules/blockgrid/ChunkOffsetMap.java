package com.evandev.modulation.modules.blockgrid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ChunkOffsetMap {
    public static final Codec<ChunkOffsetMap> CODEC = Entry.CODEC.listOf()
            .xmap(ChunkOffsetMap::of, ChunkOffsetMap::entries);

    private final Long2ObjectMap<Vec3> byPosition = new Long2ObjectOpenHashMap<>();

    public static ChunkOffsetMap of(List<Entry> entries) {
        ChunkOffsetMap map = new ChunkOffsetMap();
        for (Entry entry : entries) {
            map.byPosition.put(entry.packedPos(), entry.offset());
        }
        return map;
    }

    public List<Entry> entries() {
        List<Entry> entries = new ArrayList<>(this.byPosition.size());
        for (Long2ObjectMap.Entry<Vec3> entry : this.byPosition.long2ObjectEntrySet()) {
            entries.add(new Entry(entry.getLongKey(), entry.getValue()));
        }
        entries.sort(Comparator.comparingLong(Entry::packedPos));
        return entries;
    }

    public Vec3 get(BlockPos pos) {
        return this.byPosition.get(pos.asLong());
    }

    public void put(BlockPos pos, Vec3 offset) {
        this.byPosition.put(pos.asLong(), offset);
    }

    public void remove(BlockPos pos) {
        this.byPosition.remove(pos.asLong());
    }

    public boolean isEmpty() {
        return this.byPosition.isEmpty();
    }

    public record Entry(long packedPos, Vec3 offset) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.LONG.fieldOf("pos").forGetter(Entry::packedPos),
                        Vec3.CODEC.fieldOf("offset").forGetter(Entry::offset)
                ).apply(instance, Entry::new)
        );
    }
}
