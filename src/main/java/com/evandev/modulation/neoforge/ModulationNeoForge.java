package com.evandev.modulation.neoforge;

//? if neoforge {
import com.evandev.modulation.CommonClass;
import com.evandev.modulation.Constants;
import com.evandev.modulation.items.api.OxidizableItemHelper;
import com.evandev.modulation.neoforge.client.ClientConfigSetup;
import com.evandev.modulation.neoforge.client.ModulationClientEvents;
import com.evandev.modulation.registry.ModMemoryTypes;
import com.evandev.modulation.registry.ModRegistry;
import com.evandev.modulation.registry.ModSensorTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
//? if <26.1 {
import com.evandev.modulation.client.compat.FiguraClientHandler;
import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
//?}

@Mod(Constants.MOD_ID)
public class ModulationNeoForge {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(BuiltInRegistries.MEMORY_MODULE_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(BuiltInRegistries.SENSOR_TYPE, Constants.MOD_ID);

    public ModulationNeoForge(IEventBus modEventBus, ModContainer modContainer) {

        BLOCKS.register("cast_post", () -> ModRegistry.CAST_POST = ModRegistry.createCastPost());
        ITEMS.register("cast_post", () -> ModRegistry.CAST_POST_ITEM = ModRegistry.createCastPostItem());
        ITEMS.register("chain_staff", () -> ModRegistry.CHAIN_STAFF = ModRegistry.createChainStaff());
        ITEMS.register("zipline_staff", () -> ModRegistry.ZIPLINE_STAFF = ModRegistry.createZiplineStaff());

        ModMemoryTypes.all().forEach((id, type) -> MEMORY_MODULE_TYPES.register(id, () -> type));
        ModSensorTypes.all().forEach((id, type) -> SENSOR_TYPES.register(id, () -> type));

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        MEMORY_MODULE_TYPES.register(modEventBus);
        SENSOR_TYPES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        //? if <26.1
        modEventBus.addListener(this::registerPayloads);

        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);

        //? if >=26.1 {
        /*if (FMLEnvironment.getDist().isClient()) {
        *///?} else {
        if (FMLEnvironment.dist.isClient()) {
        //?}
            ClientConfigSetup.register(modContainer);
            ModulationClientEvents.register(modEventBus);
        }
    }

    //? if <26.1 {
    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                FiguraSyncPayload.TYPE,
                FiguraSyncPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> FiguraClientHandler.loadSkin(payload.skinName()))
        );

        registrar.playToClient(
                FiguraClearPayload.TYPE,
                FiguraClearPayload.CODEC,
                (payload, context) -> context.enqueueWork(FiguraClientHandler::clearSkin)
        );
    }
    //?}

    private void onServerTick(ServerTickEvent.Post event) {
        CommonClass.onServerTick();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CommonClass.init();
            OxidizableItemHelper.populateCache(BuiltInRegistries.ITEM);
        });
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommonClass.registerCommands(event.getDispatcher());
    }
}
//?}
