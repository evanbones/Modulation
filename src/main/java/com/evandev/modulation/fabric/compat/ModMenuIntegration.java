package com.evandev.modulation.fabric.compat;

//? if fabric {
/*import com.evandev.modulation.client.config.ModulationConfigScreen;
import com.evandev.modulation.platform.Services;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!Services.PLATFORM.isModLoaded("yet_another_config_lib_v3")) return parent -> null;
        return ModulationConfigScreen::createScreen;
    }
}
*///?}
