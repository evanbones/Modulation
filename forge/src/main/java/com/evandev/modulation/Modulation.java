package com.evandev.modulation;

import com.evandev.modulation.client.ModulationClient;
import com.evandev.modulation.platform.Services;
import com.evandev.modulation.registry.ModRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Constants.MOD_ID)
public class Modulation {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public Modulation() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModRegistry.init();

        if (Services.PLATFORM.isModLoaded("connectiblechains")) {
            BLOCKS.register("cast_post", () -> ModRegistry.CAST_POST);
            ITEMS.register("cast_post", () -> ModRegistry.CAST_POST_ITEM);
            ITEMS.register("chain_staff", () -> ModRegistry.CHAIN_STAFF);
        }

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        if (FMLEnvironment.dist.isClient()) {
            ModulationClient.register(ModLoadingContext.get().getActiveContainer());
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CommonClass::init);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommonClass.registerCommands(event.getDispatcher());
    }
}