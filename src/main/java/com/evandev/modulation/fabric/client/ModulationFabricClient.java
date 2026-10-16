package com.evandev.modulation.fabric.client;

//? if fabric {
/*import com.evandev.modulation.client.PanoramaScreenshot;
import com.evandev.modulation.modules.vanilla.smoothlight.LightTransitions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.1 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
//?} else {
/^import com.evandev.modulation.client.compat.FiguraClientHandler;
import com.evandev.modulation.fabric.client.compat.TrinketsSlotHighlight;
import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
^///?}

public class ModulationFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        //? if <26.1 {
        /^ClientPlayNetworking.registerGlobalReceiver(FiguraSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> FiguraClientHandler.loadSkin(payload.skinName()));
        });

        ClientPlayNetworking.registerGlobalReceiver(FiguraClearPayload.TYPE, (payload, context) -> {
            context.client().execute(FiguraClientHandler::clearSkin);
        });
        ^///?}

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                LightTransitions.tick(client.level);
            }
        });

        if (PanoramaScreenshot.isAvailable()) {
            //? if >=26.1 {
            KeyMapping.Category.register(PanoramaScreenshot.CATEGORY.id());
            KeyMappingHelper.registerKeyMapping(PanoramaScreenshot.key());
            //?} else {
            /^KeyBindingHelper.registerKeyBinding(PanoramaScreenshot.key());
            ^///?}
            ClientTickEvents.END_CLIENT_TICK.register(PanoramaScreenshot::onClientTick);
        }

        //? if <26.1 {
        /^if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            TrinketsSlotHighlight.init();
        }
        ^///?}
    }
}
*///?}
