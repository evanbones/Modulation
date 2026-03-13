package com.evandev.modulation.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.compat.FiguraClientHandler;
import com.evandev.modulation.modules.MusicModule;
import com.evandev.modulation.modules.music.MusicClientLogic;
import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MusicModule module = (MusicModule) ModuleManager.getModule("music");
            if (module != null && module.shouldLoad()) {
                MusicClientLogic.getInstance().onClientTick(client);
            }
        });
    }
}