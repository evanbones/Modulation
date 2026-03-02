package com.evandev.modulation;

import com.evandev.modulation.platform.Services;
import com.evandev.modulation.registry.ModRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class Modulation implements ModInitializer {

    @Override
    public void onInitialize() {
        ModRegistry.init();

        if (Services.PLATFORM.isModLoaded("connectiblechains")) {
            Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Constants.MOD_ID, "cast_post"), ModRegistry.CAST_POST);
            Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Constants.MOD_ID, "cast_post"), ModRegistry.CAST_POST_ITEM);
            Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Constants.MOD_ID, "chain_staff"), ModRegistry.CHAIN_STAFF);
            Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Constants.MOD_ID, "zipline_staff"), ModRegistry.ZIPLINE_STAFF);
        }

        CommonClass.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CommonClass.registerCommands(dispatcher);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            CommonClass.onServerTick();
        });
    }
}