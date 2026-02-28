package com.evandev.modulation.mixin.figura;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;
import org.figuramc.figura.avatar.local.LocalAvatarFetcher;
import org.figuramc.figura.utils.FiguraClientCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

@Mixin(targets = "org.figuramc.figura.commands.LoadCommand")
public class LoadCommandMixin {

    /**
     * @author evandev
     * @reason Add skin autofill, remove greedy string to allow server command fallthrough.
     */
    @Overwrite(remap = false)
    public static LiteralArgumentBuilder<FiguraClientCommandSource> getCommand() {
        LiteralArgumentBuilder<FiguraClientCommandSource> load = LiteralArgumentBuilder.literal("load");

        SuggestionProvider<FiguraClientCommandSource> skinSuggestions = (context, builder) -> {
            List<String> availableSkins = new ArrayList<>();
            File avatarsDir = LocalAvatarFetcher.getLocalAvatarDirectory().toFile();

            if (avatarsDir.exists() && avatarsDir.isDirectory()) {
                File[] files = avatarsDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName();
                        if (name.startsWith(".")) continue;

                        boolean isValidAvatar = false;
                        String suggestionName = name;

                        if (file.isDirectory() && new File(file, "avatar.json").exists()) {
                            isValidAvatar = true;
                        } else if (file.isFile() && name.endsWith(".zip")) {
                            try (ZipFile zip = new ZipFile(file)) {
                                if (zip.getEntry("avatar.json") != null) {
                                    isValidAvatar = true;
                                    suggestionName = name.replace(".zip", "");
                                }
                            } catch (Exception ignored) {
                            }
                        }

                        if (isValidAvatar) {
                            if (suggestionName.contains(" ")) suggestionName = "\"" + suggestionName + "\"";
                            availableSkins.add(suggestionName);
                        }
                    }
                }
            }
            return SharedSuggestionProvider.suggest(availableSkins, builder);
        };

        java.util.function.Function<String, RequiredArgumentBuilder<FiguraClientCommandSource, String>> createSkinArg = (targetStr) -> {
            RequiredArgumentBuilder<FiguraClientCommandSource, String> skin = RequiredArgumentBuilder.argument("skin", StringArgumentType.greedyString());
            skin.suggests(skinSuggestions);
            skin.executes(context -> {
                String finalTarget = targetStr != null ? targetStr : StringArgumentType.getString(context, "target");
                String skinName = StringArgumentType.getString(context, "skin");

                if (finalTarget.contains(" ")) finalTarget = "\"" + finalTarget + "\"";
                com.evandev.modulation.client.ClientCommandHelper.forward("modulation_figura load " + finalTarget + " " + skinName);
                return 1;
            });
            return skin;
        };

        for (String selector : new String[]{"@a", "@p", "@r", "@s", "@e"}) {
            load.then(LiteralArgumentBuilder.<FiguraClientCommandSource>literal(selector)
                    .then(createSkinArg.apply(selector))
            );
        }

        RequiredArgumentBuilder<FiguraClientCommandSource, String> playerTarget = RequiredArgumentBuilder.argument("target", StringArgumentType.word());
        playerTarget.suggests((context, builder) -> SharedSuggestionProvider.suggest(context.getSource().getOnlinePlayerNames(), builder));
        playerTarget.then(createSkinArg.apply(null));

        load.then(playerTarget);

        return load;
    }
}