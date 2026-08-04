package com.evandev.modulation.platform;

import com.evandev.modulation.networking.ChunkOffsetsPayload;
import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import com.evandev.modulation.platform.services.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

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

    @Override
    public void sendFiguraLoadPacket(ServerPlayer player, String skinName) {
        ServerPlayNetworking.send(player, new FiguraSyncPayload(skinName));
    }

    @Override
    public void sendFiguraClearPacket(ServerPlayer player) {
        ServerPlayNetworking.send(player, new FiguraClearPayload());
    }

    @Override
    public void sendBlockOffsetsToTracking(ServerLevel level, ChunkPos pos, ChunkOffsetsPayload payload) {
        PlayerLookup.tracking(level, pos).forEach(player -> ServerPlayNetworking.send(player, payload));
    }

    @Override
    public void sendBlockOffsetsToPlayer(ServerPlayer player, ChunkOffsetsPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}