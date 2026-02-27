package com.evandev.modulation.api;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public interface IModule {
    String getId();
    boolean shouldLoad();
    List<AbstractTweak<?>> getTweaks();
    void initialize();

    default void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
    }
}