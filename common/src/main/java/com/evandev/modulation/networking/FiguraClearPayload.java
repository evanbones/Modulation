package com.evandev.modulation.networking;

import com.evandev.modulation.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record FiguraClearPayload() implements CustomPacketPayload {
    public static final Type<FiguraClearPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "figura_clear"));
    public static final StreamCodec<FriendlyByteBuf, FiguraClearPayload> CODEC = StreamCodec.unit(new FiguraClearPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}