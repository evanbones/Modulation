package com.evandev.modulation.fabric;

//? if fabric {
/*import com.evandev.modulation.CommonClass;
import com.evandev.modulation.items.api.OxidizableItemHelper;
import com.evandev.modulation.registry.ModMemoryTypes;
import com.evandev.modulation.registry.ModRegistry;
import com.evandev.modulation.registry.ModSensorTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
//? if <26.1 {
/^import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
^///?}

public class ModulationFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        //? if <26.1 {
        /^PayloadTypeRegistry.playS2C().register(FiguraSyncPayload.TYPE, FiguraSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FiguraClearPayload.TYPE, FiguraClearPayload.CODEC);
        ^///?}

        ModRegistry.CAST_POST = Registry.register(BuiltInRegistries.BLOCK, ModRegistry.id("cast_post"), ModRegistry.createCastPost());
        ModRegistry.CAST_POST_ITEM = Registry.register(BuiltInRegistries.ITEM, ModRegistry.id("cast_post"), ModRegistry.createCastPostItem());
        ModRegistry.CHAIN_STAFF = Registry.register(BuiltInRegistries.ITEM, ModRegistry.id("chain_staff"), ModRegistry.createChainStaff());
        ModRegistry.ZIPLINE_STAFF = Registry.register(BuiltInRegistries.ITEM, ModRegistry.id("zipline_staff"), ModRegistry.createZiplineStaff());

        ModMemoryTypes.all().forEach((id, type) ->
                Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, ModRegistry.id(id), type));
        ModSensorTypes.all().forEach((id, type) ->
                Registry.register(BuiltInRegistries.SENSOR_TYPE, ModRegistry.id(id), type));

        CommonClass.init();

        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            OxidizableItemHelper.populateCache(BuiltInRegistries.ITEM);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CommonClass.registerCommands(dispatcher);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            CommonClass.onServerTick();
        });
    }
}
*///?}
