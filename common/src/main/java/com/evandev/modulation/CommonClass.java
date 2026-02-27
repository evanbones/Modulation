package com.evandev.modulation;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.config.DynamicModConfig;
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
        // ModuleManager.register(new VanillaModule());

        // Load Config
        DynamicModConfig.load();
    }
}