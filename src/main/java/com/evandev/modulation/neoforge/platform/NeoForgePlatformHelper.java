package com.evandev.modulation.neoforge.platform;

//? if neoforge {
import com.evandev.modulation.platform.services.IPlatformHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
//? if >=26.1 {
/*import net.neoforged.fml.loading.FMLEnvironment;
*///?} else {
import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;
//?}

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        //? if >=26.1 {
        /*return !FMLEnvironment.isProduction();
        *///?} else {
        return !FMLLoader.isProduction();
        //?}
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isPhysicalClient() {
        //? if >=26.1 {
        /*return FMLEnvironment.getDist() == Dist.CLIENT;
        *///?} else {
        return FMLLoader.getDist() == Dist.CLIENT;
        //?}
    }

    //? if <26.1 {
    @Override
    public void sendFiguraLoadPacket(ServerPlayer player, String skinName) {
        PacketDistributor.sendToPlayer(player, new FiguraSyncPayload(skinName));
    }

    @Override
    public void sendFiguraClearPacket(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new FiguraClearPayload());
    }
    //?}
}
//?}
