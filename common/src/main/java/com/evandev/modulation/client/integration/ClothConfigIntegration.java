package com.evandev.modulation.client.integration;

import com.evandev.modulation.Constants;
import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.api.tweaks.*;
import com.evandev.modulation.config.DynamicModConfig;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class ClothConfigIntegration {

    private static final Map<Class<?>, TweakBuilder> BUILDERS = new HashMap<>();

    static {
        BUILDERS.put(BooleanTweak.class, (t, k, b) -> {
            BooleanTweak bt = (BooleanTweak) t;
            return b.startBooleanToggle(Component.translatable(k), bt.getValue())
                    .setDefaultValue(bt.getDefaultValue())
                    .setTooltip(Component.translatable(k + ".tooltip"))
                    .setSaveConsumer(v -> {
                        bt.setValue(v);
                        bt.onApply();
                    }).build();
        });

        BUILDERS.put(IntTweak.class, (t, k, b) -> {
            IntTweak it = (IntTweak) t;
            return b.startIntField(Component.translatable(k), it.getValue())
                    .setDefaultValue(it.getDefaultValue())
                    .setTooltip(Component.translatable(k + ".tooltip"))
                    .setSaveConsumer(v -> {
                        it.setValue(v);
                        it.onApply();
                    }).build();
        });

        BUILDERS.put(DoubleTweak.class, (t, k, b) -> {
            DoubleTweak dt = (DoubleTweak) t;
            return b.startDoubleField(Component.translatable(k), dt.getValue())
                    .setDefaultValue(dt.getDefaultValue())
                    .setTooltip(Component.translatable(k + ".tooltip"))
                    .setSaveConsumer(v -> {
                        dt.setValue(v);
                        dt.onApply();
                    }).build();
        });

        BUILDERS.put(StringTweak.class, (t, k, b) -> {
            StringTweak st = (StringTweak) t;
            return b.startStrField(Component.translatable(k), st.getValue())
                    .setDefaultValue(st.getDefaultValue())
                    .setTooltip(Component.translatable(k + ".tooltip"))
                    .setSaveConsumer(v -> {
                        st.setValue(v);
                        st.onApply();
                    }).build();
        });

        BUILDERS.put(StringListTweak.class, (t, k, b) -> {
            StringListTweak slt = (StringListTweak) t;
            return b.startStrList(Component.translatable(k), slt.getValue())
                    .setDefaultValue(slt.getDefaultValue())
                    .setTooltip(Component.translatable(k + ".tooltip"))
                    .setSaveConsumer(v -> {
                        slt.setValue(v);
                        slt.onApply();
                    }).build();
        });
    }

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

                TweakBuilder tweakBuilder = null;
                for (Map.Entry<Class<?>, TweakBuilder> entry : BUILDERS.entrySet()) {
                    if (entry.getKey().isAssignableFrom(tweak.getClass())) {
                        tweakBuilder = entry.getValue();
                        break;
                    }
                }

                if (tweakBuilder != null) {
                    category.addEntry(tweakBuilder.build(tweak, translationKey, entryBuilder));
                } else {
                    Constants.LOG.warn("No Cloth Config builder found for tweak: {}", tweak.getId());
                }
            }
        }
        return builder.build();
    }

    @SuppressWarnings("rawtypes")
    private interface TweakBuilder {
        AbstractConfigListEntry<?> build(AbstractTweak tweak, String key, ConfigEntryBuilder builder);
    }
}