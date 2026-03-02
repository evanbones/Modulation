package com.evandev.modulation;

import com.evandev.modulation.blocks.CastPostBlock;
import com.evandev.modulation.client.ModulationClient;
import com.evandev.modulation.items.ChainStaffItem;
import com.evandev.modulation.items.ZiplineStaffItem;
import com.evandev.modulation.platform.Services;
import com.evandev.modulation.registry.ModRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
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

        if (Services.PLATFORM.isModLoaded("connectiblechains")) {

            BLOCKS.register("cast_post", () -> {
                ModRegistry.CAST_POST = new CastPostBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());
                return ModRegistry.CAST_POST;
            });

            ITEMS.register("cast_post", () -> {
                ModRegistry.CAST_POST_ITEM = new BlockItem(ModRegistry.CAST_POST, new Item.Properties());
                return ModRegistry.CAST_POST_ITEM;
            });

            ITEMS.register("chain_staff", () -> {
                ModRegistry.CHAIN_STAFF = new ChainStaffItem();
                return ModRegistry.CHAIN_STAFF;
            });

            ITEMS.register("zipline_staff", () -> {
                ModRegistry.ZIPLINE_STAFF = new ZiplineStaffItem();
                return ModRegistry.ZIPLINE_STAFF;
            });
        }

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        if (FMLEnvironment.dist.isClient()) {
            ModulationClient.register(ModLoadingContext.get().getActiveContainer());
        }
    }

    private void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CommonClass.onServerTick();
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CommonClass::init);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommonClass.registerCommands(event.getDispatcher());
    }
}