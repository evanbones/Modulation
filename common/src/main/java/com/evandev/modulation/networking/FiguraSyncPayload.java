package com.evandev.modulation.networking;

import com.evandev.modulation.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record FiguraSyncPayload(String skinName) implements CustomPacketPayload {
    public static final Type<FiguraSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "figura_sync"));
    public static final StreamCodec<FriendlyByteBuf, FiguraSyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, FiguraSyncPayload::skinName,
            FiguraSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}