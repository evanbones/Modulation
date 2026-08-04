package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.modules.blockgrid.BlockOffsets;
import com.evandev.modulation.modules.blockgrid.storage.ChunkOffsetHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @Inject(method = "write", at = @At("RETURN"))
    private static void modulation$saveBlockOffsets(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        if (chunk instanceof ChunkOffsetHolder holder) {
            BlockOffsets offsets = holder.modulation$getBlockOffsets();
            if (offsets != null && !offsets.isEmpty()) {
                BlockOffsets.CODEC.encodeStart(NbtOps.INSTANCE, offsets)
                        .result()
                        .ifPresent(offsetTag -> tag.put("modulation:block_offsets", offsetTag));
            }
        }
    }

    @Inject(method = "read", at = @At("RETURN"))
    private static void modulation$loadBlockOffsets(ServerLevel level, PoiManager poiManager, RegionStorageInfo regionStorageInfo, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir) {
        if (tag.contains("modulation:block_offsets")) {
            BlockOffsets.CODEC.parse(NbtOps.INSTANCE, tag.get("modulation:block_offsets"))
                    .result()
                    .ifPresent(offsets -> {
                        ChunkAccess chunk = cir.getReturnValue();
                        if (chunk instanceof ChunkOffsetHolder holder) {
                            holder.modulation$setBlockOffsets(offsets);
                        }
                    });
        }
    }
}
