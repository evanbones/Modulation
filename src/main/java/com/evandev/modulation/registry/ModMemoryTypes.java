package com.evandev.modulation.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.pathfinder.Path;

import java.util.*;

public class ModMemoryTypes {

    private static final Map<String, MemoryModuleType<?>> TO_REGISTER = new LinkedHashMap<>();

    public static final MemoryModuleType<GlobalPos> BEE_FLOWER_POS = register("bee_flower_pos", GlobalPos.CODEC);
    public static final MemoryModuleType<GlobalPos> BEE_HIVE_POS = register("bee_hive_pos", GlobalPos.CODEC);
    public static final MemoryModuleType<Path> BEE_LAST_PATH = register("bee_last_path");
    public static final MemoryModuleType<List<GlobalPos>> BEE_HIVE_BLACKLIST = register("bee_hive_blacklist");
    public static final MemoryModuleType<Integer> BEE_POLLINATING_COOLDOWN = register("bee_pollinating_cooldown", Codec.INT);
    public static final MemoryModuleType<Integer> BEE_POLLINATING_TICKS = register("bee_pollinating_ticks", Codec.INT);
    public static final MemoryModuleType<Integer> BEE_SUCCESSFUL_POLLINATING_TICKS = register("bee_successful_pollinating_ticks", Codec.INT);
    public static final MemoryModuleType<Integer> BEE_COOLDOWN_LOCATE_HIVE = register("bee_cooldown_locate_hive", Codec.INT);
    public static final MemoryModuleType<Integer> BEE_TRAVELLING_TICKS = register("bee_travelling_ticks", Codec.INT);
    public static final MemoryModuleType<Integer> BEE_STUCK_TICKS = register("bee_stuck_ticks", Codec.INT);
    public static final MemoryModuleType<Boolean> BEE_WANTS_HIVE = register("bee_wants_hive", Codec.BOOL);

    private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
        MemoryModuleType<U> type = new MemoryModuleType<>(Optional.of(codec));
        TO_REGISTER.put(id, type);
        return type;
    }

    private static <U> MemoryModuleType<U> register(String id) {
        MemoryModuleType<U> type = new MemoryModuleType<>(Optional.empty());
        TO_REGISTER.put(id, type);
        return type;
    }

    public static Map<String, MemoryModuleType<?>> all() {
        return Collections.unmodifiableMap(TO_REGISTER);
    }
}
