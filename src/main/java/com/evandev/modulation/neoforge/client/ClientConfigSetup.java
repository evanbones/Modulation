package com.evandev.modulation.neoforge.client;

//? if neoforge {
import com.evandev.modulation.client.config.ModulationConfigScreen;
import com.evandev.modulation.platform.Services;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ClientConfigSetup {
    public static void register(ModContainer container) {
        if (!Services.PLATFORM.isModLoaded("yet_another_config_lib_v3")) return;
        container.registerExtensionPoint(IConfigScreenFactory.class, (c, parent) -> ModulationConfigScreen.createScreen(parent));
    }
}
//?}
