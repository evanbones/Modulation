package com.evandev.modulation.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.integration.ClothConfigIntegration;
import com.evandev.modulation.modules.MusicModule;
import com.evandev.modulation.modules.music.MusicClientLogic;
import com.evandev.modulation.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModContainer;

public class ModulationClient {
    public static void register(ModContainer container) {
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> ClothConfigIntegration.createScreen(parent)));
        }
        MinecraftForge.EVENT_BUS.addListener(ModulationClient::onClientTick);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                MusicModule module = (MusicModule) ModuleManager.getModule("music");
                if (module != null && module.shouldLoad()) {
                    MusicClientLogic.getInstance().onClientTick(mc);
                }
            }
        }
    }
}