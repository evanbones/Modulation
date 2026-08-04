package com.evandev.modulation.networking;

import com.evandev.modulation.Constants;
import com.evandev.modulation.modules.blockgrid.ChunkOffsetMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ChunkOffsetsPayload(long chunk, List<ChunkOffsetMap.Entry> entries) implements CustomPacketPayload {
    public static final Type<ChunkOffsetsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "block_offsets"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChunkOffsetsPayload> CODEC =
            StreamCodec.of(ChunkOffsetsPayload::write, ChunkOffsetsPayload::read);

    private static void write(RegistryFriendlyByteBuf buf, ChunkOffsetsPayload payload) {
        buf.writeLong(payload.chunk());
        buf.writeVarInt(payload.entries().size());
        for (ChunkOffsetMap.Entry entry : payload.entries()) {
            buf.writeLong(entry.packedPos());
            buf.writeDouble(entry.offset().x);
            buf.writeDouble(entry.offset().y);
            buf.writeDouble(entry.offset().z);
        }
    }

    private static ChunkOffsetsPayload read(RegistryFriendlyByteBuf buf) {
        long chunk = buf.readLong();
        int count = buf.readVarInt();
        List<ChunkOffsetMap.Entry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            long packedPos = buf.readLong();
            entries.add(new ChunkOffsetMap.Entry(packedPos, new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())));
        }
        return new ChunkOffsetsPayload(chunk, entries);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
