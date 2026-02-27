package com.evandev.modulation;

import com.evandev.modulation.client.ClientConfigSetup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class Modulation {
    public Modulation() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register(ModLoadingContext.get().getActiveContainer());
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CommonClass::init);
    }
}