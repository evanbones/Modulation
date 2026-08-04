package com.evandev.modulation;

import com.evandev.modulation.blocks.CastPostBlock;
import com.evandev.modulation.client.ClientConfigSetup;
import com.evandev.modulation.client.compat.FiguraClientHandler;
import com.evandev.modulation.items.ChainStaffItem;
import com.evandev.modulation.items.ZiplineStaffItem;
import com.evandev.modulation.items.api.OxidizableItemHelper;
import com.evandev.modulation.modules.blockgrid.ClientOffsetCache;
import com.evandev.modulation.modules.blockgrid.SupportOffsets;
import com.evandev.modulation.networking.ChunkOffsetsPayload;
import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import com.evandev.modulation.registry.ModRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Constants.MOD_ID)
public class Modulation {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);

    public Modulation(IEventBus modEventBus, ModContainer modContainer) {

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

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);

        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);

        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register(modContainer);
        }
    }

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

        registrar.playToClient(
                ChunkOffsetsPayload.TYPE,
                ChunkOffsetsPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientOffsetCache.receive(payload.chunk(), payload.entries()))
        );
    }

    private void onServerTick(ServerTickEvent.Post event) {
        CommonClass.onServerTick();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ClientOffsetCache.install();
            CommonClass.init();
            OxidizableItemHelper.populateCache(BuiltInRegistries.ITEM);
        });
    }


    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommonClass.registerCommands(event.getDispatcher());
    }

    private void onTagsUpdated(TagsUpdatedEvent event) {
        SupportOffsets.onTagsUpdated();
    }
}