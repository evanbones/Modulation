package com.evandev.modulation.client.config;

import com.evandev.modulation.Constants;
import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.api.tweaks.*;
import com.evandev.modulation.config.DynamicModConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class ModulationConfigScreen {

    private static final Map<Class<?>, TweakOption> OPTIONS = new HashMap<>();

    static {
        OPTIONS.put(BooleanTweak.class, (t, title, tooltipKey) -> {
            BooleanTweak tweak = (BooleanTweak) t;
            return applyTooltip(Option.<Boolean>createBuilder()
                    .name(title)
                    .binding(tweak.getDefaultValue(), tweak::getValue, value -> {
                        tweak.setValue(value);
                        tweak.onApply();
                    })
                    .controller(TickBoxControllerBuilder::create), tooltipKey).build();
        });

        OPTIONS.put(IntTweak.class, (t, title, tooltipKey) -> {
            IntTweak tweak = (IntTweak) t;
            return applyTooltip(Option.<Integer>createBuilder()
                    .name(title)
                    .binding(tweak.getDefaultValue(), tweak::getValue, value -> {
                        tweak.setValue(value);
                        tweak.onApply();
                    })
                    .controller(IntegerFieldControllerBuilder::create), tooltipKey).build();
        });

        OPTIONS.put(DoubleTweak.class, (t, title, tooltipKey) -> {
            DoubleTweak tweak = (DoubleTweak) t;
            return applyTooltip(Option.<Double>createBuilder()
                    .name(title)
                    .binding(tweak.getDefaultValue(), tweak::getValue, value -> {
                        tweak.setValue(value);
                        tweak.onApply();
                    })
                    .controller(DoubleFieldControllerBuilder::create), tooltipKey).build();
        });

        OPTIONS.put(StringTweak.class, (t, title, tooltipKey) -> {
            StringTweak tweak = (StringTweak) t;
            return applyTooltip(Option.<String>createBuilder()
                    .name(title)
                    .binding(tweak.getDefaultValue(), tweak::getValue, value -> {
                        tweak.setValue(value);
                        tweak.onApply();
                    })
                    .controller(StringControllerBuilder::create), tooltipKey).build();
        });
    }

    public static Screen createScreen(Screen parent) {
        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("config.modulation.title"))
                .save(DynamicModConfig::save);

        for (IModule module : ModuleManager.getModules()) {
            ConfigCategory.Builder category = ConfigCategory.createBuilder()
                    .name(Component.translatable("config.modulation.module." + module.getId()));

            for (AbstractTweak<?> tweak : module.getTweaks()) {
                String titleKey = "config.modulation.tweak." + module.getId() + "." + tweak.getId();
                String tooltipKey = titleKey + ".tooltip";
                Component title = Component.translatableWithFallback(titleKey, humanize(tweak.getId()));

                if (tweak instanceof StringListTweak listTweak) {
                    category.option(buildListOption(listTweak, title, tooltipKey));
                    continue;
                }

                TweakOption factory = findFactory(tweak.getClass());
                if (factory == null) {
                    Constants.LOG.warn("No YACL controller registered for tweak type: {}", tweak.getClass());
                    continue;
                }

                category.option(factory.build(tweak, title, tooltipKey));
            }

            builder.category(category.build());
        }

        return builder.build().generateScreen(parent);
    }

    private static TweakOption findFactory(Class<?> tweakClass) {
        for (Class<?> c = tweakClass; c != null; c = c.getSuperclass()) {
            TweakOption factory = OPTIONS.get(c);
            if (factory != null) return factory;
        }
        return null;
    }

    private static ListOption<String> buildListOption(StringListTweak tweak, Component title, String tooltipKey) {
        ListOption.Builder<String> option = ListOption.<String>createBuilder()
                .name(title)
                .binding(tweak.getDefaultValue(), tweak::getValue, value -> {
                    tweak.setValue(value);
                    tweak.onApply();
                })
                .controller(StringControllerBuilder::create)
                .initial("");

        if (Language.getInstance().has(tooltipKey)) {
            option.description(OptionDescription.of(Component.translatable(tooltipKey)));
        }
        return option.build();
    }

    private static Option.Builder<?> applyTooltip(Option.Builder<?> builder, String tooltipKey) {
        if (Language.getInstance().has(tooltipKey)) {
            builder.description(OptionDescription.of(Component.translatable(tooltipKey)));
        }
        return builder;
    }

    private static String humanize(String id) {
        String[] parts = id.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (!result.isEmpty()) result.append(' ');
            result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return result.toString();
    }

    @FunctionalInterface
    private interface TweakOption {
        Option<?> build(AbstractTweak<?> tweak, Component title, String tooltipKey);
    }
}
