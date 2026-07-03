package com.evandev.modulation.compat;

import com.evandev.modulation.client.config.ModulationConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModulationConfigScreen::createScreen;
    }
}