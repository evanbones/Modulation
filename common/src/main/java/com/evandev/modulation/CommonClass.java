package com.evandev.modulation;

import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.config.DynamicModConfig;
import com.evandev.modulation.modules.reconnectible_chains.ReconnectibleChainsModule;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public class CommonClass {
    private static MinecraftServer currentServer;

    public static MinecraftServer getServer() {
        return currentServer;
    }

    public static void setServer(MinecraftServer server) {
        currentServer = server;
    }

    public static void init() {
        // Register Modules
        ModuleManager.loadModules();

        // Load Config
        DynamicModConfig.load();
    }

    public static void onServerTick() {
        ReconnectibleChainsModule module = ModuleManager.getModule("reconnectible_chains", ReconnectibleChainsModule.class);
        if (module != null) {
            module.onServerTick();
        }
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (IModule module : ModuleManager.getModules()) {
            module.registerCommands(dispatcher);
        }
    }
}