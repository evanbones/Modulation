package com.evandev.modulation.modules;

import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipFile;

public class FiguraModule implements IModule {

    private static final SuggestionProvider<CommandSourceStack> SKIN_SUGGESTIONS = (context, builder) -> {
        List<String> availableSkins = new ArrayList<>();
        Path gameDir = Services.PLATFORM.getConfigDirectory().getParent();

        if (gameDir != null) {
            File avatarsDir = gameDir.resolve("figura").resolve("avatars").toFile();

            if (avatarsDir.exists() && avatarsDir.isDirectory()) {
                File[] files = avatarsDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName();

                        if (name.startsWith(".")) continue;

                        boolean isValidAvatar = false;
                        String suggestionName = name;

                        if (file.isDirectory()) {
                            if (new File(file, "avatar.json").exists()) {
                                isValidAvatar = true;
                            }
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
                            if (suggestionName.contains(" ")) {
                                suggestionName = "\"" + suggestionName + "\"";
                            }
                            availableSkins.add(suggestionName);
                        }
                    }
                }
            }
        }

        return SharedSuggestionProvider.suggest(availableSkins, builder);
    };
    private final BooleanTweak enableTargetSelectors = new BooleanTweak("enable_target_selectors", true);

    @Override
    public String getId() {
        return "figura";
    }

    @Override
    public boolean shouldLoad() {
        return Services.PLATFORM.isModLoaded("figura");
    }

    @Override
    public List<AbstractTweak<?>> getTweaks() {
        return List.of(enableTargetSelectors);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!enableTargetSelectors.getValue()) return;

        dispatcher.register(Commands.literal("mofigura")
                .then(Commands.literal("load")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("skin", StringArgumentType.string())
                                        .suggests(SKIN_SUGGESTIONS)
                                        .executes(context -> {
                                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
                                            String skinName = StringArgumentType.getString(context, "skin");

                                            for (ServerPlayer player : players) {
                                                Services.PLATFORM.sendFiguraLoadPacket(player, skinName);
                                            }
                                            return players.size();
                                        })
                                )
                        )
                )
                .then(Commands.literal("clear")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> {
                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");

                                    for (ServerPlayer player : players) {
                                        Services.PLATFORM.sendFiguraClearPacket(player);
                                    }
                                    return players.size();
                                })
                        )
                )
        );
    }
}