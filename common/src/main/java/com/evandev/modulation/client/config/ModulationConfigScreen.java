package com.evandev.modulation.client.config;

import com.evandev.modulation.Constants;
import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.api.tweaks.*;
import com.evandev.modulation.config.ModConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

import java.util.*;

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
                    .controller(TickBoxControllerBuilder::create), t, tooltipKey).build();
        });

        OPTIONS.put(IntTweak.class, (t, title, tooltipKey) -> {
            IntTweak tweak = (IntTweak) t;
            return applyTooltip(Option.<Integer>createBuilder()
                    .name(title)
                    .binding(tweak.getDefaultValue(), tweak::getValue, value -> {
                        tweak.setValue(value);
                        tweak.onApply();
                    })
                    .controller(IntegerFieldControllerBuilder::create), t, tooltipKey).build();
        });

        OPTIONS.put(DoubleTweak.class, (t, title, tooltipKey) -> {
            DoubleTweak tweak = (DoubleTweak) t;
            return applyTooltip(Option.<Double>createBuilder()
                    .name(title)
                    .binding(tweak.getDefaultValue(), tweak::getValue, value -> {
                        tweak.setValue(value);
                        tweak.onApply();
                    })
                    .controller(DoubleFieldControllerBuilder::create), t, tooltipKey).build();
        });

        OPTIONS.put(StringTweak.class, (t, title, tooltipKey) -> {
            StringTweak tweak = (StringTweak) t;
            return applyTooltip(Option.<String>createBuilder()
                    .name(title)
                    .binding(tweak.getDefaultValue(), tweak::getValue, value -> {
                        tweak.setValue(value);
                        tweak.onApply();
                    })
                    .controller(StringControllerBuilder::create), t, tooltipKey).build();
        });
    }

    public static Screen createScreen(Screen parent) {
        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("config.modulation.title"))
                .save(ModConfig::save);

        for (IModule module : ModuleManager.getModules()) {
            ConfigCategory.Builder category = ConfigCategory.createBuilder()
                    .name(Component.translatable("config.modulation.module." + module.getId()));
            Map<String, OptionGroup.Builder> groups = new LinkedHashMap<>();

            for (AbstractTweak<?> tweak : module.getTweaks()) {
                String titleKey = "config.modulation.tweak." + module.getId() + "." + tweak.getId();
                String tooltipKey = titleKey + ".tooltip";
                Component title = Component.translatableWithFallback(titleKey, humanize(tweak.getId()));

                if (tweak instanceof StringListTweak listTweak) {
                    addOption(category, groups, module, tweak, buildListOption(listTweak, title, tooltipKey));
                    continue;
                }

                TweakOption factory = findFactory(tweak.getClass());
                if (factory == null) {
                    Constants.LOG.warn("No YACL controller registered for tweak type: {}", tweak.getClass());
                    continue;
                }

                addOption(category, groups, module, tweak, factory.build(tweak, title, tooltipKey));
            }

            for (OptionGroup.Builder group : groups.values()) {
                category.group(group.build());
            }

            builder.category(category.build());
        }

        return builder.build().generateScreen(parent);
    }

    private static void addOption(ConfigCategory.Builder category, Map<String, OptionGroup.Builder> groups, IModule module, AbstractTweak<?> tweak, Option<?> option) {
        if (tweak.getGroup() == null) {
            category.option(option);
            return;
        }
        groups.computeIfAbsent(tweak.getGroup(), id -> {
            String nameKey = "config.modulation.group." + module.getId() + "." + id;
            String descriptionKey = nameKey + ".tooltip";
            OptionGroup.Builder group = OptionGroup.createBuilder()
                    .name(Component.translatableWithFallback(nameKey, humanize(id)));
            if (Language.getInstance().has(descriptionKey)) {
                group.description(OptionDescription.of(Component.translatable(descriptionKey)));
            }
            return group;
        }).option(option);
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

    private static Option.Builder<?> applyTooltip(Option.Builder<?> builder, AbstractTweak<?> tweak, String tooltipKey) {
        List<Component> lines = new ArrayList<>();
        if (Language.getInstance().has(tooltipKey)) {
            lines.add(Component.translatable(tooltipKey));
        }

        String blockingMod = tweak.getBlockingMod();
        if (blockingMod != null) {
            builder.available(false);
            if (!lines.isEmpty()) {
                lines.add(Component.empty());
            }
            Component modName = Component.translatableWithFallback("config.modulation.mod." + blockingMod, blockingMod);
            lines.add(Component.translatable("config.modulation.unavailable", modName));
        }

        if (!lines.isEmpty()) {
            builder.description(OptionDescription.of(lines.toArray(new Component[0])));
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
