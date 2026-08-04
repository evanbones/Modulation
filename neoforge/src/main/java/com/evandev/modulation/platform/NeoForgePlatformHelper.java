package com.evandev.modulation.platform;

import com.evandev.modulation.networking.BlockOffsetsPayload;
import com.evandev.modulation.networking.FiguraClearPayload;
import com.evandev.modulation.networking.FiguraSyncPayload;
import com.evandev.modulation.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

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
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public void sendFiguraLoadPacket(ServerPlayer player, String skinName) {
        PacketDistributor.sendToPlayer(player, new FiguraSyncPayload(skinName));
    }

    @Override
    public void sendFiguraClearPacket(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new FiguraClearPayload());
    }

    @Override
    public void sendBlockOffsetsToTracking(ServerLevel level, ChunkPos pos, BlockOffsetsPayload payload) {
        PacketDistributor.sendToPlayersTrackingChunk(level, pos, payload);
    }

    @Override
    public void sendBlockOffsetsToPlayer(ServerPlayer player, BlockOffsetsPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}