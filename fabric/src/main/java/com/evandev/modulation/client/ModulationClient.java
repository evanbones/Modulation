package com.evandev.modulation.client;

import com.evandev.modulation.client.compat.FiguraClientHandler;
import com.evandev.modulation.platform.FabricPlatformHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ModulationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(FabricPlatformHelper.FIGURA_SYNC, (client, handler, buf, responseSender) -> {
            String skin = buf.readUtf();
            client.execute(() -> FiguraClientHandler.loadSkin(skin));
        });

        ClientPlayNetworking.registerGlobalReceiver(FabricPlatformHelper.FIGURA_CLEAR, (client, handler, buf, responseSender) -> {
            client.execute(FiguraClientHandler::clearSkin);
        });
    }
}