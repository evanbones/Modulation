package com.evandev.modulation.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FogType;

import java.lang.ref.WeakReference;

public final class SkyExposure {

    private static final int SAMPLE_RADIUS = 48;
    private static final int SAMPLE_STEP = 8;
    private static final int UPPER_SAMPLE_OFFSET = 8;
    private static final int DEPTH_MARGIN = 8;
    private static final int FADE_TICKS = 20;

    private static WeakReference<ClientLevel> cachedLevel = new WeakReference<>(null);
    private static long cachedTime = Long.MIN_VALUE;
    private static float previousFactor;
    private static float factor;

    private SkyExposure() {
    }

    public static float localSkyLight(Level level, BlockPos pos) {
        if (!level.dimensionType().hasSkyLight()) {
            return 0.0F;
        }
        return level.getBrightness(LightLayer.SKY, pos) / 15.0F;
    }

    public static float skyHideFactor(Camera camera, float partialTick) {
        Entity entity = camera.getEntity();
        if (!(entity.level() instanceof ClientLevel clientLevel)) {
            return 0.0F;
        }
        return skyHideFactor(clientLevel, camera, partialTick);
    }

    public static float skyHideFactor(ClientLevel level, Camera camera, float partialTick) {
        if (level == null) {
            return 0.0F;
        }
        advance(level, camera);
        return Mth.lerp(Mth.clamp(partialTick, 0.0F, 1.0F), previousFactor, factor);
    }

    private static void advance(ClientLevel level, Camera camera) {
        if (cachedLevel.get() != level) {
            cachedLevel = new WeakReference<>(level);
            cachedTime = Long.MIN_VALUE;
            previousFactor = 0.0F;
            factor = 0.0F;
        }

        long time = level.getGameTime();
        if (time == cachedTime) {
            return;
        }
        cachedTime = time;

        previousFactor = factor;
        float target = shouldHide(level, camera) ? 1.0F : 0.0F;
        float step = 1.0F / FADE_TICKS;

        if (factor < target) {
            factor = Math.min(target, factor + step);
        } else if (factor > target) {
            factor = Math.max(target, factor - step);
        }
    }

    private static boolean shouldHide(ClientLevel level, Camera camera) {
        if (!ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixCaveSkyEnabled)) {
            return false;
        }

        if (!level.dimensionType().hasSkyLight()) {
            return false;
        }

        if (level.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL) {
            return false;
        }

        if (camera.getFluidInCamera() != FogType.NONE) {
            return false;
        }

        return computeEnclosed(level, camera.getBlockPosition());
    }

    private static boolean computeEnclosed(ClientLevel level, BlockPos pos) {
        if (level.getBrightness(LightLayer.SKY, pos) > 0) {
            return false;
        }

        BlockPos.MutableBlockPos sample = new BlockPos.MutableBlockPos();
        int lowestSurface = Integer.MAX_VALUE;

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

                lowestSurface = Math.min(lowestSurface, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z));
            }
        }

        return lowestSurface != Integer.MAX_VALUE && pos.getY() < lowestSurface - DEPTH_MARGIN;
    }
}
