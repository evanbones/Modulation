package com.evandev.modulation.fabric.platform;

//? if fabric {
/*import com.evandev.modulation.platform.services.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
//? if <26.1 {
/^import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
^///?}

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    //? if <26.1 {
    /^@Override
    public void sendFiguraLoadPacket(ServerPlayer player, String skinName) {
        ServerPlayNetworking.send(player, new FiguraSyncPayload(skinName));
    }

    @Override
    public void sendFiguraClearPacket(ServerPlayer player) {
        ServerPlayNetworking.send(player, new FiguraClearPayload());
    }
    ^///?}
}
*///?}
