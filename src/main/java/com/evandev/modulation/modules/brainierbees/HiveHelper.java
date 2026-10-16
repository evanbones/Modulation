package com.evandev.modulation.modules.brainierbees;

import com.evandev.modulation.registry.ModMemoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public final class HiveHelper {

    public static final int RELOCATE_COOLDOWN = 200;
    private static final int MAX_BLACKLISTED_HIVES = 3;

    private HiveHelper() {
    }

    public static BlockPos getHivePos(Bee bee) {
        return bee.getBrain().getMemory(ModMemoryTypes.BEE_HIVE_POS).map(GlobalPos::pos).orElse(null);
    }

    public static boolean hasHive(Bee bee) {
        return bee.getBrain().hasMemoryValue(ModMemoryTypes.BEE_HIVE_POS);
    }

    public static void setHivePos(Bee bee, BlockPos pos) {
        bee.getBrain().setMemory(ModMemoryTypes.BEE_HIVE_POS, GlobalPos.of(bee.level().dimension(), pos));
    }

    public static void dropHive(Bee bee) {
        bee.getBrain().eraseMemory(ModMemoryTypes.BEE_HIVE_POS);
        bee.getBrain().setMemory(ModMemoryTypes.BEE_COOLDOWN_LOCATE_HIVE, RELOCATE_COOLDOWN);
    }

    public static void dropAndBlacklistHive(Bee bee) {
        BlockPos hivePos = getHivePos(bee);
        if (hivePos != null) {
            blacklist(bee, hivePos);
        }
        dropHive(bee);
    }

    public static void blacklist(Bee bee, BlockPos pos) {
        GlobalPos globalPos = GlobalPos.of(bee.level().dimension(), pos);
        List<GlobalPos> blacklist = bee.getBrain().getMemory(ModMemoryTypes.BEE_HIVE_BLACKLIST).orElse(null);
        if (blacklist == null) {
            blacklist = new ArrayList<>();
            blacklist.add(globalPos);
            bee.getBrain().setMemory(ModMemoryTypes.BEE_HIVE_BLACKLIST, blacklist);
            return;
        }
        if (!blacklist.contains(globalPos)) {
            blacklist.add(globalPos);
        }
        while (blacklist.size() > MAX_BLACKLISTED_HIVES) {
            blacklist.removeFirst();
        }
    }

    public static boolean isBlacklisted(Bee bee, BlockPos pos) {
        return bee.getBrain().getMemory(ModMemoryTypes.BEE_HIVE_BLACKLIST)
                .map(list -> list.contains(GlobalPos.of(bee.level().dimension(), pos)))
                .orElse(false);
    }

    public static boolean isHiveNearFire(Level level, Bee bee) {
        BlockPos hivePos = getHivePos(bee);
        if (hivePos == null || !level.isLoaded(hivePos)) {
            return false;
        }
        BlockEntity blockEntity = level.getBlockEntity(hivePos);
        return blockEntity instanceof BeehiveBlockEntity hive && hive.isFireNearby();
    }
}
