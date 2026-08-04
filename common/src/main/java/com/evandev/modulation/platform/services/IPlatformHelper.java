package com.evandev.modulation.platform.services;

import com.evandev.modulation.networking.BlockOffsetsPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.nio.file.Path;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Gets the configuration directory for the current platform.
     *
     * @return The path to the config directory.
     */
    Path getConfigDirectory();

    /**
     * Checks if the code is running on the physical client.
     *
     * @return True if on the client, false if on a dedicated server.
     */
    boolean isPhysicalClient();

    /**
     * Sends a packet to change the Figura avatar for the specified player.
     *
     * @param player The target player whose avatar should be changed.
     */
    void sendFiguraLoadPacket(ServerPlayer player, String skinName);

    /**
     * Sends a packet to clear the Figura avatar for the specified player.
     *
     * @param player The target player whose avatar should be cleared.
     */
    void sendFiguraClearPacket(ServerPlayer player);

    /**
     * Sends block offsets payload to tracking players.
     */
    void sendBlockOffsetsToTracking(ServerLevel level, ChunkPos pos, BlockOffsetsPayload payload);

    /**
     * Sends block offsets payload to a specific player.
     */
    void sendBlockOffsetsToPlayer(ServerPlayer player, BlockOffsetsPayload payload);
}

