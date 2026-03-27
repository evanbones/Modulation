package com.evandev.modulation.client;

import com.evandev.modulation.client.integration.ClothConfigIntegration;
import com.evandev.modulation.platform.Services;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ModulationClient {
    public static void register(ModContainer container) {
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            container.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> ClothConfigIntegration.createScreen(parent));
        }
    }
}