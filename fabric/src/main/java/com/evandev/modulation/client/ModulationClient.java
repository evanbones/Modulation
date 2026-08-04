package com.evandev.modulation.client;

import com.evandev.modulation.client.compat.FiguraClientHandler;
import com.evandev.modulation.modules.blockgrid.ClientOffsetCache;
import com.evandev.modulation.networking.ChunkOffsetsPayload;
import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ModulationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(FiguraSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> FiguraClientHandler.loadSkin(payload.skinName()));
        });

        ClientPlayNetworking.registerGlobalReceiver(FiguraClearPayload.TYPE, (payload, context) -> {
            context.client().execute(FiguraClientHandler::clearSkin);
        });

        ClientOffsetCache.install();
        ClientPlayNetworking.registerGlobalReceiver(ChunkOffsetsPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> ClientOffsetCache.receive(payload.chunk(), payload.entries()));
        });
    }
}