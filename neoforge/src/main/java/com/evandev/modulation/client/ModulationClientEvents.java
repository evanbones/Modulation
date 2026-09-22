package com.evandev.modulation.client;

import com.evandev.modulation.modules.vanilla.smoothlight.LightTransitions;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ModulationClientEvents {

    public static void register(IEventBus modEventBus) {
        if (PanoramaScreenshot.isAvailable()) {
            modEventBus.addListener(ModulationClientEvents::onRegisterKeyMappings);
        }
        NeoForge.EVENT_BUS.addListener(ModulationClientEvents::onClientTick);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(PanoramaScreenshot.key());
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (PanoramaScreenshot.isAvailable()) {
            PanoramaScreenshot.onClientTick(minecraft);
        }
        if (minecraft.level != null) {
            LightTransitions.tick(minecraft.level);
        }
    }
}
