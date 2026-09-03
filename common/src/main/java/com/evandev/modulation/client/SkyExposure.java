package com.evandev.modulation.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public final class SkyExposure {

    private static final int SAMPLE_RADIUS = 32;
    private static final int SAMPLE_STEP = 8;
    private static final int UPPER_SAMPLE_OFFSET = 8;

    private static long cachedPos = Long.MIN_VALUE;
    private static long cachedTime = Long.MIN_VALUE;
    private static boolean cachedEnclosed;

    private SkyExposure() {
    }

    public static float localSkyLight(Level level, BlockPos pos) {
        if (!level.dimensionType().hasSkyLight()) {
            return 0.0F;
        }
        return level.getBrightness(LightLayer.SKY, pos) / 15.0F;
    }

    public static boolean isEnclosed(ClientLevel level, BlockPos pos) {
        long posKey = pos.asLong();
        long time = level.getGameTime();
        if (posKey == cachedPos && time == cachedTime) {
            return cachedEnclosed;
        }

        cachedPos = posKey;
        cachedTime = time;
        cachedEnclosed = computeEnclosed(level, pos);
        return cachedEnclosed;
    }

    private static boolean computeEnclosed(ClientLevel level, BlockPos pos) {
        if (level.getBrightness(LightLayer.SKY, pos) > 0) {
            return false;
        }

        BlockPos.MutableBlockPos sample = new BlockPos.MutableBlockPos();

        for (int dx = -SAMPLE_RADIUS; dx <= SAMPLE_RADIUS; dx += SAMPLE_STEP) {
            for (int dz = -SAMPLE_RADIUS; dz <= SAMPLE_RADIUS; dz += SAMPLE_STEP) {
                int x = pos.getX() + dx;
                int z = pos.getZ() + dz;
                if (!level.hasChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z))) {
                    continue;
                }

                if (level.getBrightness(LightLayer.SKY, sample.set(x, pos.getY(), z)) > 0) {
                    return false;
                }

                if (level.getBrightness(LightLayer.SKY, sample.set(x, pos.getY() + UPPER_SAMPLE_OFFSET, z)) > 0) {
                    return false;
                }
            }
        }

        return true;
    }
}
