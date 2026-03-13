package com.evandev.modulation.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.integration.ClothConfigIntegration;
import com.evandev.modulation.modules.MusicModule;
import com.evandev.modulation.platform.Services;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

public class ModulationClient {
    public static void register(ModContainer container) {
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            container.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> ClothConfigIntegration.createScreen(parent));
        }
        NeoForge.EVENT_BUS.addListener(ModulationClient::onClientTick);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            MusicModule module = (MusicModule) ModuleManager.getModule("music");
            if (module != null && module.shouldLoad()) {
                module.onClientTick(mc);
            }
        }
    }
}