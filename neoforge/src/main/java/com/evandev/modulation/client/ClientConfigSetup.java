package com.evandev.modulation.client;

import com.evandev.modulation.client.config.ModulationConfigScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ClientConfigSetup {
    public static void register(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (c, parent) -> ModulationConfigScreen.createScreen(parent));
    }
}