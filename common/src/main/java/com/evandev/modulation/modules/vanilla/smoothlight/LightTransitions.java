package com.evandev.modulation.modules.vanilla.smoothlight;

import com.evandev.modulation.modules.vanilla.VanillaVisualModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LightTransitions {
    private static final int MAX_ENTRIES = 8192;
    private static final int MIN_OPACITY = 1;

    private LightTransitions() {
    }

    public static void onBlockChanged(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (!VanillaVisualModule.SMOOTH_LIGHT.on()) return;
        if (!level.isClientSide()) return;
        if (oldState == newState || !(level instanceof LightTransitionHolder holder)) return;

        int targetEmission = newState.getLightEmission();
        int oldEmission = oldState.getLightEmission();

        ConcurrentHashMap<Long, LightTransition> existing = holder.modulation$lightTransitionsOrNull();
        long key = pos.asLong();
        LightTransition previous = existing != null ? existing.get(key) : null;

        if (previous == null && targetEmission == 0 && oldEmission == 0
                && level.getBrightness(LightLayer.BLOCK, pos) == 0) {
            return;
        }

        int targetOpacity = Math.max(MIN_OPACITY, newState.getLightBlock(level, pos));
        int fromEmission = previous != null ? previous.currentEmission() : oldEmission;
        int fromOpacity = previous != null
                ? previous.currentOpacity()
                : Math.max(MIN_OPACITY, oldState.getLightBlock(level, pos));

        if (VanillaVisualModule.SMOOTH_LIGHT_FADE_OUT_ONLY.on()) {
            if (targetEmission > fromEmission) fromEmission = targetEmission;
            if (targetOpacity < fromOpacity) fromOpacity = targetOpacity;
        }

        if (fromEmission == targetEmission && fromOpacity == targetOpacity) {
            if (previous != null) existing.remove(key);
            return;
        }

        ConcurrentHashMap<Long, LightTransition> map = holder.modulation$lightTransitions();
        if (previous == null && map.size() >= MAX_ENTRIES) return;
        map.put(key, new LightTransition(fromEmission, targetEmission, fromOpacity, targetOpacity));
    }

    public static int emission(BlockGetter level, long pos, int actual) {
        LightTransition transition = transition(level, pos);
        return transition != null ? transition.currentEmission() : actual;
    }

    public static int opacity(BlockGetter level, long pos, int actual) {
        LightTransition transition = transition(level, pos);
        return transition != null ? transition.currentOpacity() : actual;
    }

    public static boolean overridesOcclusionShape(BlockGetter level, long pos) {
        LightTransition transition = transition(level, pos);
        return transition != null && transition.opacityChanging();
    }

    public static void tick(Level level) {
        if (!(level instanceof LightTransitionHolder holder)) return;
        ConcurrentHashMap<Long, LightTransition> map = holder.modulation$lightTransitionsOrNull();
        if (map == null || map.isEmpty()) return;

        int step = VanillaVisualModule.SMOOTH_LIGHT_FADE_SPEED.get();
        LevelLightEngine lightEngine = level.getChunkSource().getLightEngine();

        Iterator<Map.Entry<Long, LightTransition>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, LightTransition> entry = iterator.next();
            LightTransition next = entry.getValue().step(step);
            if (next.done()) {
                iterator.remove();
            } else {
                entry.setValue(next);
            }
            lightEngine.checkBlock(BlockPos.of(entry.getKey()));
        }
    }

    public static void refresh(Level level, int chunkX, int chunkZ) {
        if (!(level instanceof LightTransitionHolder holder)) return;
        ConcurrentHashMap<Long, LightTransition> map = holder.modulation$lightTransitionsOrNull();
        if (map == null || map.isEmpty()) return;

        LevelLightEngine lightEngine = level.getChunkSource().getLightEngine();
        for (long key : map.keySet()) {
            BlockPos pos = BlockPos.of(key);
            if (SectionPos.blockToSectionCoord(pos.getX()) == chunkX
                    && SectionPos.blockToSectionCoord(pos.getZ()) == chunkZ) {
                lightEngine.checkBlock(pos);
            }
        }
    }

    @Nullable
    private static LightTransition transition(BlockGetter level, long pos) {
        if (!(level instanceof LightTransitionHolder holder)) return null;
        ConcurrentHashMap<Long, LightTransition> map = holder.modulation$lightTransitionsOrNull();
        return map != null ? map.get(pos) : null;
    }
}
