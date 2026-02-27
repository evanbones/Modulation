package com.evandev.modulation.client.integration;

import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.config.DynamicModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigIntegration {

    public static Screen createScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.modulation.title"));

        builder.setSavingRunnable(DynamicModConfig::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        for (IModule module : ModuleManager.getModules()) {
            ConfigCategory category = builder.getOrCreateCategory(
                    Component.translatable("config.modulation.module." + module.getId())
            );

            for (AbstractTweak<?> tweak : module.getTweaks()) {
                String translationKey = "config.modulation.tweak." + module.getId() + "." + tweak.getId();

                if (tweak instanceof BooleanTweak bt) {
                    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable(translationKey), bt.getValue())
                            .setDefaultValue(bt.getDefaultValue())
                            .setTooltip(Component.translatable(translationKey + ".tooltip"))
                            .setSaveConsumer(newValue -> {
                                bt.setValue(newValue);
                                bt.onApply();
                            })
                            .build());
                }
                // TODO: Implement UI building for IntTweak sliders, etc...
            }
        }
        return builder.build();
    }
}